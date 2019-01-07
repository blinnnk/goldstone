package io.goldstone.blinnnk.module.home.profile.fingerprintsetting.contract

import io.goldstone.blinnnk.common.error.AccountError
import io.goldstone.blinnnk.module.common.contract.GoldStonePresenter
import io.goldstone.blinnnk.module.common.contract.GoldStoneView


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