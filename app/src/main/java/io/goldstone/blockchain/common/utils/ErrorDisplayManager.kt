package io.goldstone.blockchain.common.utils

import android.content.Context
import android.support.annotation.UiThread
import io.goldstone.blockchain.common.thread.launchUI


/**
 * @author KaySaith
 * @date  2018/10/19
 */
class ErrorDisplayManager(private val error: Throwable) {
	private var displayMessage: String? = null

	init {
		val errorMessage = error.message
		if (errorMessage != null) {
			displayMessage = when {
				errorMessage.contains("Timeout", true) ||
					errorMessage.contains("Time out", true) -> {
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