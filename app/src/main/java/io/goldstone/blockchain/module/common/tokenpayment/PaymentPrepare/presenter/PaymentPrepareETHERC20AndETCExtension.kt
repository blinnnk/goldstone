package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter

import android.os.Bundle
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.ethereum.SolidityCode
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toAddressCode
import io.goldstone.blockchain.crypto.utils.toCryptHexString
import io.goldstone.blockchain.crypto.utils.toDataString
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentPrepareModel
import java.math.BigInteger

/**
 * @date 2018/7/25 3:24 PM
 * @author KaySaith
 */
fun PaymentPreparePresenter.prepareETHSeriesPaymentModel(
	count: Double,
	callback: (RequestError) -> Unit
) {
	generatePaymentPrepareModel(
		count,
		fragment.getMemoContent(),
		fragment.rootFragment?.token?.contract.getChainType(),
		callback
	) { model ->
		fragment.rootFragment?.apply {
			presenter.showTargetFragment<GasSelectionFragment>(
				Bundle().apply {
					putSerializable(ArgumentKey.gasPrepareModel, model)
				})
			callback(RequestError.None)
		}
	}
}

/**
 * 查询当前账户的可用 `nonce` 以及 `symbol` 的相关信息后, 生成 `Recommend` 的 `RawTransaction`
 */
private fun PaymentPreparePresenter.generatePaymentPrepareModel(
	count: Double,
	memo: String,
	chainType: ChainType,
	errorCallback: (RequestError) -> Unit,
	hold: (PaymentPrepareModel) -> Unit
) {
	GoldStoneEthCall.getUsableNonce(
		errorCallback,
		chainType,
		CoinSymbol(getToken()?.symbol).getAddress()
	) {
		generateTransaction(fragment.address!!, count, memo, it, errorCallback, hold)
	}
}

private fun PaymentPreparePresenter.generateTransaction(
	toAddress: String,
	count: Double,
	memo: String,
	nonce: BigInteger,
	errorCallback: (RequestError) -> Unit,
	hold: (PaymentPrepareModel) -> Unit
) {
	val countWithDecimal: BigInteger
	val data: String
	val to: String
	// `ETH`, `ETC` 和 `Token` 转账需要准备不同的 `Transaction`
	when {
		getToken()?.contract.isETH() || getToken()?.contract.isETC() -> {
			to = toAddress
			data = if (memo.isEmpty()) "0x" else "0x" + memo.toCryptHexString() // Memo
			countWithDecimal = CryptoUtils.toValueWithDecimal(count)
		}

		else -> {
			to = getToken()?.contract?.contract.orEmpty()
			countWithDecimal = CryptoUtils.toValueWithDecimal(count, getToken()?.decimal.orZero())
			data = SolidityCode.contractTransfer + // 方法
				toAddress.toAddressCode(false) + // 地址
				countWithDecimal.toDataString() + // 数量
				if (memo.isEmpty()) "" else memo.toCryptHexString() // Memo
		}
	}
	GoldStoneEthCall.getTransactionExecutedValue(
		to,
		CoinSymbol(getToken()?.symbol).getAddress(),
		data,
		errorCallback,
		CoinSymbol(getToken()?.symbol).getCurrentChainName()
	) { limit ->
		hold(
			PaymentPrepareModel(
				CoinSymbol(getToken()?.symbol).getAddress(),
				nonce,
				limit,
				to,
				countWithDecimal,
				count,
				data,
				fragment.address!!,
				fragment.getMemoContent()
			)
		)
	}
}