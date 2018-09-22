package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import android.support.annotation.UiThread
import android.widget.LinearLayout
import com.blinnnk.extension.orElse
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.bitcoin.BTCSeriesTransactionUtils
import io.goldstone.blockchain.crypto.bitcoin.exportBase58PrivateKey
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.bitcoin.BTCSeriesJsonRPC
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashApi
import io.goldstone.blockchain.kernel.network.litecoin.LitecoinApi
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.GasSelectionModel
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.GasSelectionPresenter.Companion.goToTransactionDetailFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionCell
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFooter
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentBTCSeriesModel
import org.jetbrains.anko.runOnUiThread
import java.math.BigInteger

/**
 * @date 2018/7/25 9:38 PM
 * @author KaySaith
 */
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

fun GasSelectionPresenter.transferBTC(
	prepareBTCModel: PaymentBTCSeriesModel,
	password: String,
	@UiThread callback: (GoldStoneError) -> Unit
) {
	getCurrentWalletBTCPrivateKey(
		prepareBTCModel.fromAddress,
		password
	) { secret ->
		if (secret.isNullOrBlank()) {
			callback(AccountError.WrongPassword)
		} else prepareBTCModel.apply model@{
			val fee = gasUsedGasFee?.toSatoshi()!!
			BitcoinApi.getUnspentListByAddress(fromAddress) { unspents ->
				BTCSeriesTransactionUtils.generateBTCSignedRawTransaction(
					value,
					fee,
					toAddress,
					changeAddress,
					unspents,
					secret!!,
					Config.isTestEnvironment()
				).let { signedModel ->
					BTCSeriesJsonRPC.sendRawTransaction(
						Config.getBTCCurrentChainName(),
						signedModel.signedMessage,
						callback
					) { hash ->
						hash?.let {
							// 插入 `Pending` 数据到本地数据库
							insertBTCSeriesPendingDataDatabase(this, fee, signedModel.messageSize, it)
							// 跳转到章党详情界面
							GoldStoneAPI.context.runOnUiThread {
								goToTransactionDetailFragment(
									rootFragment,
									fragment,
									prepareReceiptModelFromBTCSeries(this@model, fee, it)
								)
								callback(GoldStoneError.None)
							}
						}
					}
				}
			}
		}
	}
}

private fun GasSelectionPresenter.getCurrentWalletBTCPrivateKey(
	walletAddress: String,
	password: String,
	@UiThread hold: (String?) -> Unit
) {
	val isSingleChainWallet = !Config.getCurrentWalletType().isBIP44()
	fragment.context?.exportBase58PrivateKey(
		walletAddress,
		password,
		isSingleChainWallet,
		Config.isTestEnvironment(),
		hold
	)
}

fun GasSelectionPresenter.prepareToTransferBTC(
	footer: GasSelectionFooter,
	callback: (GoldStoneError) -> Unit
) {
	// 检查余额状况
	checkBTCBalanceIsValid(gasUsedGasFee!!) {
		if (this) GoldStoneAPI.context.runOnUiThread {
			showConfirmAttentionView(footer, callback)
		} else callback(TransferError.BalanceIsNotEnough)
	}
}

fun GasSelectionPresenter.checkBCHBalanceIsValid(fee: Double, hold: Boolean.() -> Unit) {
	prepareBTCSeriesModel?.apply {
		BitcoinCashApi.getBalance(fromAddress) {
			GoldStoneAPI.context.runOnUiThread {
				hold(it.toSatoshi() > value + fee.toSatoshi())
			}
		}
	}
}

fun GasSelectionPresenter.checkBTCBalanceIsValid(fee: Double, hold: Boolean.() -> Unit) {
	prepareBTCSeriesModel?.apply {
		BitcoinApi.getBalance(fromAddress) {
			GoldStoneAPI.context.runOnUiThread {
				hold(it > value + fee.toSatoshi())
			}
		}
	}
}

fun GasSelectionPresenter.checkLTCBalanceIsValid(fee: Double, hold: Boolean.() -> Unit) {
	prepareBTCSeriesModel?.apply {
		LitecoinApi.getBalance(fromAddress) {
			GoldStoneAPI.context.runOnUiThread {
				hold(it > value + fee.toSatoshi())
			}
		}
	}
}