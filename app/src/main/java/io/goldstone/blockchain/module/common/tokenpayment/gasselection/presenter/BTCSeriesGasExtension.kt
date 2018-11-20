package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import android.support.annotation.WorkerThread
import android.widget.LinearLayout
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.orElse
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.crypto.bitcoin.BTCSeriesTransactionUtils
import io.goldstone.blockchain.crypto.litecoin.LitecoinNetParams
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.kernel.network.bitcoin.BTCSeriesJsonRPC.sendRawTransaction
import io.goldstone.blockchain.kernel.network.btcseries.insight.InsightApi
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.GasSelectionModel
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionCell
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.model.PaymentBTCSeriesModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
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
				balance.value > getTransferCount() + gasUsedGasFee?.toSatoshi().orElse(0)
			if (isEnough) callback(GoldStoneError.None) else callback(TransferError.BalanceIsNotEnough)
		} else callback(error)
	}
}

fun GasSelectionPresenter.transferBTCSeries(
	btcSeriesModel: PaymentBTCSeriesModel,
	chainType: ChainType,
	privateKey: String,
	@WorkerThread callback: (receiptModel: ReceiptModel?, error: GoldStoneError) -> Unit
) {
	with(btcSeriesModel) {
		val fee = gasUsedGasFee?.toSatoshi().orElse(0)
		// `BCH` 的 `Insight Api` 不需要加密
		InsightApi.getUnspents(chainType, !chainType.isBCH(), fromAddress) { unspents, error ->
			if (unspents.isNotNull() && error.isNone()) BTCSeriesTransactionUtils.generateSignedRawTransaction(
				value,
				fee,
				toAddress,
				changeAddress,
				unspents,
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
						// 插入 `Pending` 数据到本地数据库
						insertBTCSeriesPendingData(this, fee, signedModel.messageSize, hash)
						callback(generateReceipt(this@with, fee, hash), hashError)
					} else callback(null, hashError)
				}
			} else callback(null, error)
		}
	}
}

fun GasSelectionPresenter.updateBTCGasSettings(symbol: String, container: LinearLayout) {
	defaultSatoshiValue.forEachIndexed { index, miner ->
		container.findViewById<GasSelectionCell>(index)?.let { cell ->
			cell.model = GasSelectionModel(
				index,
				miner.toString().toLong(),
				fragment.btcSeriesPaymentModel?.signedMessageSize ?: 226,
				currentMinerType.type,
				symbol
			)
		}
	}
}

fun GasSelectionPresenter.insertCustomBTCSatoshi() {
	val gasPrice = BigInteger.valueOf(gasFeeFromCustom()?.gasPrice.orElse(0))
	currentMinerType = MinerFeeType.Custom
	if (defaultSatoshiValue.size == 4) {
		defaultSatoshiValue.remove(defaultSatoshiValue.last())
	}
	defaultSatoshiValue.add(gasPrice)
	fragment.clearGasLayout()
	generateGasSelections(fragment.getGasLayout())
}