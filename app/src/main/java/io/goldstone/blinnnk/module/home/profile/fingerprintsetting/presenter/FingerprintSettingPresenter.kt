package io.goldstone.blinnnk.module.home.profile.fingerprintsetting.presenter

import com.blinnnk.extension.isNotNull
import io.goldstone.blinnnk.GoldStoneApp
import io.goldstone.blinnnk.common.error.AccountError
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.common.thread.launchDefault
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.crypto.keystore.getBigIntegerPrivateKeyByWalletID
import io.goldstone.blinnnk.crypto.keystore.verifyKeystorePasswordByWalletID
import io.goldstone.blinnnk.crypto.utils.JavaKeystoreUtil
import io.goldstone.blinnnk.crypto.utils.KeystoreInfo
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blinnnk.module.home.profile.fingerprintsetting.contract.FingerprintSettingContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * @author KaySaith
 * @date  2018/12/18
 */
class FingerprintSettingPresenter : FingerprintSettingContract.GSPresenter {

	override fun start() {}
	override fun getSecret(password: String, hold: (secret: String?, error: AccountError) -> Unit) {
		launchDefault {
			if (SharedWallet.getCurrentWalletType().isBIP44()) {
				GoldStoneApp.appContext.verifyKeystorePasswordByWalletID(
					password,
					SharedWallet.getCurrentWalletID()
				) { isCorrect ->
					if (isCorrect) {
						val mnemonic =
							JavaKeystoreUtil(KeystoreInfo.isMnemonic()).decryptData(WalletTable.dao.getEncryptMnemonic()!!)
						hold(mnemonic, AccountError.None)
					} else hold(null, AccountError.None)
				}
			} else {
				GoldStoneApp.appContext.getBigIntegerPrivateKeyByWalletID(
					password,
					SharedWallet.getCurrentWalletID()
				) { privateKey, error ->
					if (privateKey.isNotNull() && error.isNone()) {
						hold(privateKey.toString(16), error)
					} else hold(null, AccountError.WrongPassword)
				}
			}
		}
	}

	override fun turnOffFingerprintPayment(callback: () -> Unit) {
		GlobalScope.launch(Dispatchers.Default) {
			WalletTable.dao.turnOffFingerprint()
			SharedWallet.updateFingerprint(false)
			launchUI(callback)
		}
	}

	override fun updateFingerEncryptKey(encryptKey: String, callback: () -> Unit) {
		GlobalScope.launch(Dispatchers.Default) {
			WalletTable.dao.updateFingerEncryptKey(encryptKey)
			launchUI(callback)
		}
	}
}