package io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isTrue
import com.blinnnk.util.getDeviceBrand
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.value.DeviceName
import io.goldstone.blockchain.crypto.ethereum.Address
import io.goldstone.blockchain.crypto.ethereum.isValid
import io.goldstone.blockchain.crypto.keystore.updatePassword
import io.goldstone.blockchain.crypto.keystore.updatePasswordByWalletID
import io.goldstone.blockchain.crypto.keystore.verifyCurrentWalletKeyStorePassword
import io.goldstone.blockchain.crypto.multichain.WalletType
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.view.PasswordSettingsFragment
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
		@UiThread callback: (GoldStoneError) -> Unit
	) {
		if (oldPassword.isEmpty()) callback(AccountError.EmptyRepeatPassword)
		else CreateWalletPresenter.checkInputValue(
			"",
			newPassword,
			repeatPassword,
			true,
			callback
		) { password, _ ->
			WalletTable.getWalletType { type, wallet ->
				fragment.context?.verifyCurrentWalletKeyStorePassword(oldPassword, wallet.id) { isCorrect ->
					if (isCorrect) updatePassword(
						oldPassword,
						password,
						type,
						wallet,
						passwordHint
					) else GoldStoneAPI.context.runOnUiThread {
						callback(AccountError.WrongPassword)
					}
				}
			}
		}
	}

	private fun updatePassword(
		oldPassword: String,
		password: String,
		type: WalletType,
		wallet: WalletTable,
		passwordHint: String
	) {
		when {
			// 删除多链钱包下的所有地址对应的数据
			type.isBIP44() -> {
				object : ConcurrentAsyncCombine() {
					override var asyncCount = 4
					override fun concurrentJobs() {
						listOf(
							wallet.ethAddresses,
							wallet.etcAddresses,
							wallet.bchAddresses,
							wallet.ltcAddresses,
							wallet.btcAddresses,
							wallet.btcSeriesTestAddresses,
							wallet.eosAddresses
						).forEach { addresses ->
							addresses.forEach { pair ->
								updateKeystorePassword(
									pair.address,
									oldPassword,
									password,
									passwordHint,
									// 不是合规的 `ETH` 地址就是 `BTC` 系列地址
									!Address(pair.address).isValid()
								) {
									completeMark()
								}
							}
						}
					}

					override fun mergeCallBack() = autoBack()
				}.start()
			}
			// 新的钱包结构只存在 Bip44 多链和 单私钥多链两种情况
			else -> updateKeystorePasswordByWalletID(
				wallet.id,
				oldPassword,
				password,
				passwordHint
			) {
				autoBack()
			}
		}
	}

	private fun updateKeystorePassword(
		address: String,
		oldPassword: String,
		newPassword: String,
		passwordHint: String,
		isBTCSeriesWallet: Boolean,
		callback: (AccountError) -> Unit
	) {
		// ToDO 低端机型解 `Keystore` 会耗时很久,等自定义的 `Alert` 完成后应当友好提示
		fragment.context?.updatePassword(
			address,
			oldPassword,
			newPassword,
			isBTCSeriesWallet
		) { _, error ->
			// Update User Password Hint
			if (error.isNone()) WalletTable.updateHint(passwordHint)
			callback(error)
		}
	}

	private fun updateKeystorePasswordByWalletID(
		walletID: Int,
		oldPassword: String,
		newPassword: String,
		passwordHint: String,
		callback: (AccountError) -> Unit
	) {
		fragment.context?.updatePasswordByWalletID(
			walletID,
			oldPassword,
			newPassword
		) {
			// Update User Password Hint
			passwordHint.isNotEmpty() isTrue {
				WalletTable.updateHint(passwordHint)
			}
			callback(it)
		}
	}

	@UiThread
	private fun autoBack() {
		fragment.getParentFragment<WalletSettingsFragment> {
			// `VIVO` 手机显示 `toast` 会出错
			if (!getDeviceBrand().contains(DeviceName.vivo, true)) activity?.toast(CommonText.succeed)
			presenter.showTargetFragmentByTitle(WalletSettingsText.walletSettings)
		}
	}
}