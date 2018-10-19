package io.goldstone.blockchain.common.utils

import android.content.Context


/**
 * @author KaySaith
 * @date  2018/10/19
 */
class ErrorDisplayManager(context: Context?, error: Throwable) {
	init {
		when {
			error.message.orEmpty().contains("Timeout", true) -> {
				// 上报 Server 逻辑, 这部分超市
			}
			else -> context.alert(error.message.orEmpty())
		}
	}
}