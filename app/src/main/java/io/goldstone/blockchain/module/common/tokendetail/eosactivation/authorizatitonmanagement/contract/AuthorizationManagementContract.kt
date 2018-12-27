package io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.contract

import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.model.AuthorizationManagementModel
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.model.AuthorizationObserverModel
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.model.AuthorizationType


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
	}
}