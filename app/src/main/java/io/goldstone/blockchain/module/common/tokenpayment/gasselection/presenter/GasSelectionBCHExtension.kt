package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.orElse
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.crypto.bitcoin.BTCSeriesTransactionUtils.generateBCHSignedRawTransaction
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.kernel.network.bitcoin.BTCSeriesJsonRPC.sendRawTransaction
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashApi
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentBTCSeriesModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter.PrivateKeyExportPresenter.Companion.getPrivateKey
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/15 5:52 PM
 * @author KaySaith
 */

fun GasSelectionPresenter.prepareToTransferBCH(@UiThread callback: (GoldStoneError) -> Unit) {
	prepareBTCSeriesModel?.apply {
		// 检查余额状况
		BitcoinCashApi.getBalance(fromAddress, true) { balance, error ->
			if (balance != null && error.isNone()) {
				val isEnough =
					balance.toSatoshi() > value + gasUsedGasFee?.toSatoshi().orElse(0)
				when {
					isEnough -> showConfirmAttentionView(callback)
					error.isNone() -> callback(TransferError.BalanceIsNotEnough)
					else -> callback(error)
				}
			} else callback(error)
		}
	}
}

fun GasSelectionPresenter.transferBCH(
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
			BitcoinCashApi.getUnspentListByAddress(fromAddress) { unspents, error ->
				if (unspents != null && error.isNone()) generateBCHSignedRawTransaction(
					value,
					fee,
					toAddress,
					changeAddress,
					unspents,
					privateKey,
					SharedValue.isTestEnvironment()
				).let { signedModel ->
					sendRawTransaction(SharedChain.getBCHCurrent(), signedModel.signedMessage) { hash, hashError ->
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
						} else callback(hashError)
					}
				} else callback(error)
			}
		} else callback(error)
	}
}