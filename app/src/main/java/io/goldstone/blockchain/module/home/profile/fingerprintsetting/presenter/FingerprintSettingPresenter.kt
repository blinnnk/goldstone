package io.goldstone.blockchain.module.home.profile.fingerprintsetting.presenter

import com.blinnnk.extension.isNotNull
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.crypto.keystore.getBigIntegerPrivateKeyByWalletID
import io.goldstone.blockchain.crypto.keystore.verifyKeystorePasswordByWalletID
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
import io.goldstone.blockchain.crypto.utils.KeystoreInfo
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.profile.fingerprintsetting.contract.FingerprintSettingContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * @author KaySaith
 * @date  2018/12/18
 */
class FingerprintSettingPresenter : FingerprintSettingContract.GSPresenter {
	override fun start() {

	}

	override fun getSecret(password: String, hold: (secret: String?, error: AccountError) -> Unit) {
		GlobalScope.launch(Dispatchers.Default) {
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
					} else hold(null, AccountError.None)
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