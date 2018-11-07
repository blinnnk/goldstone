@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.common.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.language.DialogText
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.presenter.SplashPresenter
import io.goldstone.blockchain.module.home.home.view.MainActivity
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

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

	fun hasNetwork(context: Context?): Boolean {
		val connection = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
		val activeNetwork = connection?.activeNetworkInfo
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting
	}
}

@Suppress("DEPRECATION")
class ConnectionChangeReceiver : BroadcastReceiver() {

	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	override fun onReceive(context: Context, intent: Intent) {
		if (NetworkUtil.hasNetwork(context)) {
			// 如果还没有检测过账号状态那么在网络恢复的时候检测并更新钱包的资产状态
			if (!SharedValue.getAccountCheckedStatus()) {
				launch {
					withContext(CommonPool, CoroutineStart.LAZY) {
						SplashPresenter.updateAccountInformation(context) {
							(context as? MainActivity)?.getWalletDetailFragment()
								?.presenter?.updateData()
						}
					}
				}
			}
			// 网络恢复的时候判断是否有在断网情况下创建的地址需要补充注册
			AppConfigTable.getAppConfig {
				if (it?.isRegisteredAddresses == false) {
					WalletTable.getCurrentWallet {
						XinGePushReceiver.registerAddressesForPush(this)
					}
				}
			}
		} else GoldStoneDialog.show(context) {
			showOnlyConfirmButton {
				GoldStoneDialog.remove(context)
			}
			setImage(R.drawable.network_browken_banner)
			setContent(DialogText.networkTitle, DialogText.networkDescription)
		}
	}
}