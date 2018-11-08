package io.goldstone.blockchain.common.utils

import android.content.Context
import android.support.annotation.UiThread
import org.jetbrains.anko.runOnUiThread


/**
 * @author KaySaith
 * @date  2018/10/19
 */
class ErrorDisplayManager(error: Throwable) {
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
			context?.runOnUiThread { alert(this@apply) }
		}
	}
}