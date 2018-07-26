package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter

import android.os.Bundle
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ChainText
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.kernel.network.bitcoin.BTCJsonRPC
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentPrepareBTCModel

/**
 * @date 2018/7/25 3:13 PM
 * @author KaySaith
 */
fun PaymentPreparePresenter.prepareBTCPaymentModel(
	count: Double,
	callback: () -> Unit
) {
	generateBTCPaymentModel(count) {
		fragment.rootFragment?.apply {
			presenter.showTargetFragment<GasSelectionFragment>(
				TokenDetailText.customGas,
				TokenDetailText.paymentValue,
				Bundle().apply {
					putSerializable(ArgumentKey.btcPrepareModel, it)
				})
			callback()
		}
	}
}

private fun PaymentPreparePresenter.generateBTCPaymentModel(
	count: Double,
	hold: (PaymentPrepareBTCModel) -> Unit
) {
	val chainName =
		if (Config.isTestEnvironment()) ChainText.btcTest else ChainText.btcMain
	// 这个接口返回的是 `n` 个区块内的每千字节平均燃气费
	BTCJsonRPC.estimatesmartFee(chainName, 3) {
		if (it.orZero() > 0) {
			val feePerByte = it.orZero().toSatoshi() / 1000
			getFromAddress {
				PaymentPrepareBTCModel(
					fragment.address.orEmpty(),
					it,
					count.toSatoshi(),
					feePerByte
				).let(hold)
			}
		}
	}
}