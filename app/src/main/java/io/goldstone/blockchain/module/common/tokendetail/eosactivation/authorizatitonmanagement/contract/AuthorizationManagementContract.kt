package io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.contract

import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.authorizatitonmanagement.model.AuthorizationManagementModel


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
			publicKey: String,
			permission: EOSActor,
			privateKey: EOSPrivateKey,
			isDelete: Boolean,
			hold: (response: EOSResponse?, error: GoldStoneError) -> Unit
		)
		fun refreshAuthList()
	}
}