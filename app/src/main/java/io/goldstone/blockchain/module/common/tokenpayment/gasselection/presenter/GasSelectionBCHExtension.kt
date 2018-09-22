package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.bitcoin.BTCSeriesTransactionUtils
import io.goldstone.blockchain.crypto.bitcoin.exportBase58PrivateKey
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.bitcoin.BTCSeriesJsonRPC
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashApi
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.GasSelectionPresenter.Companion.goToTransactionDetailFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFooter
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentBTCSeriesModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/15 5:52 PM
 * @author KaySaith
 */


fun GasSelectionPresenter.prepareToTransferBCH(
	footer: GasSelectionFooter,
	callback: (GoldStoneError) -> Unit
) {
	// 检查余额状况
	checkBCHBalanceIsValid(gasUsedGasFee!!) {
		if (this) GoldStoneAPI.context.runOnUiThread {
			showConfirmAttentionView(footer, callback)
		} else callback(TransferError.BalanceIsNotEnough)
	}
}

private fun GasSelectionPresenter.getCurrentWalletBCHPrivateKey(
	walletAddress: String,
	password: String,
	hold: (String?) -> Unit
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

fun GasSelectionPresenter.transferBCH(
	prepareBTCSeriesModel: PaymentBTCSeriesModel,
	password: String,
	callback: (GoldStoneError) -> Unit
) {
	getCurrentWalletBCHPrivateKey(
		prepareBTCSeriesModel.fromAddress,
		password
	) { secret ->
		if (secret.isNullOrBlank()) {
			callback(AccountError.WrongPassword)
		} else prepareBTCSeriesModel.apply model@{
			val fee = gasUsedGasFee?.toSatoshi()!!
			BitcoinCashApi.getUnspentListByAddress(fromAddress) { unspents ->
				BTCSeriesTransactionUtils.generateBCHSignedRawTransaction(
					value,
					fee,
					toAddress,
					changeAddress,
					unspents,
					secret!!,
					Config.isTestEnvironment()
				).let { signedModel ->
					BTCSeriesJsonRPC.sendRawTransaction(
						Config.getBCHCurrentChainName(),
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