package io.goldstone.blockchain.module.home.profile.fingerprintsetting.contract

import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import javax.security.auth.callback.Callback


/**
 * @author KaySaith
 * @date  2018/12/18
 */
interface FingerprintSettingContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {

	}

	interface GSPresenter : GoldStonePresenter {
		fun getSecret(password: String, hold: (secret: String?, error: AccountError) -> Unit)
		fun updateFingerEncryptKey(encryptKey: String, callback: () -> Unit)
		fun turnOffFingerprintPayment(callback: () -> Unit)
	}
}