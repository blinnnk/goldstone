package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter

import android.os.Bundle
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.showAfterColonContent
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.SolidityCode
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toCryptHexString
import io.goldstone.blockchain.crypto.utils.toDataString
import io.goldstone.blockchain.crypto.utils.toDataStringFromAddress
import io.goldstone.blockchain.kernel.network.ChainURL
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentPrepareModel
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import java.math.BigInteger

/**
 * @date 2018/7/25 3:24 PM
 * @author KaySaith
 */
fun PaymentPreparePresenter.prepareETHERC20ETCPaymentModel(count: Double, callback: () -> Unit) {
	generatePaymentPrepareModel(
		count,
		fragment.getMemoContent(),
		ChainURL.getChainTypeBySymbol(fragment.rootFragment?.token?.symbol.orEmpty()),
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
	chainType: ChainType,
	callback: () -> Unit,
	hold: (PaymentPrepareModel) -> Unit
) {
	GoldStoneEthCall.getUsableNonce(
		{ error, reason ->
			fragment.context?.alert(reason ?: error.toString().showAfterColonContent())
		},
		chainType,
		WalletTable.getAddressBySymbol(getToken()?.symbol)
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
		getToken()?.contract.equals(CryptoValue.ethContract, true)
			or getToken()?.contract.equals(CryptoValue.etcContract, true) -> {
			to = toAddress
			data = if (memo.isEmpty()) "0x" else "0x" + memo.toCryptHexString() // Memo
			countWithDecimal = CryptoUtils.toValueWithDecimal(count)
		}
		
		else -> {
			to = getToken()?.contract.orEmpty()
			countWithDecimal = CryptoUtils.toValueWithDecimal(count, getToken()?.decimal.orZero())
			data = SolidityCode.contractTransfer + // 方法
				toAddress.toDataStringFromAddress() + // 地址
				countWithDecimal.toDataString() + // 数量
				if (memo.isEmpty()) "" else memo.toCryptHexString() // Memo
		}
	}
	GoldStoneEthCall.getTransactionExecutedValue(
		to,
		WalletTable.getAddressBySymbol(getToken()?.symbol),
		data,
		{ error, reason ->
			fragment.context?.alert(reason ?: error.toString())
			callback()
		},
		ChainURL.getChainNameBySymbol(getToken()?.symbol.orEmpty())
	) { limit ->
		hold(
			PaymentPrepareModel(
				WalletTable.getAddressBySymbol(getToken()?.symbol),
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