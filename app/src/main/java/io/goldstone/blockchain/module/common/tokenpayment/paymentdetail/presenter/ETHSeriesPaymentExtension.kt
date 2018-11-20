package io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.presenter

import android.os.Bundle
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.ethereum.SolidityCode
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toAddressCode
import io.goldstone.blockchain.crypto.utils.toCryptHexString
import io.goldstone.blockchain.crypto.utils.toDataString
import io.goldstone.blockchain.kernel.network.ethereum.ETHJsonRPC
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.model.PaymentDetailModel
import java.math.BigInteger

/**
 * @date 2018/7/25 3:24 PM
 * @author KaySaith
 */
fun PaymentDetailPresenter.prepareETHSeriesPaymentModel(
	count: Double,
	@UiThread callback: (RequestError) -> Unit
) {
	generatePaymentPrepareModel(
		count,
		fragment.getMemoContent(),
		fragment.rootFragment?.token?.contract.getChainType()
	) { model, error ->
		launchUI {
			if (model.isNotNull() && error.isNone()) {
				fragment.rootFragment?.apply {
					presenter.showTargetFragment<GasSelectionFragment>(
						Bundle().apply {
							putSerializable(ArgumentKey.gasPrepareModel, model)
						})
					callback(RequestError.None)
				}
			} else callback(error)
		}
	}
}

/**
 * 查询当前账户的可用 `nonce` 以及 `symbol` 的相关信息后, 生成 `Recommend` 的 `RawTransaction`
 */
private fun PaymentDetailPresenter.generatePaymentPrepareModel(
	count: Double,
	memo: String,
	chainType: ChainType,
	@WorkerThread hold: (model: PaymentDetailModel?, error: RequestError) -> Unit
) {
	ETHJsonRPC.getUsableNonce(
		chainType.getChainURL(),
		getToken()?.contract.getAddress()
	) { nonce, error ->
		if (!nonce.isNull() && error.isNone()) {
			generateTransaction(fragment.address!!, count, memo, nonce, hold)
		} else hold(null, error)
	}
}

private fun PaymentDetailPresenter.generateTransaction(
	toAddress: String,
	count: Double,
	memo: String,
	nonce: BigInteger,
	@WorkerThread hold: (model: PaymentDetailModel?, error: RequestError) -> Unit
) {
	val amount: BigInteger
	val data: String
	val to: String
	// `ETH`, `ETC` 和 `Token` 转账需要准备不同的 `Transaction`
	when {
		getToken()?.contract.isETH() || getToken()?.contract.isETC() -> {
			to = toAddress
			data = if (memo.isEmpty()) "0x" else "0x" + memo.toCryptHexString() // Memo
			amount = CryptoUtils.toValueWithDecimal(count)
		}

		else -> {
			to = getToken()?.contract?.contract.orEmpty()
			amount = CryptoUtils.toValueWithDecimal(count, getToken()?.decimal.orZero())
			data = SolidityCode.contractTransfer + // 方法
				toAddress.toAddressCode(false) + // 地址
				amount.toDataString() + // 数量
				if (memo.isEmpty()) "" else memo.toCryptHexString() // Memo
		}
	}
	ETHJsonRPC.getTransactionExecutedValue(
		to,
		getToken()?.contract.getAddress(),
		data,
		getToken()?.contract?.getChainURL()!!
	) { limit, error ->
		if (limit != null && error.isNone()) {
			hold(
				PaymentDetailModel(
					getToken()?.contract.getAddress(),
					nonce,
					limit,
					to,
					amount,
					count,
					data,
					fragment.address!!,
					fragment.getMemoContent()
				),
				error
			)
		} else hold(null, error)
	}
}