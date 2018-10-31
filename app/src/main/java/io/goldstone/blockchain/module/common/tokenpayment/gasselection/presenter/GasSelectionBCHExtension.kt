package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.crypto.bitcoin.BTCSeriesTransactionUtils
import io.goldstone.blockchain.crypto.bitcoin.exportBase58PrivateKey
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.kernel.network.bitcoin.BTCSeriesJsonRPC
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashApi
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.GasSelectionPresenter.Companion.goToTransactionDetailFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentBTCSeriesModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/15 5:52 PM
 * @author KaySaith
 */

fun GasSelectionPresenter.prepareToTransferBCH(@UiThread callback: (GoldStoneError) -> Unit) {
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

private fun GasSelectionPresenter.getCurrentBCHPrivateKey(
	walletAddress: String,
	password: String,
	hold: (privateKey: String?, error: AccountError) -> Unit
) {
	fragment.context?.exportBase58PrivateKey(
		walletAddress,
		password,
		SharedValue.isTestEnvironment(),
		true,
		hold
	)
}

fun GasSelectionPresenter.transferBCH(
	prepareBTCSeriesModel: PaymentBTCSeriesModel,
	password: String,
	@WorkerThread callback: (GoldStoneError) -> Unit
) {
	getCurrentBCHPrivateKey(
		prepareBTCSeriesModel.fromAddress,
		password
	) { privateKey, error ->
		if (!privateKey.isNull() && error.isNone()) prepareBTCSeriesModel.apply model@{
			val fee = gasUsedGasFee?.toSatoshi()!!
			BitcoinCashApi.getUnspentListByAddress(fromAddress) { unspents, error ->
				if (unspents.isNull() || error.hasError()) {
					callback(error)
					return@getUnspentListByAddress
				}
				BTCSeriesTransactionUtils.generateBCHSignedRawTransaction(
					value,
					fee,
					toAddress,
					changeAddress,
					unspents!!,
					privateKey!!,
					SharedValue.isTestEnvironment()
				).let { signedModel ->
					BTCSeriesJsonRPC.sendRawTransaction(
						SharedChain.getBCHCurrent(),
						signedModel.signedMessage
					) { hash, error ->
						if (!hash.isNullOrEmpty() && error.isNone()) {
							// 插入 `Pending` 数据到本地数据库
							insertBTCSeriesPendingDataDatabase(this, fee, signedModel.messageSize, hash!!)
							// 跳转到章党详情界面
							GoldStoneAPI.context.runOnUiThread {
								goToTransactionDetailFragment(
									rootFragment,
									fragment,
									prepareReceiptModelFromBTCSeries(this@model, fee, hash)
								)
								callback(GoldStoneError.None)
							}
						} else callback(error)
					}
				}
			}
		} else callback(error)
	}
}