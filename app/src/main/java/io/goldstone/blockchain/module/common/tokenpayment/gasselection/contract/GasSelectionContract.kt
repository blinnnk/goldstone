package io.goldstone.blockchain.module.common.tokenpayment.gasselection.contract

import android.support.annotation.WorkerThread
import android.widget.LinearLayout
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.presenter.GasFee
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/11/20
 */
interface GasSelectionContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {
		fun showLoading(status: Boolean)
		fun getMemo(): String
		fun getGasLimit(): Long
		fun getTransferCount(): Double
		fun showSpendingValue(value: String)
		fun getCustomFee(): GasFee
		fun getGasLayout(): LinearLayout
		fun clearGasLayout()
	}

	interface GSPresenter : GoldStonePresenter {
		fun checkIsValidTransfer(@WorkerThread callback: (GoldStoneError) -> Unit)
		fun addCustomFeeCell()
		fun transfer(
			contract: TokenContract,
			password: String,
			paymentModel: Serializable,
			gasFee: GasFee,
			@WorkerThread callback: (receiptModel: ReceiptModel?, error: GoldStoneError) -> Unit
		)
	}
}