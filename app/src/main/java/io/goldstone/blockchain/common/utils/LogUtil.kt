package io.goldstone.blockchain.common.utils

import android.util.Log
import io.goldstone.blockchain.common.value.LogTag

/**
 * @date 2018/5/19 2:41 PM
 * @author KaySaith
 */

object LogUtil {

	fun error(
		position: String,
		error: Throwable? = null
	) {
		Log.e(LogTag.error, "position: $position error: $error")
	}

	fun debug(
		position: String,
		debug: String
	) {
		Log.e(LogTag.debug, "position: $position debug: $debug")
	}

}