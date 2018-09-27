package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.presenter

import android.app.Activity
import android.content.Context
import com.blinnnk.extension.jump
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.keystore.deleteAccount
import io.goldstone.blockchain.crypto.keystore.verifyCurrentWalletKeyStorePassword
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.isStoredInKeyStoreByAddress
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.presenter.AddressManagerPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.model.WalletSettingsListModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.view.WalletSettingsListFragment
import org.jetbrains.anko.runOnUiThread

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
		fragment.activity?.apply { SoftKeyboard.hide(this) }
	}

	override fun updateData() {
		val balanceText =
			Config.getCurrentBalance().formatCurrency() + " (${Config.getCurrencyCode()})"
		WalletTable.getCurrentWallet {
			arrayListOf(
				WalletSettingsListModel(WalletSettingsText.viewAddresses),
				WalletSettingsListModel(WalletSettingsText.balance, balanceText),
				WalletSettingsListModel(WalletSettingsText.walletName, Config.getCurrentName()),
				WalletSettingsListModel(WalletSettingsText.hint, "******"),
				WalletSettingsListModel(WalletSettingsText.passwordSettings),
				WalletSettingsListModel(
					WalletSettingsText.backUpMnemonic,
					WalletSettingsText.safeAttention
				),
				WalletSettingsListModel(WalletSettingsText.delete)
			).let {
				// 如果已经备份了助记词就不再显示提示条目
				if (hasBackUpMnemonic) {
					it.removeAt(it.lastIndex - 1)
				}
				fragment.asyncData = it
			}
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
		showAlertView(
			WalletSettingsText.deleteInfoTitle,
			WalletSettingsText.deleteInfoSubtitle,
			!Config.isWatchOnlyWallet()
		) { passwordInput ->
			if (Config.isWatchOnlyWallet()) WalletTable.getWatchOnlyWallet {
				deleteWatchOnlyWallet(first, second)
			} else {
				val password = passwordInput?.text.toString()
				WalletTable.getCurrentWallet(false) {
					verifyCurrentWalletKeyStorePassword(password, id) { isCorrect ->
						if (isCorrect) {
							runOnUiThread { getMainActivity()?.showLoadingView() }
							fragment.context?.deleteWalletData(this, password)
						} else GoldStoneAPI.context.runOnUiThread {
							fragment.context.alert(CommonText.wrongPassword)
						}
					}
				}
			}
		}
	}

	private fun Context.deleteWalletData(wallet: WalletTable, password: String) {
		// get current wallet address
		wallet.apply {
			val addresses =
				getCurrentAllAddressAndChainID()
			object : ConcurrentAsyncCombine() {
				override var asyncCount = addresses.size
				override fun concurrentJobs() {
					addresses.forEach { account ->
						AddressManagerPresenter.convertToChildAddresses(account.first).forEach {
							deleteRoutineWallet(
								it.first,
								password,
								account.second,
								false,
								true
							) {
								completeMark()
							}
						}
					}
				}

				override fun mergeCallBack() {
					WalletTable.deleteCurrentWallet { wallet ->
						// 删除 `push` 监听包地址不再监听用户删除的钱包地址
						XinGePushReceiver.registerAddressesForPush(wallet, true)
						(this@deleteWalletData as? Activity)?.jump<SplashActivity>()
					}
				}
			}.start()
		}
	}

	private fun Context.deleteRoutineWallet(
		address: String,
		password: String,
		chainType: ChainType,
		isSingleChainWallet: Boolean,
		justDeleteData: Boolean = false,
		callback: () -> Unit
	) {
		// delete `keystore` file
		deleteAccount(
			address,
			password,
			chainType.isStoredInKeyStoreByAddress(),
			isSingleChainWallet
		) { error ->
			if (error.isNone()) {
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
				if (justDeleteData) callback()
				else WalletTable.deleteCurrentWallet { wallet ->
					// 删除 `push` 监听包地址不再监听用户删除的钱包地址
					XinGePushReceiver.registerAddressesForPush(wallet, true)
					callback()
				}
			} else {
				fragment.context.alert(error.message)
				getMainActivity()?.removeLoadingView()
			}
		}
	}

	private fun deleteWatchOnlyWallet(address: String, chainType: ChainType) {
		GoldStoneDataBase.database.myTokenDao().deleteAllByAddress(address)
		// 删除 以太坊 类型的转账记录
		GoldStoneDataBase.database.transactionDao().deleteRecordAddressData(address)
		BTCSeriesTransactionTable.deleteByAddress(address, chainType)
		// 删除余额记录
		GoldStoneDataBase.database.tokenBalanceDao().deleteTokenBalanceByAddress(address)
		WalletTable.deleteCurrentWallet { wallet ->
			// 删除 `push` 监听包地址不再监听用户删除的钱包地址
			XinGePushReceiver.registerAddressesForPush(wallet, true)
			GoldStoneAPI.context.runOnUiThread {
				fragment.getMainActivity()?.removeLoadingView()
				fragment.activity?.jump<SplashActivity>()
			}
		}
	}
}