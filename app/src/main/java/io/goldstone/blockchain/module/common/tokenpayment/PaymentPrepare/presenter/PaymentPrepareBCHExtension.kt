package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter

import android.os.Bundle
import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.bitcoin.BTCSeriesTransactionUtils
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.utils.isValidDecimal
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.bitcoin.BTCSeriesJsonRPC
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashApi
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentBTCSeriesModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/15 4:53 PM
 * @author KaySaith
 */

fun PaymentPreparePresenter.prepareBCHPaymentModel(
	count: Double,
	changeAddress: String,
	@UiThread callback: (GoldStoneError) -> Unit
) {
	if (!count.toString().isValidDecimal(CryptoValue.btcSeriesDecimal))
		callback(TransferError.IncorrectDecimal)
	else generateBCHPaymentModel(count, changeAddress) { error, paymentModel ->
		if (!paymentModel.isNull()) fragment.rootFragment?.apply {
			presenter.showTargetFragment<GasSelectionFragment>(
				TokenDetailText.customGas,
				TokenDetailText.paymentValue,
				Bundle().apply {
					putSerializable(ArgumentKey.btcSeriesPrepareModel, paymentModel)
				})
			callback(error)
		}
		else callback(error)
	}
}

private fun PaymentPreparePresenter.generateBCHPaymentModel(
	count: Double,
	changeAddress: String,
	@UiThread hold: (GoldStoneError, PaymentBTCSeriesModel?) -> Unit
) {
	val myAddress = CoinSymbol(getToken()?.symbol).getAddress()
	val chainName =
		if (Config.isTestEnvironment()) ChainText.bchTest else ChainText.bchMain
	// 这个接口返回的是 `n` 个区块内的每千字节平均燃气费
	BTCSeriesJsonRPC.estimatesmartFee(
		chainName,
		3,
		false
	) { feePerByte ->
		if (feePerByte.orZero() < 0) {
			hold(TransferError.GetWrongFeeFromChain, null)
			return@estimatesmartFee
		}
		// 签名测速总的签名后的信息的 `Size`
		BitcoinCashApi.getUnspentListByAddress(myAddress) { unspents ->
			if (unspents.isEmpty()) {
				// 如果余额不足或者出错这里会返回空的数组
				GoldStoneAPI.context.runOnUiThread {
					hold(TransferError.BalanceIsNotEnough, null)
				}
				return@getUnspentListByAddress
			}

			val size = BTCSeriesTransactionUtils.generateBCHSignedRawTransaction(
				count.toSatoshi(),
				1L,
				fragment.address.orEmpty(),
				changeAddress,
				unspents,
				if (Config.isTestEnvironment()) CryptoValue.signedSecret
				else CryptoValue.signedBTCMainnetSecret, // 测算 `MessageSize` 的默认无效私钥
				Config.isTestEnvironment()
			).messageSize
			// 返回的是千字节的费用, 除以 `1000` 得出 `1` 字节的燃气费
			val unitFee = feePerByte.orZero().toSatoshi() / 1000
			PaymentBTCSeriesModel(
				fragment.address.orEmpty(),
				CoinSymbol(getToken()?.symbol).getAddress(),
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