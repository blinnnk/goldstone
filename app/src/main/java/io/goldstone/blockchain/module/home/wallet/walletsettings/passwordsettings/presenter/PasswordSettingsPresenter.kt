package io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.presenter

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isTrue
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.common.value.WalletType
import io.goldstone.blockchain.crypto.updatePassword
import io.goldstone.blockchain.crypto.verifyCurrentWalletKeyStorePassword
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.view.PasswordSettingsFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.presenter.AddressManagerPresneter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.support.v4.toast

/**
 * @date 26/03/2018 9:13 PM
 * @author KaySaith
 */
class PasswordSettingsPresenter(
	override val fragment: PasswordSettingsFragment
) : BasePresenter<PasswordSettingsFragment>() {
	
	fun verifyOldPassword(oldPassword: String, callback: (Boolean) -> Unit) {
		fragment.context?.apply {
			if (oldPassword.isEmpty()) {
				alert(CreateWalletText.emptyRepeatPasswordAlert)
				callback(false)
				return
			} else {
				verifyCurrentWalletKeyStorePassword(oldPassword, callback)
			}
		}
	}
	
	fun updatePassword(
		oldPassword: String,
		newPassword: String,
		repeatPassword: String,
		passwordHint: String,
		callback: () -> Unit
	) {
		CreateWalletPresenter.checkInputValue(
			"",
			newPassword,
			repeatPassword,
			true,
			fragment.context,
			callback // error callback
		) { password, _ ->
			WalletTable.getWalletType {
				WalletTable.getCurrentWallet {
					when (it) {
					// 删除多链钱包下的所有地址对应的数据
						WalletType.MultiChain -> {
							object : ConcurrentAsyncCombine() {
								override var asyncCount = 4
								
								override fun concurrentJobs() {
									AddressManagerPresneter.convertToChildAddresses(ethAddresses).forEach {
										updateKeystorePassword(
											it.first,
											oldPassword,
											password,
											passwordHint,
											false,
											false
										) {
											completeMark()
										}
									}
									AddressManagerPresneter.convertToChildAddresses(etcAddresses).forEach {
										updateKeystorePassword(
											it.first,
											oldPassword,
											password,
											passwordHint,
											false,
											false
										) {
											completeMark()
										}
									}
									AddressManagerPresneter.convertToChildAddresses(btcTestAddresses).forEach {
										updateKeystorePassword(
											it.first,
											oldPassword,
											password,
											passwordHint,
											true,
											false
										) {
											completeMark()
										}
									}
									AddressManagerPresneter.convertToChildAddresses(btcAddresses).forEach {
										updateKeystorePassword(
											it.first,
											oldPassword,
											password,
											passwordHint,
											true,
											false
										) {
											completeMark()
										}
									}
								}
								
								override fun mergeCallBack() {
									autoBack()
								}
							}.start()
						}
					// 删除 `BTCTest` 包下的所有地址对应的数据
						WalletType.BTCTestOnly -> WalletTable.getCurrentWallet {
							updateKeystorePassword(
								currentBTCTestAddress,
								oldPassword,
								password,
								passwordHint,
								true,
								true
							) {
								autoBack()
							}
						}
					// 删除 `BTCOnly` 包下的所有地址对应的数据
						WalletType.BTCOnly -> WalletTable.getCurrentWallet {
							updateKeystorePassword(
								currentBTCAddress,
								oldPassword,
								password,
								passwordHint,
								true,
								true
							) {
								autoBack()
							}
						}
					// 删除 `ETHERCAndETCOnly` 包下的所有地址对应的数据
						WalletType.ETHERCAndETCOnly -> WalletTable.getCurrentWallet {
							updateKeystorePassword(
								currentETHAndERCAddress,
								oldPassword,
								password,
								passwordHint,
								false,
								true
							) {
								autoBack()
							}
						}
					}
				}
			}
		}
	}
	
	private fun updateKeystorePassword(
		address: String,
		oldPassword: String,
		newPassword: String,
		passwordHint: String,
		isBTCWallet: Boolean,
		isSingleChainWallet: Boolean,
		callback: () -> Unit
	) {
		// ToDO 低端机型解 `Keystore` 会耗时很久,等自定义的 `Alert` 完成后应当友好提示
		fragment.context?.updatePassword(
			address,
			oldPassword,
			newPassword,
			isBTCWallet,
			isSingleChainWallet,
			{
				// error callback
				callback()
			}
		) {
			// Update User Password Hint
			passwordHint.isNotEmpty() isTrue {
				WalletTable.updateHint(passwordHint)
			}
			
			fragment.toast(CommonText.succeed)
			callback()
		}
	}
	
	private fun autoBack() {
		fragment.getParentFragment<WalletSettingsFragment> {
			presenter.showTargetFragmentByTitle(WalletSettingsText.walletSettings)
		}
	}
}