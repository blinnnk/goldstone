package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter

import android.os.Bundle
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.orZero
import com.blinnnk.extension.otherwise
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.bitcoin.BTCTransactionUtils
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.bitcoin.BTCJsonRPC
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentPrepareBTCModel
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/7/25 3:13 PM
 * @author KaySaith
 */
fun PaymentPreparePresenter.prepareBTCPaymentModel(
	count: Double,
	changeAddress: String,
	callback: (isSuccess: Boolean) -> Unit
) {
	generateBTCPaymentModel(count, changeAddress) {
		it isNotNull {
			fragment.rootFragment?.apply {
				presenter.showTargetFragment<GasSelectionFragment>(
					TokenDetailText.customGas,
					TokenDetailText.paymentValue,
					Bundle().apply {
						putSerializable(ArgumentKey.btcPrepareModel, it)
					})
				callback(true)
			}
		} otherwise {
			callback(false)
		}
	}
}

fun PaymentPreparePresenter.isValidAddressOrElse(address: String): Boolean {
	if (address.isNotEmpty()) {
		val isValidAddress = if (Config.isTestEnvironment()) {
			BTCUtils.isValidTestnetAddress(address)
		} else {
			BTCUtils.isValidMainnetAddress(address)
		}
		if (isValidAddress) {
			fragment.updateChangeAddress(CryptoUtils.scaleTo22(address))
		} else {
			fragment.context.alert(ImportWalletText.addressFromatAlert)
		}
		fragment.activity?.let { SoftKeyboard.hide(it) }
		return isValidAddress
	} else {
		return false
	}
}

private fun PaymentPreparePresenter.generateBTCPaymentModel(
	count: Double,
	changeAddress: String,
	hold: (PaymentPrepareBTCModel?) -> Unit
) {
	val myAddress = WalletTable.getAddressBySymbol(getToken()?.symbol)
	val chainName =
		if (Config.isTestEnvironment()) ChainText.btcTest else ChainText.btcMain
	// 这个接口返回的是 `n` 个区块内的每千字节平均燃气费
	BTCJsonRPC.estimatesmartFee(chainName, 3) { feePerByte ->
		if (feePerByte.orZero() < 0) {
			// TODO Alert
			return@estimatesmartFee
		}
		// 签名测速总的签名后的信息的 `Size`
		BitcoinApi.getUnspentListByAddress(myAddress) { unspents ->
			if (unspents.isEmpty()) {
				// 如果余额不足或者出错这里会返回空的数组
				hold(null)
				return@getUnspentListByAddress
			}
			val size = BTCTransactionUtils.generateSignedRawTransaction(
				count.toSatoshi(),
				1L,
				fragment.address.orEmpty(),
				changeAddress,
				unspents,
				CryptoValue.signedSecret, // 测算 `MessageSize` 的默认无效私钥
				Config.isTestEnvironment()
			).messageSize
			val unitFee = feePerByte.orZero().toSatoshi() / 1000
			PaymentPrepareBTCModel(
				fragment.address.orEmpty(),
				WalletTable.getAddressBySymbol(getToken()?.symbol),
				changeAddress,
				count.toSatoshi(),
				unitFee,
				size.toLong()
			).let {
				GoldStoneAPI.context.runOnUiThread {
					hold(it)
				}
			}
		}
	}
}