package io.goldstone.blockchain.common.utils

import android.content.Context
import android.net.ConnectivityManager

/**
 * @date 2018/5/3 3:33 PM
 * @author KaySaith
 */

object NetworkUtil {
	fun hasNetwork(context: Context? = null): Boolean {
		val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
		val activeNetwork = cm?.activeNetworkInfo
		val status = activeNetwork != null && activeNetwork.isConnectedOrConnecting
		if (!status) context?.alert("thiere isn't network found")
		return status
	}
}