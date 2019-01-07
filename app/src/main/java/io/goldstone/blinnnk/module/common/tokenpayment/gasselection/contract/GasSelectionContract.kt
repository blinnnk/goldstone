package io.goldstone.blinnnk.module.common.tokenpayment.gasselection.contract

import android.support.annotation.WorkerThread
import android.widget.LinearLayout
import io.goldstone.blinnnk.common.error.GoldStoneError
import io.goldstone.blinnnk.crypto.multichain.TokenContract
import io.goldstone.blinnnk.module.common.contract.GoldStonePresenter
import io.goldstone.blinnnk.module.common.contract.GoldStoneView
import io.goldstone.blinnnk.module.common.tokenpayment.gaseditor.presenter.GasFee
import io.goldstone.blinnnk.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import java.io.Serializable
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/11/20
 */
interface GasSelectionContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {
		fun showLoading(status: Boolean)
		fun getMemo(): String
		fun getGasLimit(): Long
		fun getTransferCount(): BigInteger?
		fun showSpendingValue(value: String)
		fun getCustomFee(): GasFee
		fun getGasLayout(): LinearLayout
		fun clearGasLayout()
	}

	interface GSPresenter : GoldStonePresenter {
		var currentFee: GasFee
		fun checkIsValidTransfer(@WorkerThread callback: (GoldStoneError) -> Unit)
		fun addCustomFeeCell()
		fun transfer(
			contract: TokenContract,
			privateKey: String,
			paymentModel: Serializable,
			gasFee: GasFee,
			@WorkerThread callback: (receiptModel: ReceiptModel?, error: GoldStoneError) -> Unit
		)
	}
}