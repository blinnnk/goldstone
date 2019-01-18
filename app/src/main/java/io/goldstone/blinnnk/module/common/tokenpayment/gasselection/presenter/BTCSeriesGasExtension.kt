package io.goldstone.blinnnk.module.common.tokenpayment.gasselection.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.orElse
import io.goldstone.blinnnk.common.error.GoldStoneError
import io.goldstone.blinnnk.common.error.TransferError
import io.goldstone.blinnnk.common.sharedpreference.SharedValue
import io.goldstone.blinnnk.common.utils.AddressUtils
import io.goldstone.blinnnk.crypto.bitcoin.BTCSeriesTransactionUtils
import io.goldstone.blinnnk.crypto.litecoin.LitecoinNetParams
import io.goldstone.blinnnk.crypto.multichain.*
import io.goldstone.blinnnk.crypto.utils.formatCount
import io.goldstone.blinnnk.crypto.utils.toBTCCount
import io.goldstone.blinnnk.kernel.commontable.BTCSeriesTransactionTable
import io.goldstone.blinnnk.kernel.database.GoldStoneDataBase
import io.goldstone.blinnnk.kernel.network.bitcoin.BTCSeriesJsonRPC.sendRawTransaction
import io.goldstone.blinnnk.kernel.network.btcseries.insight.InsightApi
import io.goldstone.blinnnk.module.common.tokenpayment.gaseditor.presenter.GasFee
import io.goldstone.blinnnk.module.common.tokenpayment.paymentdetail.model.PaymentBTCSeriesModel
import io.goldstone.blinnnk.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import java.math.BigInteger

/**
 * @date 2018/8/15 5:52 PM
 * @author KaySaith
 */

fun GasSelectionPresenter.checkBTCSeriesBalance(
	contract: TokenContract,
	@WorkerThread callback: (GoldStoneError) -> Unit
) {
	InsightApi.getBalance(contract.getChainType(), !contract.isBCH(), contract.getAddress()) { balance, error ->
		if (balance.isNotNull() && error.isNone()) {
			val isEnough =
				BigInteger.valueOf(balance.value) > gasView.getTransferCount().orElse(BigInteger.ZERO) + BigInteger.valueOf(currentFee.getUsedAmount())
			if (isEnough) callback(GoldStoneError.None)
			else callback(TransferError.BalanceIsNotEnough)
		} else callback(error)
	}
}

fun GasSelectionPresenter.transferBTCSeries(
	btcSeriesModel: PaymentBTCSeriesModel,
	chainType: ChainType,
	privateKey: String,
	fee: GasFee,
	@WorkerThread callback: (receiptModel: ReceiptModel?, error: GoldStoneError) -> Unit
) {
	if (btcSeriesModel.estimateFeePerByte > fee.gasPrice) {
		callback(
			null,
			GoldStoneError("The current Estimate Fee Per Byte is ${btcSeriesModel.estimateFeePerByte}. The value you selected is too low. Please adjust the Gas Price in the custom gas fee.")
		)
		return
	}
	with(btcSeriesModel) {
		val feeUsed = fee.getUsedAmount()
		// `BCH` 的 `Insight Api` 不需要加密
		InsightApi.getUnspent(chainType, !chainType.isBCH(), fromAddress) { unspent, error ->
			if (unspent.isNotNull() && error.isNone()) BTCSeriesTransactionUtils.generateSignedRawTransaction(
				value,
				feeUsed,
				toAddress,
				changeAddress,
				unspent,
				privateKey,
				when {
					SharedValue.isTestEnvironment() -> TestNet3Params.get()
					chainType.isLTC() -> LitecoinNetParams()
					else -> MainNetParams.get()
				},
				chainType.isBCH() // 如果是 `BCH` 采用特殊的签名方式
			).let { signedModel ->
				sendRawTransaction(chainType.getChainURL(), signedModel.signedMessage) { hash, hashError ->
					if (!hash.isNullOrEmpty() && error.isNone()) {
						insertBTCSeriesPendingData(this, feeUsed, signedModel.messageSize, hash)
						callback(generateReceipt(this@with, feeUsed, hash), hashError)
					} else callback(null, hashError)
				}
			} else callback(null, error)
		}
	}
}

private fun GasSelectionPresenter.insertBTCSeriesPendingData(
	raw: PaymentBTCSeriesModel,
	fee: Long,
	size: Int,
	taxHash: String
) {
	val myAddress = AddressUtils.getCurrentBTCAddress()
	BTCSeriesTransactionTable(
		0, // TODO 插入 Pending Data 应该是 localMaxDataIndex + 1
		token.symbol.symbol,
		-1,
		0,
		System.currentTimeMillis().toString(),
		taxHash,
		myAddress,
		raw.toAddress,
		myAddress,
		false,
		raw.value.toBTCCount().formatCount(),
		fee.toBTCCount().toBigDecimal().toPlainString(),
		size.toString(),
		-1,
		false,
		true,
		token.contract.getChainType().id
	).apply {
		val transactionDao =
			GoldStoneDataBase.database.btcSeriesTransactionDao()
		// 插入 PendingData
		transactionDao.insert(this)
		// 插入 FeeData
		transactionDao.insert(this.apply {
			isPending = false
			isFee = true
		})
	}
}