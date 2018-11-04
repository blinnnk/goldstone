package io.goldstone.blockchain.common.utils

import android.content.Context
import org.jetbrains.anko.runOnUiThread


/**
 * @author KaySaith
 * @date  2018/10/19
 */
class ErrorDisplayManager(error: Throwable) {
	var displayMessage: String? = null

	init {
		displayMessage = when {
			error.message!!.contains("Timeout", true) -> {
				// 上报 Server 逻辑, 这部分超市
				null
			}
			error.message!!.contains("404") -> {
				// 上报 Server 逻辑, 这部分超市
				null
			}
			else -> error.message
		}
	}

	fun show(context: Context?) {
		displayMessage?.apply {
			context?.runOnUiThread { alert(this@apply) }
		}
	}
}