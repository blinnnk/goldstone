package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter

import android.os.Bundle
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orEmpty
import com.blinnnk.extension.orZero
import com.blinnnk.extension.scaleTo
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.bitcoin.BTCSeriesTransactionUtils
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.multichain.getAddress
import io.goldstone.blockchain.crypto.utils.isValidDecimal
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.bitcoin.BTCSeriesJsonRPC
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentBTCSeriesModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/7/25 3:13 PM
 * @author KaySaith
 */
fun PaymentPreparePresenter.prepareBTCPaymentModel(
	count: Double,
	changeAddress: String,
	callback: (errors: GoldStoneError) -> Unit
) {
	if (!count.toString().isValidDecimal(CryptoValue.btcSeriesDecimal))
		callback(TransferError.IncorrectDecimal)
	else generateBTCPaymentModel(count, changeAddress) { error, model ->
		if (!model.isNull()) {
			fragment.rootFragment?.apply {
				presenter.showTargetFragment<GasSelectionFragment>(
					Bundle().apply {
						putSerializable(ArgumentKey.btcSeriesPrepareModel, model)
					})
				callback(GoldStoneError.None)
			}
		} else callback(error)
	}
}

fun PaymentPreparePresenter.isValidAddressOrElse(address: String): Boolean {
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

private fun PaymentPreparePresenter.generateBTCPaymentModel(
	count: Double,
	changeAddress: String,
	hold: (error: GoldStoneError, PaymentBTCSeriesModel?) -> Unit
) {
	val myAddress = getToken()?.contract.getAddress()
	val chainName =
		if (SharedValue.isTestEnvironment()) ChainText.btcTest else ChainText.btcMain
	// 这个接口返回的是 `n` 个区块内的每千字节平均燃气费
	BTCSeriesJsonRPC.estimatesmartFee(
		chainName,
		3,
		true,
		{ hold(it, null) }
	) { feePerByte ->
		if (feePerByte.orZero() < 0) {
			hold(TransferError.GetWrongFeeFromChain, null)
			return@estimatesmartFee
		}
		// 签名测速总的签名后的信息的 `Size`
		BitcoinApi.getUnspentListByAddress(myAddress) { unspents ->
			if (unspents.isEmpty()) {
				// 如果余额不足或者出错这里会返回空的数组
				GoldStoneAPI.context.runOnUiThread {
					hold(TransferError.BalanceIsNotEnough, null)
				}
				return@getUnspentListByAddress
			}
			val size = BTCSeriesTransactionUtils.generateBTCSignedRawTransaction(
				count.toSatoshi(),
				1L,
				fragment.address.orEmpty(),
				changeAddress,
				unspents,
				// 测算 `MessageSize` 的默认无效私钥
				if (SharedValue.isTestEnvironment()) CryptoValue.signedSecret
				else CryptoValue.signedBTCMainnetSecret,
				SharedValue.isTestEnvironment()
			).messageSize
			// 返回的是千字节的费用, 除以 `1000` 得出 `1` 字节的燃气费
			val unitFee = feePerByte.orZero().toSatoshi() / 1000
			PaymentBTCSeriesModel(
				fragment.address.orEmpty(),
				getToken()?.contract.getAddress(),
				changeAddress,
				count.toSatoshi(),
				unitFee,
				size.toLong()
			).let {
				GoldStoneAPI.context.runOnUiThread {
					hold(GoldStoneError.None, it)
				}
			}
		}
	}
}