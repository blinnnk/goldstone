package io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter

import android.widget.LinearLayout
import com.blinnnk.extension.orElse
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.bitcoin.BTCTransactionUtils
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.crypto.verifyKeystorePassword
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.bitcoin.BTCJsonRPC
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.GasSelectionModel
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionCell
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFooter
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import org.jetbrains.anko.runOnUiThread
import java.math.BigInteger

/**
 * @date 2018/7/25 9:38 PM
 * @author KaySaith
 */
fun GasSelectionPresenter.updateBTCGasSettings(container: LinearLayout) {
	defaultSatoshiValue.forEachIndexed { index, minner ->
		container.findViewById<GasSelectionCell>(index)?.let { cell ->
			cell.model = GasSelectionModel(
				index,
				minner.toString().toLong(),
				226,
				currentMinerType
			)
		}
	}
}

fun GasSelectionPresenter.insertCustomBTCSatoshi() {
	val gasPrice =
		BigInteger.valueOf(gasFeeFromCustom()?.gasPrice.orElse(0))
	currentMinerType = MinerFeeType.Custom.content
	if (defaultSatoshiValue.size == 4) {
		defaultSatoshiValue.remove(defaultSatoshiValue.last())
	}
	defaultSatoshiValue.add(gasPrice)
	fragment.clearGasLayout()
	generateGasSelections(fragment.getGasLayout())
}

fun GasSelectionPresenter.transferBTC(password: String, callback: () -> Unit) {
	fragment.context?.verifyKeystorePassword(password) {
		if (!it) {
			fragment.showMaskView(false)
			fragment.context.alert(CommonText.wrongPassword)
			callback()
		} else {
			prepareBTCModel?.apply {
				WalletTable.getBTCPrivateKey(fromAddress, Config.isTestEnvironment()) { secret ->
					BitcoinApi.getUnspentListByAddress(fromAddress) { unspents ->
						BTCTransactionUtils.generateSignedRawTransaction(
							value,
							gasUsedGasFee?.toSatoshi()!!,
							toAddress,
							fromAddress,
							unspents,
							secret,
							Config.isTestEnvironment()
						).let {
							BTCJsonRPC.sendRawTransaction(
								Config.isTestEnvironment(),
								it.signedMessage
							) {
								// TODO
								/**
								 * 1. 插入本地数据库 Pending 数据
								 * 2. 跳转到账单详情界面并显示本地信息
								 * 3. 恢复主线程的各种状况
								 */
								GoldStoneAPI.context.runOnUiThread {
									callback()
								}
							}
						}
					}
				}
			}
		}
	}
}

fun GasSelectionPresenter.prepareToTransferBTC(
	footer: GasSelectionFooter,
	callback: () -> Unit
) {
	// 检查余额状况
	checkBTCBalanceIsValid(gasUsedGasFee!!) {
		if (!this) {
			footer.setCanUseStyle(false)
			fragment.context.alert("Your BTC balance is not enough for this transaction")
			fragment.showMaskView(false)
			callback()
			return@checkBTCBalanceIsValid
		} else {
			GoldStoneAPI.context.runOnUiThread {
				showConfirmAttentionView(footer, callback)
			}
		}
	}
}

fun GasSelectionPresenter.checkBTCBalanceIsValid(fee: Double, hold: Boolean.() -> Unit) {
	prepareBTCModel?.apply {
		BitcoinApi.getBalanceByAddress(fromAddress) {
			GoldStoneAPI.context.runOnUiThread {
				hold(it > value + fee.toSatoshi())
			}
		}
	}
}