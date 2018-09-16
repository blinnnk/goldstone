package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter

import android.os.Bundle
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.showAfterColonContent
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.ethereum.SolidityCode
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.MultiChainType
import io.goldstone.blockchain.crypto.multichain.TokenContract
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
fun PaymentPreparePresenter.prepareETHERC20ETCPaymentModel(
	count: Double, callback: () -> Unit
) {
	generatePaymentPrepareModel(
		count,
		fragment.getMemoContent(),
		TokenContract(fragment.rootFragment?.token?.contract).getCurrentChainType(),
		callback
	) { model ->
		fragment.rootFragment?.apply {
			presenter.showTargetFragment<GasSelectionFragment>(
				TokenDetailText.customGas,
				TokenDetailText.paymentValue,
				Bundle().apply {
					putSerializable(ArgumentKey.gasPrepareModel, model)
				})
			callback()
		}
	}
}

/**
 * 查询当前账户的可用 `nonce` 以及 `symbol` 的相关信息后, 生成 `Recommond` 的 `RawTransaction`
 */
private fun PaymentPreparePresenter.generatePaymentPrepareModel(
	count: Double,
	memo: String,
	chainType: MultiChainType,
	callback: () -> Unit,
	hold: (PaymentPrepareModel) -> Unit
) {
	GoldStoneEthCall.getUsableNonce(
		{ error, reason ->
			fragment.context?.alert(reason ?: error.toString().showAfterColonContent())
		},
		chainType,
		CoinSymbol(getToken()?.symbol).getAddress()
	) {
		generateTransaction(fragment.address!!, count, memo, it, callback, hold)
	}
}

private fun PaymentPreparePresenter.generateTransaction(
	toAddress: String,
	count: Double,
	memo: String,
	nonce: BigInteger,
	callback: () -> Unit,
	hold: (PaymentPrepareModel) -> Unit
) {
	val countWithDecimal: BigInteger
	val data: String
	val to: String
	// `ETH`, `ETC` 和 `Token` 转账需要准备不同的 `Transaction`
	when {
		getToken()?.contract.equals(TokenContract.ethContract, true)
			or getToken()?.contract.equals(TokenContract.etcContract, true) -> {
			to = toAddress
			data = if (memo.isEmpty()) "0x" else "0x" + memo.toCryptHexString() // Memo
			countWithDecimal = CryptoUtils.toValueWithDecimal(count)
		}

		else -> {
			to = getToken()?.contract.orEmpty()
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
		{ error, reason ->
			fragment.context?.alert(reason ?: error.toString())
			callback()
		},
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