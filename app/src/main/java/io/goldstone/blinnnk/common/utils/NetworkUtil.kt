@file:Suppress("DEPRECATION")

package io.goldstone.blinnnk.common.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import io.goldstone.blinnnk.GoldStoneApp
import io.goldstone.blinnnk.common.component.overlay.GoldStoneDialog
import io.goldstone.blinnnk.common.language.DialogText
import io.goldstone.blinnnk.common.sharedpreference.SharedValue
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.kernel.database.GoldStoneDataBase
import io.goldstone.blinnnk.kernel.receiver.XinGePushReceiver
import io.goldstone.blinnnk.module.entrance.splash.presenter.SplashPresenter
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @date 2018/5/3 3:33 PM
 * @author KaySaith
 */
object NetworkUtil {
	fun hasNetworkWithAlert(
		context: Context? = null,
		alertText: String = DialogText.networkDescription
	): Boolean {
		val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
		val activeNetwork = cm?.activeNetworkInfo
		val status = activeNetwork != null && activeNetwork.isConnectedOrConnecting
		if (!status) context?.alert(alertText)
		return status
	}

	fun hasNetwork(): Boolean {
		val connection = GoldStoneApp.appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
		val activeNetwork = connection?.activeNetworkInfo
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting
	}
}


@Suppress("DEPRECATION")
class ConnectionChangeReceiver : BroadcastReceiver() {
	private var hasShowNetDialog = false
	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	override fun onReceive(context: Context, intent: Intent) {
		if (NetworkUtil.hasNetwork()) GlobalScope.launch(Dispatchers.Default) {
			// 如果还没有检测过账号状态那么在网络恢复的时候检测并更新钱包的资产状态
			if (!SharedValue.getAccountCheckedStatus()) {
				SplashPresenter.updateAccountInformation(context) {
					(context as? MainActivity)?.getWalletDetailFragment()?.presenter?.let { launchUI { it.start() } }
				}
			}
			// 网络恢复的时候判断是否有在断网情况下创建的地址需要补充注册
			val config =
				GoldStoneDataBase.database.appConfigDao().getAppConfig()
			if (config?.isRegisteredAddresses == false) {
				val wallet =
					GoldStoneDataBase.database.walletDao().findWhichIsUsing()
				XinGePushReceiver.registerAddressesForPush(wallet)
			}
		} else {
			if (!hasShowNetDialog) {
				GoldStoneDialog(context).showNetworkStatus {
					hasShowNetDialog = false
				}
				hasShowNetDialog = true
			}
		}
	}
}