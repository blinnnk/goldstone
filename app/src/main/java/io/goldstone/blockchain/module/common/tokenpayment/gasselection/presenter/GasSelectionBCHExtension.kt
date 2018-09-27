package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.crypto.bitcoin.BTCSeriesTransactionUtils
import io.goldstone.blockchain.crypto.bitcoin.exportBase58PrivateKey
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.bitcoin.BTCSeriesJsonRPC
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashApi
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.GasSelectionPresenter.Companion.goToTransactionDetailFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentBTCSeriesModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/15 5:52 PM
 * @author KaySaith
 */

fun GasSelectionPresenter.prepareToTransferBCH(callback: (GoldStoneError) -> Unit) {
	prepareBTCSeriesModel?.apply {
		// 检查余额状况
		BitcoinCashApi.getBalance(fromAddress, true) { balance, error ->
			if (!balance.isNull() && error.isNone()) {
				val isEnough =
					balance?.toSatoshi().orElse(0) > value + gasUsedGasFee!!.toSatoshi()
				when {
					isEnough -> showConfirmAttentionView(callback)
					error.isNone() -> callback(TransferError.BalanceIsNotEnough)
					else -> callback(error)
				}
			} else callback(error)
		}
	}
}

private fun GasSelectionPresenter.getCurrentWalletBCHPrivateKey(
	walletAddress: String,
	password: String,
	hold: (privateKey: String?, error: AccountError) -> Unit
) {
	val isSingleChainWallet = !SharedWallet.getCurrentWalletType().isBIP44()
	fragment.context?.exportBase58PrivateKey(
		walletAddress,
		password,
		isSingleChainWallet,
		SharedValue.isTestEnvironment(),
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
	) { privateKey, error ->
		if (!privateKey.isNull() && error.isNone()) prepareBTCSeriesModel.apply model@{
			val fee = gasUsedGasFee?.toSatoshi()!!
			BitcoinCashApi.getUnspentListByAddress(fromAddress) { unspents ->
				BTCSeriesTransactionUtils.generateBCHSignedRawTransaction(
					value,
					fee,
					toAddress,
					changeAddress,
					unspents,
					privateKey!!,
					SharedValue.isTestEnvironment()
				).let { signedModel ->
					BTCSeriesJsonRPC.sendRawTransaction(
						SharedChain.getBCHCurrentName(),
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