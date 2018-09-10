package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WalletType
import io.goldstone.blockchain.crypto.bitcoin.BTCSeriesTransactionUtils
import io.goldstone.blockchain.crypto.bitcoin.exportBase58PrivateKey
import io.goldstone.blockchain.crypto.litecoin.exportLTCBase58PrivateKey
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.bitcoin.BTCSeriesJsonRPC
import io.goldstone.blockchain.kernel.network.litecoin.LitecoinApi
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFooter
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentBTCSeriesModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/14 11:11 PM
 * @author KaySaith
 */

fun GasSelectionPresenter.prepareToTransferLTC(
	footer: GasSelectionFooter,
	callback: () -> Unit
) {
	// 检查余额状况
	checkLTCBalanceIsValid(gasUsedGasFee!!) {
		if (!this) {
			footer.setCanUseStyle(false)
			fragment.context.alert("Your LTC balance is not enough for this transaction")
			fragment.showMaskView(false)
			callback()
			return@checkLTCBalanceIsValid
		} else {
			GoldStoneAPI.context.runOnUiThread {
				showConfirmAttentionView(footer, callback)
			}
		}
	}
}

private fun GasSelectionPresenter.getCurrentWalletLTCPrivateKey(
	walletAddress: String,
	password: String,
	hold: (String?) -> Unit
) {
	val isSingleChainWallet =
		!Config.getCurrentWalletType().equals(WalletType.Bip44MultiChain.content, true)
	if(Config.isTestEnvironment()) {
		fragment.context?.exportBase58PrivateKey(
			walletAddress,
			password,
			isSingleChainWallet,
			true,
			hold
		)
	} else {
		fragment.context?.exportLTCBase58PrivateKey(
			walletAddress,
			password,
			isSingleChainWallet,
			hold
		)
	}
}

fun GasSelectionPresenter.transferLTC(
	prepareBTCSeriesModel: PaymentBTCSeriesModel,
	password: String,
	callback: () -> Unit
) {
	getCurrentWalletLTCPrivateKey(
		prepareBTCSeriesModel.fromAddress,
		password
	) { secret ->
		if (secret.isNullOrBlank()) {
			callback()
			fragment.showMaskView(false)
			return@getCurrentWalletLTCPrivateKey
		}
		prepareBTCSeriesModel.apply model@{
			val fee = gasUsedGasFee?.toSatoshi()!!
			LitecoinApi.getUnspentListByAddress(fromAddress) { unspents ->
				BTCSeriesTransactionUtils.generateLTCSignedRawTransaction(
					value,
					fee,
					toAddress,
					changeAddress,
					unspents,
					secret!!,
					Config.isTestEnvironment()
				).let { signedModel ->
					BTCSeriesJsonRPC.sendRawTransaction(
						Config.getLTCCurrentChainName(),
						signedModel.signedMessage
					) { hash ->
						hash?.let {
							// 插入 `Pending` 数据到本地数据库
							insertBTCSeriesPendingDataDatabase(
								this,
								fee,
								signedModel.messageSize,
								it
							)
							// 跳转到章党详情界面
							GoldStoneAPI.context.runOnUiThread {
								goToTransactionDetailFragment(
									prepareReceiptModelFromBTCSeries(this@model, fee, it)
								)
								callback()
							}
						}
					}
				}
			}
		}
	}
}