package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.bitcoin.BTCSeriesTransactionUtils
import io.goldstone.blockchain.crypto.bitcoin.exportBase58PrivateKey
import io.goldstone.blockchain.crypto.litecoin.exportLTCBase58PrivateKey
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.bitcoin.BTCSeriesJsonRPC
import io.goldstone.blockchain.kernel.network.litecoin.LitecoinApi
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.GasSelectionPresenter.Companion.goToTransactionDetailFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFooter
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentBTCSeriesModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/14 11:11 PM
 * @author KaySaith
 */

fun GasSelectionPresenter.prepareToTransferLTC(
	footer: GasSelectionFooter,
	@UiThread callback: (GoldStoneError) -> Unit
) {
	checkLTCBalanceIsValid(gasUsedGasFee!!) { isEnough, error ->
		when {
			isEnough -> showConfirmAttentionView(footer, callback)
			error.isNone() -> callback(TransferError.BalanceIsNotEnough)
			else -> callback(error)
		}
	}
}

private fun GasSelectionPresenter.getCurrentWalletLTCPrivateKey(
	walletAddress: String,
	password: String,
	@UiThread hold: (privateKey: String?, error: AccountError) -> Unit
) {
	val isSingleChainWallet = !Config.getCurrentWalletType().isBIP44()
	if (Config.isTestEnvironment()) fragment.context?.exportBase58PrivateKey(
		walletAddress,
		password,
		isSingleChainWallet,
		true,
		hold
	) else fragment.context?.exportLTCBase58PrivateKey(
		walletAddress,
		password,
		isSingleChainWallet,
		hold
	)
}

fun GasSelectionPresenter.transferLTC(
	prepareBTCSeriesModel: PaymentBTCSeriesModel,
	password: String,
	callback: (GoldStoneError) -> Unit
) {
	getCurrentWalletLTCPrivateKey(
		prepareBTCSeriesModel.fromAddress,
		password
	) { privateKey, error ->
		if (!privateKey.isNull() && error.isNone()) prepareBTCSeriesModel.apply model@{
			val fee = gasUsedGasFee?.toSatoshi()!!
			LitecoinApi.getUnspentListByAddress(fromAddress) { unspents ->
				BTCSeriesTransactionUtils.generateLTCSignedRawTransaction(
					value,
					fee,
					toAddress,
					changeAddress,
					unspents,
					privateKey!!,
					Config.isTestEnvironment()
				).let { signedModel ->
					BTCSeriesJsonRPC.sendRawTransaction(
						Config.getLTCCurrentChainName(),
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
		} else callback(error)
	}
}