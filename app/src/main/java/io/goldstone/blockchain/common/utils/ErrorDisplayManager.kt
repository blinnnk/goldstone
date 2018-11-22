package io.goldstone.blockchain.common.utils

import android.content.Context
import android.support.annotation.UiThread
import com.blinnnk.extension.isNotNull
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
					// 上报 Server 逻辑, 这部分超市
					null
				}
				errorMessage.contains("404") -> {
					// 上报 Server 逻辑, 这部分超市
					null
				}
				errorMessage.contains("failed to connect", true) -> {
					// 上报 Server 逻辑, 这部分超市
					null
				}
				// 比特币交易的时候数额特别小的时候, 链会返回这个关键字的错误.
				errorMessage.contains("64: dust", true) -> {
					"amount too small to be recognised as legitimate on the bitcoin network."
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
				if (!packageName.isNullOrEmpty() && contains(packageName, true)) {
					context.alert(substring(packageName.length, length))
				} else context.alert(this)
			}
		}
	}
}