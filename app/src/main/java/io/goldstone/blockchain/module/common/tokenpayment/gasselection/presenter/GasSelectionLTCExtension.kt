package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.crypto.bitcoin.BTCSeriesTransactionUtils.generateLTCSignedRawTransaction
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.kernel.network.bitcoin.BTCSeriesJsonRPC.sendRawTransaction
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.litecoin.LitecoinApi
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentBTCSeriesModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter.PrivateKeyExportPresenter
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/14 11:11 PM
 * @author KaySaith
 */

fun GasSelectionPresenter.prepareToTransferLTC(callback: (GoldStoneError) -> Unit) {
	prepareBTCSeriesModel?.apply {
		LitecoinApi.getBalance(fromAddress, true) { balance, error ->
			if (!balance.isNull() && error.isNone()) {
				val isEnough =
					balance?.value.orElse(0) > value + gasUsedGasFee!!.toSatoshi()
				when {
					isEnough -> showConfirmAttentionView(callback)
					error.isNone() -> callback(TransferError.BalanceIsNotEnough)
					else -> callback(error)
				}
			} else callback(error)
		}
	}
}

fun GasSelectionPresenter.transferLTC(
	prepareBTCSeriesModel: PaymentBTCSeriesModel,
	address: String,
	chainType: ChainType,
	password: String,
	@WorkerThread callback: (GoldStoneError) -> Unit
) {
	PrivateKeyExportPresenter.getPrivateKey(
		fragment.context!!,
		address,
		chainType,
		password
	) { privateKey, error ->
		if (privateKey != null && error.isNone()) prepareBTCSeriesModel.apply {
			val fee = gasUsedGasFee?.toSatoshi() ?: 0L
			LitecoinApi.getUnspentListByAddress(fromAddress) { unspents, unspentError ->
				if (unspents != null && error.isNone()) generateLTCSignedRawTransaction(
					value,
					fee,
					toAddress,
					changeAddress,
					unspents,
					privateKey,
					SharedValue.isTestEnvironment()
				).let { signedModel ->
					sendRawTransaction(SharedChain.getLTCCurrent(), signedModel.signedMessage) { hash, hashError ->
						if (hash != null && hash.isNotEmpty() && error.isNone()) {
							// 插入 `Pending` 数据到本地数据库
							insertBTCSeriesPendingData(this, fee, signedModel.messageSize, hash)
							// 跳转到章党详情界面
							GoldStoneAPI.context.runOnUiThread {
								rootFragment?.goToTransactionDetailFragment(
									fragment,
									generateReceipt(this@apply, fee, hash)
								)
							}
							callback(hashError)
						} else callback(error)
					}
				} else callback(unspentError)
			}
		} else callback(error)
	}
}