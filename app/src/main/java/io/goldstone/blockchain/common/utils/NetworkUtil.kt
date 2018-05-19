package io.goldstone.blockchain.common.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.GoldStoneDialog
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver

/**
 * @date 2018/5/3 3:33 PM
 * @author KaySaith
 */

object NetworkUtil {

	fun hasNetworkWithAlert(
		context: Context? = null,
		alertText: String = "thiere isn't network found"
	): Boolean {
		val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
		val activeNetwork = cm?.activeNetworkInfo
		val status = activeNetwork != null && activeNetwork.isConnectedOrConnecting
		if (!status) context?.alert(alertText)
		return status
	}

	fun hasNetwork(context: Context? = null): Boolean {
		val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
		val activeNetwork = cm?.activeNetworkInfo
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting
	}

}

@Suppress("DEPRECATION")
class ConnectionChangeReceiver : BroadcastReceiver() {
	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	override fun onReceive(
		context: Context,
		intent: Intent
	) {
		NetworkUtil.hasNetwork(context) isTrue {
			AppConfigTable.getAppConfig {
				it?.isRegisteredAddresses?.isFalse {
					XinGePushReceiver.registerWalletAddressForPush()
				}
			}
		} otherwise {
			GoldStoneDialog.show(context) {
				showButtons { }
				setImage(R.drawable.alert_banner)
				setContent(
					"Network Browken",
					"An extensible dialog system I designed for the ItsON SaaS telecom solution for mobile Android devices at the OS level. Having dialogs easily identifiable as the brand of the phones service provider allows the context to be clearly understood"
				)
			}
		}
	}
}