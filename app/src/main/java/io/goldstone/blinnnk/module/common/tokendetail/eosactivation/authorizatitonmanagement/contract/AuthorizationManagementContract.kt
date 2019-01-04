package io.goldstone.blinnnk.module.common.tokendetail.eosactivation.authorizatitonmanagement.contract

import io.goldstone.blinnnk.common.error.GoldStoneError
import io.goldstone.blinnnk.crypto.eos.account.EOSPrivateKey
import io.goldstone.blinnnk.crypto.eos.accountregister.EOSActor
import io.goldstone.blinnnk.crypto.eos.base.EOSResponse
import io.goldstone.blinnnk.module.common.contract.GoldStonePresenter
import io.goldstone.blinnnk.module.common.contract.GoldStoneView
import io.goldstone.blinnnk.module.common.tokendetail.eosactivation.authorizatitonmanagement.model.AuthorizationManagementModel
import io.goldstone.blinnnk.module.common.tokendetail.eosactivation.authorizatitonmanagement.model.AuthorizationObserverModel
import io.goldstone.blinnnk.module.common.tokendetail.eosactivation.authorizatitonmanagement.model.AuthorizationType


/**
 * @author KaySaith
 * @date  2018/12/26
 */

interface AuthorizationManagementContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {
		fun updateData(data: ArrayList<AuthorizationManagementModel>)
	}

	interface GSPresenter : GoldStonePresenter {
		fun updateAuthorization(
			newPublicKey: String,
			oldPublicKey: String,
			permission: EOSActor,
			privateKey: EOSPrivateKey,
			actionType: AuthorizationType,
			hold: (response: EOSResponse?, error: GoldStoneError) -> Unit
		)

		fun refreshAuthList(observerModel: AuthorizationObserverModel)
		fun removeObserver()
		fun checkIsExistedOrElse(
			targetPublickey: String,
			targetActor: EOSActor,
			callback: (Boolean) -> Unit
		)
		fun showAlertBeforeDeleteLastKey(willDeletePublicKey: String, callback: (Boolean) -> Unit)
		fun deleteAccount(callback: () -> Unit)
	}
}