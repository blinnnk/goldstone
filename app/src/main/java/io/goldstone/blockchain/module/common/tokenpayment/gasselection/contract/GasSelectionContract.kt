package io.goldstone.blockchain.module.common.tokenpayment.gasselection.contract

import android.support.annotation.WorkerThread
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView


/**
 * @author KaySaith
 * @date  2018/11/17
 */
interface GasSelectionContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {
		fun setSpendingValue(value: String)
		fun showConfirmAttention(@WorkerThread callback: (GoldStoneError) -> Unit)
	}

	interface GSPresenter : GoldStonePresenter {
		fun checkIsValidTransfer(@WorkerThread callback: (GoldStoneError) -> Unit)
		fun transfer(privateKey: String, @WorkerThread callback: (GoldStoneError) -> Unit)
	}
}