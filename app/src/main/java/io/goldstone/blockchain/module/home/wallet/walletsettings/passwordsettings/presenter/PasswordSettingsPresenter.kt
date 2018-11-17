package io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.presenter

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isTrue
import com.blinnnk.util.ConcurrentJobs
import com.blinnnk.util.getDeviceBrand
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.value.DeviceName
import io.goldstone.blockchain.crypto.ethereum.Address
import io.goldstone.blockchain.crypto.ethereum.isValid
import io.goldstone.blockchain.crypto.keystore.updatePassword
import io.goldstone.blockchain.crypto.keystore.updatePasswordByWalletID
import io.goldstone.blockchain.crypto.keystore.verifyCurrentWalletKeyStorePassword
import io.goldstone.blockchain.crypto.multichain.WalletType
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.view.PasswordSettingsFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast

/**
 * @date 26/03/2018 9:13 PM
 * @author KaySaith
 */
class PasswordSettingsPresenter(
	override val fragment: PasswordSettingsFragment
) : BasePresenter<PasswordSettingsFragment>() {

	@WorkerThread
	fun checkOrUpdatePassword(
		oldPassword: String,
		newPassword: String,
		repeatPassword: String,
		passwordHint: String,
		callback: (GoldStoneError) -> Unit
	) = GlobalScope.launch(Dispatchers.Default) {
		if (oldPassword.isEmpty()) callback(AccountError.EmptyRepeatPassword)
		else CreateWalletPresenter.checkInputValue(
			"",
			newPassword,
			repeatPassword,
			true
		) { password, _, error ->
			if (password.isNullOrEmpty() || error.hasError()) callback(error)
			else {
				val wallet = WalletTable.dao.findWhichIsUsing(true) ?: return@checkInputValue
				fragment.context?.verifyCurrentWalletKeyStorePassword(oldPassword, wallet.id) { isCorrect ->
					if (isCorrect) updatePassword(
						oldPassword,
						password,
						wallet.getWalletType(),
						wallet,
						passwordHint
					) else callback(AccountError.WrongPassword)
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
				val allAddresses = listOf(
					wallet.ethAddresses,
					wallet.etcAddresses,
					wallet.bchAddresses,
					wallet.ltcAddresses,
					wallet.btcAddresses,
					wallet.btcSeriesTestAddresses,
					wallet.eosAddresses
				)
				object : ConcurrentJobs() {
					override var asyncCount: Int = allAddresses.size
					override fun doChildJob(index: Int) {
						allAddresses[index].forEach { pair ->
							fragment.context?.updatePassword(
								pair.address,
								oldPassword,
								password,
								!Address(pair.address).isValid()
							) { _, _ ->
								completeMark()
							}
						}
					}

					override fun mergeCallBack() {
						WalletTable.dao.updateHint(passwordHint)
						launchUI { autoBack() }
					}
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
	private fun autoBack() = launchUI {
		fragment.getParentFragment<WalletSettingsFragment> {
			// `VIVO` 手机显示 `toast` 会出错
			if (!getDeviceBrand().contains(DeviceName.vivo, true)) activity?.toast(CommonText.succeed)
			presenter.showTargetFragmentByTitle(WalletSettingsText.walletSettings)
		}
	}
}