package io.goldstone.blockchain.module.home.profile.fingerprintsetting.contract

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
		fun getUsedStatus(hold: (Boolean) -> Unit)
		fun getSecret(password: String, hold: (String) -> Unit)
		fun updateFingerEncryptKey(encryptKey: String, callback: () -> Unit)
		fun turnOffFingerprintPayment(callback: () -> Unit)
	}
}