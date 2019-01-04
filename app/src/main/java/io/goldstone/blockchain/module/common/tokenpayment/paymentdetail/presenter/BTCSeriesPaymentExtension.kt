package io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.presenter

import android.os.Bundle
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.scaleTo
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.bitcoin.BTCSeriesTransactionUtils
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
import io.goldstone.blockchain.crypto.litecoin.LitecoinNetParams
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.kernel.network.bitcoin.BTCSeriesJsonRPC
import io.goldstone.blockchain.kernel.network.btcseries.insight.InsightApi
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.model.PaymentBTCSeriesModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params

/**
 * @date 2018/8/15 4:53 PM
 * @author KaySaith
 */

fun PaymentDetailPresenter.prepareBTCSeriesPaymentModel(
	chainType: ChainType,
	count: Double,
	changeAddress: String,
	@UiThread callback: (GoldStoneError) -> Unit
) {
	generateBTCSeriesPaymentModel(chainType, count, changeAddress) { paymentModel, error ->
		GlobalScope.launch(Dispatchers.Main) {
			if (paymentModel.isNotNull()) fragment.rootFragment?.apply {
				presenter.showTargetFragment<GasSelectionFragment>(
					Bundle().apply {
						putSerializable(ArgumentKey.btcSeriesPrepareModel, paymentModel)
					})
				callback(error)
			}
			else callback(error)
		}
	}
}

fun PaymentDetailPresenter.generateBTCSeriesPaymentModel(
	chainType: ChainType,
	count: Double,
	changeAddress: String,
	@WorkerThread hold: (model: PaymentBTCSeriesModel?, error: GoldStoneError) -> Unit
) {
	val myAddress = getToken()?.contract.getAddress()
	// 这个接口返回的是 `n` 个区块内的每千字节平均燃气费
	BTCSeriesJsonRPC.estimatesmartFee(
		chainType.getChainURL(),
		3,
		!chainType.isBCH()
	) { feePerByte, feeError ->
		// API 错误的时候
		if (feePerByte == null || feeError.hasError()) {
			hold(null, feeError)
			return@estimatesmartFee
		}
		if (feePerByte < 0) {
			hold(null, TransferError.GetWrongFeeFromChain)
			return@estimatesmartFee
		}
		// 签名测速总的签名后的信息的 `Size`
		InsightApi.getUnspents(chainType, !chainType.isBCH(), myAddress) { unspents, error ->
			if (unspents == null || error.hasError()) {
				hold(null, error)
			} else if (unspents.isEmpty() || error.hasError()) {
				// 如果余额不足或者出错这里会返回空的数组
				hold(null, TransferError.BalanceIsNotEnough)
			} else {
				val size = BTCSeriesTransactionUtils.generateSignedRawTransaction(
					count.toSatoshi(),
					1L,
					fragment.address.orEmpty(),
					changeAddress,
					unspents,
					// 测算 `MessageSize` 的默认无效私钥
					when {
						SharedValue.isTestEnvironment() -> CryptoValue.signedSecret
						chainType.isLTC() -> CryptoValue.ltcMainnetSignedSecret
						else -> CryptoValue.signedBTCMainnetSecret
					},
					when {
						SharedValue.isTestEnvironment() -> TestNet3Params.get()
						chainType.isLTC() -> LitecoinNetParams()
						else -> MainNetParams.get()
					},
					chainType.isBCH()
				).messageSize
				// 返回的是千字节的费用, 除以 `1000` 得出 `1` 字节的燃气费
				val unitFee = feePerByte.toSatoshi() / 1000

				hold(
					PaymentBTCSeriesModel(
						fragment.address.orEmpty(),
						chainType.getContract().getAddress(),
						changeAddress,
						count.toSatoshi(),
						unitFee,
						size.toLong()
					),
					error
				)
			}
		}
	}
}

fun PaymentDetailPresenter.isValidLTCAddressOrElse(address: String): Boolean {
	return if (address.isNotEmpty()) {
		val isValidAddress =
			if (SharedValue.isTestEnvironment()) BTCUtils.isValidTestnetAddress(address)
			else LTCWalletUtils.isValidAddress(address)
		if (isValidAddress) fragment.updateChangeAddress(address.scaleTo(22))
		else fragment.context.alert(ImportWalletText.addressFormatAlert)
		fragment.activity?.let { SoftKeyboard.hide(it) }
		isValidAddress
	} else {
		false
	}
}

fun PaymentDetailPresenter.isValidAddressOrElse(address: String): Boolean {
	if (address.isNotEmpty()) {
		val isValidAddress = if (SharedValue.isTestEnvironment()) {
			BTCUtils.isValidTestnetAddress(address)
		} else {
			BTCUtils.isValidMainnetAddress(address)
		}
		if (isValidAddress) {
			fragment.updateChangeAddress(address.scaleTo(22))
		} else {
			fragment.context.alert(ImportWalletText.addressFormatAlert)
		}
		fragment.activity?.let { SoftKeyboard.hide(it) }
		return isValidAddress
	} else {
		return false
	}
}