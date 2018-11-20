package io.goldstone.blockchain.common.utils

import android.content.Context
import android.support.annotation.UiThread
import com.blinnnk.extension.isNotNull
import io.goldstone.blockchain.common.thread.launchUI


/**
 * @author KaySaith
 * @date  2018/10/19
 */
class ErrorDisplayManager(private val error: Throwable) {
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
					LogUtil.error(this::class.java.simpleName, Throwable("GoldStone ERROR: *************** $errorMessage ***************"))
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
				if (!error.message.isNullOrEmpty()) {
					context.alert(this@apply)
				}
			}
		}
	}
}