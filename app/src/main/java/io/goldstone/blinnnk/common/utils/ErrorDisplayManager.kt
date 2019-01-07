package io.goldstone.blinnnk.common.utils

import android.content.Context
import android.support.annotation.UiThread
import android.util.Log
import com.blinnnk.extension.isNotNull
import io.goldstone.blinnnk.common.error.GoldStoneError
import io.goldstone.blinnnk.common.language.EosResourceErrorText
import io.goldstone.blinnnk.common.language.ErrorText
import io.goldstone.blinnnk.common.thread.launchUI


/**
 * @author KaySaith
 * @date  2018/10/19
 */
class ErrorDisplayManager(error: Throwable) {
	private var displayMessage: String? = null

	init {
		val errorMessage = error.message
		// 有些被收集的 `ERROR` 没有执行 `Show` 的方法, 需要再打印中观察并在这个类里面管理起来
		println(errorMessage)
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

				errorMessage.contains("insufficient staked", true) -> {
					"target account doesn't have enough bandwidth to refund"
				}
				// EOS交易时CPU不足
				errorMessage.contains("tx_cpu_usage_exceeded", true) -> {
					EosResourceErrorText.cpuNotEnough
				}
				// EOS交易时CPU不足
				errorMessage.contains("tx_net_usage_exceeded", true) -> {
					EosResourceErrorText.netNotEnough
				}
				// EOS 账号 错误
				errorMessage.contains("account does not exist", true) -> {
					ErrorText.invalidAccountName
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
					} else if (!this.contains(GoldStoneError.None.message, true)) context.alert(this)
				} catch (error: Exception) {
					// Context 丢失的时候执行
					Log.e("Error Display Manager", error.message)
				}
			}
		}
	}
}