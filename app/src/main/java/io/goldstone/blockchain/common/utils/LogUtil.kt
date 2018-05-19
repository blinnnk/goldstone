package io.goldstone.blockchain.common.utils

import android.util.Log
import io.goldstone.blockchain.common.value.LogTag

/**
 * @date 2018/5/19 2:41 PM
 * @author KaySaith
 */

object LogUtil {

	fun error(error: String) {
		Log.e(LogTag.error, error)
	}

	fun debug(debug: String) {
		Log.e(LogTag.debug, debug)
	}

}