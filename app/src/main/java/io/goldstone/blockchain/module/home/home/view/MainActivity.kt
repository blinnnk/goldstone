package io.goldstone.blockchain.module.home.home.view

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.blinnnk.extension.addFragment
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.findChildFragmentByTag
import com.blinnnk.extension.isNull
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.ConnectionChangeReceiver
import io.goldstone.blockchain.common.utils.transparentStatus
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.kernel.receiver.registerDeviceForPush
import io.goldstone.blockchain.module.home.dapp.dappbrowser.view.DAppBrowserFragment
import io.goldstone.blockchain.module.home.dapp.dappcenter.view.DAPPCenterFragment
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import org.jetbrains.anko.relativeLayout

class MainActivity : AppCompatActivity() {

	var backEvent: Runnable? = null
	private var netWorkReceiver: ConnectionChangeReceiver? = null
	private var tracker: Tracker? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		// 如果不是开发者模式禁止任何截屏行为
		if (!SharedValue.getDeveloperModeStatus())
			window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
		val application = application as GoldStoneApp
		// 初始化 `Google Analytics` 追踪器
		tracker = application.getDefaultTracker()

		transparentStatus()

		setContentView(relativeLayout {
			id = ContainerID.main
			if (savedInstanceState.isNull()) {
				// 判断 `SaveInstanceState` 防止旋转屏幕重新创建 `Fragment`
				addFragment<HomeFragment>(this.id, FragmentTag.home)
				// 如果本地的钱包数量不为空那么才开始注册设备
				// 把 `GoldStoneID` 存储到 `SharePreference` 里面
				registerDeviceForPush()
			}
			registerReceiver()
		})
	}

	override fun onDestroy() {
		super.onDestroy()
		unregisterReceiver(netWorkReceiver)
	}

	private var currentIntent: Intent? = null
	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		// App 存在的时候记录 `Intent`
		currentIntent = intent
	}

	override fun onResume() {
		super.onResume()
		// Push 跳转
		showNotificationFragmentByIntent(currentIntent ?: intent)
	}

	fun sendAnalyticsData(className: String) {
		tracker?.setScreenName(className)
		tracker?.send(
			HitBuilders.ScreenViewBuilder().setCustomDimension(
				currentChannel.code,
				currentChannel.value
			).build()
		)
	}

	fun showDappBrowserFragment(url: String, previousView: Int, currentFragment: Fragment) {
		hideChildFragment(currentFragment)
		addFragmentAndSetArguments<DAppBrowserFragment>(ContainerID.main) {
			putString(ArgumentKey.webViewUrl, url)
			putInt(ArgumentKey.fromView, previousView)
		}
	}

	override fun onBackPressed() {
		recoveryBackEventFromOtherApp()
		if (backEvent.isNull()) {
			super.onBackPressed()
		} else {
			backEvent?.run()
		}
	}

	fun getHomeFragment(): HomeFragment? {
		supportFragmentManager.findFragmentByTag(FragmentTag.home).let {
			return if (it.isNull()) null
			else it as? HomeFragment
		}
	}

	// 防止重绘的专用方法
	fun hideHomeFragment() {
		supportFragmentManager.findFragmentByTag(FragmentTag.home)?.let { fragment ->
			if (fragment is HomeFragment) {
				supportFragmentManager.beginTransaction().hide(fragment).commitAllowingStateLoss()
			}
		}
	}

	// 防止重绘的专用方法
	fun showHomeFragment() {
		getHomeFragment()?.let {
			if (it.isHidden) {
				supportFragmentManager
					?.beginTransaction()
					?.show(it)
					?.commitAllowingStateLoss()
			}
		}
	}

	fun getWalletDetailFragment(): WalletDetailFragment? {
		return getHomeFragment()?.findChildFragmentByTag(FragmentTag.walletDetail)
	}

	fun getQuotationFragment(): QuotationFragment? {
		return getHomeFragment()?.findChildFragmentByTag(FragmentTag.quotation)
	}

	fun getDAPPCenterFragment(): DAPPCenterFragment? {
		return getHomeFragment()?.findChildFragmentByTag(FragmentTag.dappCenter)
	}

	fun showChildFragment(child: Fragment) {
		supportFragmentManager?.beginTransaction()?.show(child)?.commit()
	}

	private fun hideChildFragment(child: Fragment) {
		supportFragmentManager?.beginTransaction()?.hide(child)?.commit()
	}

	private fun recoveryBackEventFromOtherApp() {
		supportFragmentManager.fragments.last()?.let {
			if (it is BaseOverlayFragment<*>) {
				val child = it.childFragmentManager.fragments.lastOrNull()
				if (child is BaseFragment<*>) {
					child.recoveryBackEvent()
				} else if (child is BaseRecyclerFragment<*, *>) {
					child.recoveryBackEvent()
				}
			}
		}
	}

	/**
	 * 接受到 `Push` 跳转到 `NotificationFragment`
	 */
	private fun showNotificationFragmentByIntent(intent: Intent?) {
		val hash = intent?.getStringExtra(IntentKey.hashFromNotify)
		if (hash.isNull()) return
		getWalletDetailFragment()?.apply {
			// 如果有正在打开的悬浮层, 直接关闭
			supportFragmentManager.fragments.find {
				it is BaseOverlayFragment<*>
			}?.let {
				(it as? BaseOverlayFragment<*>)?.presenter?.removeSelfFromActivity()
			}
			// 展示通知中心
			showNotificationListFragment()
			currentIntent = null
		}
	}

	private fun registerReceiver() {
		netWorkReceiver = ConnectionChangeReceiver()
		val intentFilter = IntentFilter()
		intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
		registerReceiver(netWorkReceiver, intentFilter)
	}
}

fun FragmentActivity?.findIsItExist(fragmentTag: String): Boolean {
	return !this?.supportFragmentManager?.findFragmentByTag(fragmentTag).isNull()
}