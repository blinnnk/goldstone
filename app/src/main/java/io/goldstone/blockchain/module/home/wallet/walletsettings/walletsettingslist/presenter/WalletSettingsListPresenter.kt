package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.presenter

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.jump
import com.blinnnk.util.ConcurrentAsyncCombine
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.component.overlay.Dashboard
import io.goldstone.blockchain.common.component.overlay.LoadingView
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.AddressUtils
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.crypto.keystore.deleteAccount
import io.goldstone.blockchain.crypto.keystore.deleteWalletByWalletID
import io.goldstone.blockchain.crypto.keystore.verifyCurrentWalletKeyStorePassword
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.isStoredInKeyStoreByAddress
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.Bip44Address
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.model.WalletSettingsListModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.view.WalletSettingsListAdapter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.view.WalletSettingsListFragment
import kotlinx.coroutines.Dispatchers

/**
 * @date 25/03/2018 10:15 PM
 * @author KaySaith
 */
class WalletSettingsListPresenter(
	override val fragment: WalletSettingsListFragment
) : BaseRecyclerPresenter<WalletSettingsListFragment, WalletSettingsListModel>() {

	override fun onFragmentCreateView() {
		super.onFragmentCreateView()
		// 如果键盘在显示那么销毁键盘
		fragment.activity?.apply {
			SoftKeyboard.hide(this)
		}
		// 每次进入这个界面更新一次钱包地址总数
		fragment.getParentFragment<WalletSettingsFragment>()
			?.presenter?.showCurrentWalletInfo()
	}

	override fun updateData() {
		val balanceText =
			SharedWallet.getCurrentBalance().formatCurrency() + " (${SharedWallet.getCurrencyCode()})"
		fragment.asyncData = arrayListOf()
		arrayListOf(
			WalletSettingsListModel(WalletSettingsText.viewAddresses),
			WalletSettingsListModel(WalletSettingsText.balance, balanceText),
			WalletSettingsListModel(WalletSettingsText.walletName, SharedWallet.getCurrentName()),
			WalletSettingsListModel(WalletSettingsText.hint, "******"),
			WalletSettingsListModel(WalletSettingsText.passwordSettings),
			WalletSettingsListModel(
				WalletSettingsText.backUpMnemonic,
				WalletSettingsText.safeAttention
			),
			WalletSettingsListModel(WalletSettingsText.delete)
		).let {
			// 如果已经备份了助记词就不再显示提示条目
			if (SharedWallet.hasBackUpMnemonic()) it.removeAt(it.lastIndex - 1)
			diffAndUpdateSingleCellAdapterData<WalletSettingsListAdapter>(it)
		}
	}

	fun showTargetFragment(title: String) {
		when {
			title.equals(WalletSettingsText.delete, true) -> fragment.context?.deleteWallet()
			title.equals(WalletSettingsText.balance, true) -> return
			else -> fragment.getParentFragment<WalletSettingsFragment>()?.apply {
				headerTitle = title
				presenter.showTargetFragmentByTitle(title)
			}
		}
	}

	/** 分别从数据库和 `Keystore` 文件内删除掉用户钱包的所有数据 */
	private fun Context.deleteWallet() {
		Dashboard(this) {
			showAlertView(
				WalletSettingsText.deleteInfoTitle,
				WalletSettingsText.deleteInfoSubtitle,
				!SharedWallet.isWatchOnlyWallet(),
				{}
			) { passwordInput ->
				val loadingView = LoadingView(this@deleteWallet)
				loadingView.show()
				if (SharedWallet.isWatchOnlyWallet()) deleteWatchOnlyWallet()
				else {
					val password = passwordInput?.text.toString()
					WalletTable.getCurrent(Dispatchers.Default) {
						val type = getWalletType()
						verifyCurrentWalletKeyStorePassword(password, id) { isCorrect ->
							if (isCorrect) {
								if (type.isBIP44())
									deleteWalletData(id, getCurrentAllBip44Address(), password)
								else deleteRootKeyWallet(id, getCurrentBip44Addresses(), password)
							} else launchUI {
								loadingView.remove()
								fragment.safeShowError(Throwable(CommonText.wrongPassword))
							}
						}
					}
				}
			}
		}
	}

	private fun Context.deleteWalletData(willDeleteWalletID: Int, addresses: List<Bip44Address>, password: String) {
		object : ConcurrentAsyncCombine() {
			override var asyncCount = addresses.size
			override fun doChildTask(index: Int) {
				deleteRoutineWallet(
					willDeleteWalletID,
					addresses[index].address,
					password,
					addresses[index].getChainType()
				) {
					completeMark()
				}
			}

			override fun mergeCallBack() {
				WalletTable.deleteCurrentWallet { wallet ->
					// 删除 `push` 监听包地址不再监听用户删除的钱包地址
					XinGePushReceiver.registerAddressesForPush(wallet, true)
					getMainActivity()?.jump<SplashActivity>()
				}
			}
		}.start()
	}

	private fun Context.deleteRootKeyWallet(
		walletID: Int,
		addresses: List<Bip44Address>,
		password: String
	) {
		object : ConcurrentAsyncCombine() {
			override var asyncCount = addresses.size
			override val completeInUIThread: Boolean = false
			override fun doChildTask(index: Int) {
				deleteAllLocalDataByAddress(addresses[index].address, addresses[index].getChainType())
				completeMark()
			}

			override fun mergeCallBack() {
				deleteWalletByWalletID(walletID, password) {
					if (it.isNone()) WalletTable.deleteCurrentWallet { wallet ->
						// 删除 `push` 监听包地址不再监听用户删除的钱包地址
						XinGePushReceiver.registerAddressesForPush(wallet, true)
						launchUI { getMainActivity()?.jump<SplashActivity>() }
					} else launchUI { alert(it.message) }
				}
			}
		}.start()
	}

	private fun Context.deleteRoutineWallet(
		willDeleteWalletID: Int,
		address: String,
		password: String,
		chainType: ChainType,
		callback: (AccountError) -> Unit
	) {
		// delete `keystore` file
		deleteAccount(
			address,
			password,
			chainType.isStoredInKeyStoreByAddress()
		) { error ->
			if (error.isNone()) {
				// 检测钱包里是否存在着这个地址的其他钱包, 如果存在就不删除数据只删除 Wallet
				AddressUtils.hasExistAddress(listOf(address), willDeleteWalletID) {
					if (it) callback(AccountError.None)
					else deleteAllLocalDataByAddress(address, chainType)
				}
				callback(AccountError.None)
			} else callback(error)
		}
	}

	@WorkerThread
	private fun deleteAllLocalDataByAddress(address: String, chainType: ChainType) {
		GoldStoneDataBase.database.myTokenDao().deleteAllByAddress(address)
		// 删除 以太坊 类型的转账记录
		GoldStoneDataBase.database.transactionDao().deleteRecordAddressData(address)
		// 删除 BTC 类型的转账记录
		BTCSeriesTransactionTable.deleteByAddress(address, chainType)
		// 删除 EOS 类型的转账记录
		GoldStoneDataBase.database.eosTransactionDao().deleteDataByRecordAddress(address)
		// 删除 EOS Account Info 类型的记录
		GoldStoneDataBase.database.eosAccountDao().deleteByKey(address)
		// 删除余额记录
		GoldStoneDataBase.database.tokenBalanceDao().deleteTokenBalanceByAddress(address)
	}

	private fun deleteWatchOnlyWallet() {
		WalletTable.deleteCurrentWallet { wallet ->
			val bip44Address = wallet!!.getCurrentBip44Addresses().firstOrNull() ?: Bip44Address()
			// 检测钱包里是否存在着这个地址的其他钱包, 如果存在就不删除数据只删除 Wallet
			AddressUtils.hasExistAddress(listOf(bip44Address.address)) {
				if (!it) deleteAllLocalDataByAddress(bip44Address.address, bip44Address.getChainType())
			}
			// 删除 `push` 监听包地址不再监听用户删除的钱包地址
			XinGePushReceiver.registerAddressesForPush(wallet, true)
			launchUI {
				fragment.activity?.jump<SplashActivity>()
			}
		}
	}
}