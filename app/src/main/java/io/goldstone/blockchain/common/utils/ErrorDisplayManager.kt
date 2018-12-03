package io.goldstone.blockchain.common.utils

import android.content.Context
import android.support.annotation.UiThread
import com.blinnnk.extension.isNotNull
import io.goldstone.blockchain.common.language.ChainErrorText
import io.goldstone.blockchain.common.language.TransactionErrorText
import io.goldstone.blockchain.common.thread.launchUI


/**
 * @author KaySaith
 * @date  2018/10/19
 */
class ErrorDisplayManager(error: Throwable) {
	private var displayMessage: String? = null

	init {
		val errorMessage = error.message
		if (errorMessage.isNotNull()) {
			displayMessage = when {
				errorMessage.contains("Timeout", true)
					|| errorMessage.contains("Time out", true)
					|| errorMessage.contains("timed out", true) -> {
					// 上报 Server 逻辑, 这部分超时
					null
				}
				errorMessage.contains("404") -> {
					// 上报 Server 逻辑, 这部分超时
					null
				}
				errorMessage.contains("failed to connect", true) -> {
					// 上报 Server 逻辑, 这部分超时
					null
				}
				// 第三报错
				errorMessage.contains("onResponse Error in 61", true) -> {
					// 上报 Server 逻辑, 这部分超时
					null
				}
				// 比特币交易的时候数额特别小的时候, 链会返回这个关键字的错误.
				errorMessage.contains("64: dust", true) -> {
					"amount too small to be recognised as legitimate on the bitcoin network."
				}
				// 余额不足以支付Gas.
				errorMessage.contains("insufficient funds for gas * price + value", true) -> {
					TransactionErrorText.notEnoughGasFee
				}
				// EOS get_key_accounts获取失败.
				errorMessage.contains("v1/chain/get_currency_balance", true) -> {
					ChainErrorText.getKeyAccountsError
				}
				// EOS get_key_accounts获取失败.
				errorMessage.contains("v1/history/get_key_accounts", true) -> {
					ChainErrorText.getEOSBalanceError
				}
				// EOS账号已经存在无法注册的错误.
				errorMessage.contains("3050003", true) -> {
					TransactionErrorText.transferToUnactivedEOSAcount
				}
				// EOS链json rpc返回出错，写在最后面，先执行上面的具体报错
				errorMessage.contains("Connection closed by peer", true) -> {
					// 上报 Server 逻辑, 这部分超时
					null
				}
				else -> error.message
			}
		}
	}

	@UiThread
	fun show(context: Context?) {
		displayMessage?.apply {
			launchUI {
				val packageName = context?.applicationContext?.packageName
				try {
					if (!packageName.isNullOrEmpty() && contains(packageName, true)) {
						context.alert(substring(packageName.length, length))
					} else context.alert(this)
				} catch (error: Exception) {
					// Context 丢失的时候执行
					LogUtil.error("Error Display Manager", error)
				}
			}
		}
	}
}