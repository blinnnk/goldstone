package io.goldstone.blinnnk.module.common.tokenpayment.paymentdetail.presenter

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.orElse
import io.goldstone.blinnnk.common.error.GoldStoneError
import io.goldstone.blinnnk.common.sharedpreference.SharedAddress
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.crypto.eos.account.EOSAccount
import io.goldstone.blinnnk.crypto.eos.transaction.EOSTransactionInfo
import io.goldstone.blinnnk.crypto.multichain.CryptoValue
import io.goldstone.blinnnk.crypto.multichain.TokenContract
import io.goldstone.blinnnk.crypto.utils.toAmount
import io.goldstone.blinnnk.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import io.goldstone.blinnnk.module.home.wallet.transactions.transactiondetail.model.ReceiptModel


/**
 * @author KaySaith
 * @date  2018/09/14
 */

// EOS 的 `Token` 转币只需写对 `Token` 的 `Symbol` 就可以转账成功
fun PaymentDetailPresenter.transferEOS(
	count: Double,
	contract: TokenContract,
	@UiThread cancelAction: () -> Unit,
	@WorkerThread callback: (error: GoldStoneError) -> Unit
) {
	EOSTransactionInfo(
		SharedAddress.getCurrentEOSAccount(),
		EOSAccount(fragment.address.orEmpty()),
		count.toAmount(contract.decimal.orElse(CryptoValue.eosDecimal)),
		fragment.getMemoContent(),
		contract
	).apply {
		trade(fragment.context!!, cancelAction = cancelAction) { response, error ->
			if (error.isNone() && response.isNotNull())
				insertPendingDataToDatabase(response) {
					launchUI {
						getToken()?.let {
							val receiptModel = ReceiptModel(this, response, it)
							GasSelectionFragment.goToTransactionDetailFragment(rootFragment, fragment, receiptModel)
						}
					}
				}
			else callback(error)
		}
	}
}

