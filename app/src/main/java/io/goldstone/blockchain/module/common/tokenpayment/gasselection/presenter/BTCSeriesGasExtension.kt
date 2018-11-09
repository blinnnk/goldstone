package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import android.support.annotation.WorkerThread
import android.widget.LinearLayout
import com.blinnnk.extension.orElse
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.crypto.bitcoin.BTCSeriesTransactionUtils
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.isBCH
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.kernel.network.bitcoin.BTCSeriesJsonRPC.sendRawTransaction
import io.goldstone.blockchain.kernel.network.btcseries.insight.InsightApi
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.GasSelectionModel
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionCell
import io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.model.PaymentBTCSeriesModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter.PrivateKeyExportPresenter.Companion.getPrivateKey
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import java.math.BigInteger

/**
 * @date 2018/8/15 5:52 PM
 * @author KaySaith
 */

fun GasSelectionPresenter.checkBTCSeriesBalance(
	chainType: ChainType,
	@WorkerThread callback: (GoldStoneError) -> Unit
) {
	prepareBTCSeriesModel?.apply {
		// 检查余额状况
		InsightApi.getBalance(chainType, !chainType.isBCH(), fromAddress) { balance, error ->
			if (balance != null && error.isNone()) {
				val isEnough =
					balance.value > value + gasUsedGasFee?.toSatoshi().orElse(0)
				when {
					isEnough -> launch(UI) { showConfirmAttentionView(callback) }
					error.isNone() -> callback(TransferError.BalanceIsNotEnough)
					else -> callback(error)
				}
			} else callback(error)
		}
	}
}

fun GasSelectionPresenter.transferBTCSeries(
	prepareBTCSeriesModel: PaymentBTCSeriesModel,
	address: String,
	chainType: ChainType,
	password: String,
	@WorkerThread callback: (GoldStoneError) -> Unit
) {
	getPrivateKey(
		fragment.context!!,
		address,
		chainType,
		password
	) { privateKey, error ->
		if (privateKey != null && error.isNone()) prepareBTCSeriesModel.apply {
			val fee = gasUsedGasFee?.toSatoshi().orElse(0)
			InsightApi.getUnspents(chainType, !chainType.isBCH(), fromAddress) { unspents, error ->
				if (unspents != null && error.isNone()) BTCSeriesTransactionUtils.generateSignedRawTransaction(
					value,
					fee,
					toAddress,
					changeAddress,
					unspents,
					privateKey,
					if (SharedValue.isTestEnvironment()) TestNet3Params.get() else MainNetParams.get(),
					chainType.isBCH()
				).let { signedModel ->
					sendRawTransaction(chainType.getChainURL(), signedModel.signedMessage) { hash, hashError ->
						if (hash != null && hash.isNotEmpty() && error.isNone()) {
							// 插入 `Pending` 数据到本地数据库
							insertBTCSeriesPendingData(this, fee, signedModel.messageSize, hash)
							// 跳转到章党详情界面
							launch(UI) {
								rootFragment?.goToTransactionDetailFragment(fragment, generateReceipt(this@apply, fee, hash))
							}
							callback(hashError)
						} else callback(hashError)
					}
				} else callback(error)
			}
		} else callback(error)
	}
}

fun GasSelectionPresenter.updateBTCGasSettings(symbol: String, container: LinearLayout) {
	defaultSatoshiValue.forEachIndexed { index, miner ->
		container.findViewById<GasSelectionCell>(index)?.let { cell ->
			cell.model = GasSelectionModel(
				index,
				miner.toString().toLong(),
				prepareBTCSeriesModel?.signedMessageSize ?: 226,
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