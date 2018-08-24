package io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.presenter

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isTrue
import io.goldstone.blockchain.common.Language.CreateWalletText
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WalletType
import io.goldstone.blockchain.crypto.Address
import io.goldstone.blockchain.crypto.isValid
import io.goldstone.blockchain.crypto.updatePassword
import io.goldstone.blockchain.crypto.verifyCurrentWalletKeyStorePassword
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.view.PasswordSettingsFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.presenter.AddressManagerPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast

/**
 * @date 26/03/2018 9:13 PM
 * @author KaySaith
 */
class PasswordSettingsPresenter(
	override val fragment: PasswordSettingsFragment
) : BasePresenter<PasswordSettingsFragment>() {

	fun checkInputValueThenUpdatePassword(
		oldPassword: String,
		newPassword: String,
		repeatPassword: String,
		passwordHint: String,
		callback: () -> Unit
	) {
		if (oldPassword.isEmpty()) {
			fragment.context.alert(CreateWalletText.emptyRepeatPasswordAlert)
			return
		}
		CreateWalletPresenter.checkInputValue(
			"",
			newPassword,
			repeatPassword,
			true,
			fragment.context,
			callback // error callback
		) { password, _ ->
			fragment.context?.verifyCurrentWalletKeyStorePassword(oldPassword) { isCorrect ->
				if (isCorrect) updatePassword(oldPassword, password, passwordHint)
				else {
					callback()
					fragment.context?.runOnUiThread {
						alert(CommonText.wrongPassword)
					}
				}
			}
		}
	}

	private fun updatePassword(
		oldPassword: String,
		password: String,
		passwordHint: String
	) {
		WalletTable.getCurrentWallet {
			when (Config.getCurrentWalletType()) {
				// 删除多链钱包下的所有地址对应的数据
				WalletType.MultiChain.content -> {
					object : ConcurrentAsyncCombine() {
						override var asyncCount = 4

						override fun concurrentJobs() {
							listOf(
								ethAddresses,
								etcAddresses,
								bchAddresses,
								ltcAddresses,
								btcAddresses,
								btcSeriesTestAddresses
							).forEach { addresses ->
								AddressManagerPresenter.convertToChildAddresses(addresses).forEach {
									updateKeystorePassword(
										it.first,
										oldPassword,
										password,
										passwordHint,
										!Address(it.first).isValid(), // 不是合规的 `ETH` 地址就是 `BTC` 系列地址
										false
									) {
										completeMark()
									}
								}
							}
						}

						override fun mergeCallBack() {
							autoBack()
						}
					}.start()
				}
				// 删除 `BTCTest` 包下的所有地址对应的数据
				WalletType.BTCTestOnly.content -> WalletTable.getCurrentWallet {
					updateKeystorePassword(
						currentBTCSeriesTestAddress,
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
				WalletType.LTCOnly.content -> WalletTable.getCurrentWallet {
					updateKeystorePassword(
						currentLTCAddress,
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
				WalletType.BCHOnly.content -> WalletTable.getCurrentWallet {
					updateKeystorePassword(
						currentBCHAddress,
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
				WalletType.BTCOnly.content -> WalletTable.getCurrentWallet {
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
				WalletType.ETHERCAndETCOnly.content -> WalletTable.getCurrentWallet {
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

	private fun updateKeystorePassword(
		address: String,
		oldPassword: String,
		newPassword: String,
		passwordHint: String,
		isBTCSeriesWallet: Boolean,
		isSingleChainWallet: Boolean,
		callback: () -> Unit
	) {
		// ToDO 低端机型解 `Keystore` 会耗时很久,等自定义的 `Alert` 完成后应当友好提示
		fragment.context?.updatePassword(
			address,
			oldPassword,
			newPassword,
			isBTCSeriesWallet,
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
			callback()
		}
	}

	private fun autoBack() {
		fragment.getParentFragment<WalletSettingsFragment> {
			presenter.showTargetFragmentByTitle(WalletSettingsText.walletSettings)
			fragment.activity?.toast(CommonText.succeed)
		}
	}
}