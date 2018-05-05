package io.goldstone.blockchain.common.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.isTrue
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver

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

@Suppress("DEPRECATION")
class ConnectionChangeReceiver : BroadcastReceiver() {
	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	override fun onReceive(context: Context, intent: Intent) {
		NetworkUtil.hasNetwork(context) isTrue {
			AppConfigTable.getAppConfig {
				it?.isRegisteredAddresses?.isFalse {
					XinGePushReceiver.registerWalletAddressForPush()
				}
			}
		}
	}
}