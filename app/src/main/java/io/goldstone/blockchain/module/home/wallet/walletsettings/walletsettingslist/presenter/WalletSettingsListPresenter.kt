package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.presenter

import android.support.v4.app.Fragment
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.jump
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.common.value.WalletType
import io.goldstone.blockchain.crypto.deleteAccount
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.crypto.verifyCurrentWalletKeyStorePassword
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.model.TokenBalanceTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.presenter.AddressManagerPresneter
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
			title.equals(WalletSettingsText.delete, true) -> deleteWallet()
			title.equals(WalletSettingsText.balance, true) -> return
			
			else -> {
				fragment.getParentFragment<WalletSettingsFragment>()?.apply {
					headerTitle = title
					presenter.showTargetFragmentByTitle(title)
				}
			}
		}
	}
	
	/** 分别从数据库和 `Keystore` 文件内删除掉用户钱包的所有数据 */
	private fun deleteWallet() {
		fragment.context?.showAlertView(
			WalletSettingsText.deleteInfoTitle,
			WalletSettingsText.deleteInfoSubtitle,
			!Config.getCurrentIsWatchOnlyOrNot()
		) {
			val password = it?.text.toString()
			fragment.context?.verifyCurrentWalletKeyStorePassword(password) {
				if (it) fragment.deleteWalletData(password)
				else fragment.context.alert(CommonText.wrongPassword)
			}
		}
	}
	
	private fun WalletSettingsListFragment.deleteWalletData(password: String) {
		getMainActivity()?.showLoadingView()
		// get current wallet address
		if (Config.getCurrentIsWatchOnlyOrNot()) {
			WalletTable.getWatchOnlyWallet {
				deleteWatchOnlyWallet(it.orEmpty())
			}
		} else {
			WalletTable.getCurrentWallet {
				when (Config.getCurrentWalletType()) {
				// 删除多链钱包下的所有地址对应的数据
					WalletType.MultiChain.content -> {
						object : ConcurrentAsyncCombine() {
							override var asyncCount = 4
							
							override fun concurrentJobs() {
								AddressManagerPresneter.convertToChildAddresses(ethAddresses).forEach {
									deleteRoutineWallet(
										it.first,
										password,
										false,
										false,
										true
									) {
										completeMark()
									}
								}
								AddressManagerPresneter.convertToChildAddresses(etcAddresses).forEach {
									deleteRoutineWallet(
										it.first,
										password,
										false,
										false,
										true
									) {
										completeMark()
									}
								}
								AddressManagerPresneter.convertToChildAddresses(btcTestAddresses).forEach {
									deleteRoutineWallet(
										it.first,
										password,
										true,
										false,
										true
									) {
										completeMark()
									}
								}
								AddressManagerPresneter.convertToChildAddresses(btcAddresses).forEach {
									deleteRoutineWallet(
										it.first,
										password,
										true,
										false,
										true
									) {
										completeMark()
									}
								}
							}
							
							override fun mergeCallBack() {
								// delete wallet record in `walletTable`
								WalletTable.deleteCurrentWallet {
									// 删除 `push` 监听包地址不再监听用户删除的钱包地址
									XinGePushReceiver.registerAddressesForPush(true)
									activity?.jump<SplashActivity>()
								}
							}
						}.start()
					}
				// 删除 `BTCTest` 包下的所有地址对应的数据
					WalletType.BTCTestOnly.content -> WalletTable.getCurrentWallet {
						deleteRoutineWallet(
							currentBTCTestAddress,
							password,
							true,
							true
						) {
							activity?.jump<SplashActivity>()
						}
					}
				// 删除 `BTCOnly` 包下的所有地址对应的数据
					WalletType.BTCOnly.content -> WalletTable.getCurrentWallet {
						deleteRoutineWallet(
							currentBTCAddress,
							password,
							true,
							true
						) {
							activity?.jump<SplashActivity>()
						}
					}
				// 删除 `ETHERCAndETCOnly` 包下的所有地址对应的数据
					WalletType.ETHERCAndETCOnly.content -> WalletTable.getCurrentWallet {
						deleteRoutineWallet(
							currentETHAndERCAddress,
							password,
							false,
							true
						) {
							activity?.jump<SplashActivity>()
						}
					}
				}
			}
		}
	}
	
	private fun Fragment.deleteRoutineWallet(
		address: String,
		password: String,
		isBTCAccount: Boolean,
		isSingleChainWallet: Boolean,
		justDeleteData: Boolean = false,
		callback: () -> Unit
	) {
		// delete `keystore` file
		context?.deleteAccount(
			address,
			password,
			isBTCAccount,
			isSingleChainWallet
		) {
			it isFalse {
				fragment.context?.alert(CommonText.wrongPassword)
				getMainActivity()?.removeLoadingView()
				return@deleteAccount
			}
			// delete all records of this `address` in `myTokenTable`
			MyTokenTable.deleteByAddress(address) {
				TransactionTable.deleteByAddress(address) {
					TokenBalanceTable.deleteByAddress(address) {
						if (justDeleteData) {
							callback()
						} else {
							// delete wallet record in `walletTable`
							WalletTable.deleteCurrentWallet {
								// 删除 `push` 监听包地址不再监听用户删除的钱包地址
								XinGePushReceiver.registerAddressesForPush(true)
								callback()
							}
						}
					}
				}
			}
		}
	}
	
	private fun deleteWatchOnlyWallet(address: String) {
		MyTokenTable.deleteByAddress(address) {
			TransactionTable.deleteByAddress(address) {
				TokenBalanceTable.deleteByAddress(address) {
					WalletTable.deleteCurrentWallet {
						// 删除 `push` 监听包地址不再监听用户删除的钱包地址
						XinGePushReceiver.registerAddressesForPush(true)
						GoldStoneAPI.context.runOnUiThread {
							fragment.getMainActivity()?.removeLoadingView()
							fragment.activity?.jump<SplashActivity>()
						}
					}
				}
			}
		}
	}
}