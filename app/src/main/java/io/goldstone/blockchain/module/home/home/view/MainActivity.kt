package io.goldstone.blockchain.module.home.home.view

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.saveDataToSharedPreferences
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.tencent.android.tpush.XGPushClickedResult
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.component.overlay.LoadingView
import io.goldstone.blockchain.common.language.AlarmText
import io.goldstone.blockchain.common.utils.ConnectionChangeReceiver
import io.goldstone.blockchain.common.utils.PriceAlarmUtils
import io.goldstone.blockchain.common.utils.TinyNumber
import io.goldstone.blockchain.common.utils.transparentStatus
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmlist.presenter.PriceAlarmReceiver
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmlist.presenter.PriceAlarmStatusObserver
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationFragment
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @rewriteDate 16/08/2018 16:23 PM
 * @rewriter wcx
 * @description 增加showNotificationAlarmPopUps()处理价格闹钟跳转逻辑,初始化priceAlarmStatusObserver价格闹铃轮询
 */

class MainActivity : AppCompatActivity() {

	var backEvent: Runnable? = null
	private var loadingView: LoadingView? = null
	private var netWorkReceiver: ConnectionChangeReceiver? = null
	private var tracker: Tracker? = null
	private var priceAlarmStatusObserver: PriceAlarmStatusObserver? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val application = application as GoldStoneApp
		// 初始化 `Google Analytics` 追踪器
		tracker = application.getDefaultTracker()

		XinGePushReceiver.appIsBackground = false
		// 轮询价格闹铃监听
		priceAlarmStatusObserver = object : PriceAlarmStatusObserver(this) {}.apply { start() }

		transparentStatus()

		setContentView(relativeLayout {
			id = ContainerID.main
			savedInstanceState.isNull {
				// 判断 `SaveInstanceState` 防止旋转屏幕重新创建 `Fragment`
				addFragment<HomeFragment>(this.id, FragmentTag.home)
			}
		})
		registerReceiver()
	}

	private var currentIntent: Intent? = null
	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		// App 存在的时候记录 `Intent`
		currentIntent = intent
	}

	override fun onResume() {
		super.onResume()
		XinGePushReceiver.appIsBackground = false
		// Push 跳转
		showNotificationFragmentByIntent(currentIntent ?: intent)
		showNotificationAlarmPopUps(currentIntent ?: intent)
	}

	override fun onStop() {
		super.onStop()
		XinGePushReceiver.appIsBackground = true
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

	fun showLoadingView() {
		findViewById<RelativeLayout>(ContainerID.main)?.let { layout ->
			findViewById<LoadingView>(ElementID.loadingView).isNull() isTrue {
				loadingView = LoadingView(layout.context)
				layout.addView(loadingView)
			}
		}
	}

	fun removeLoadingView() {
		findViewById<RelativeLayout>(ContainerID.main)?.apply {
			loadingView?.let { removeView(it) }
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		unregisterReceiver(netWorkReceiver)
		priceAlarmStatusObserver?.removeObserver()
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

	fun getMainContainer(): RelativeLayout? {
		return findViewById(ContainerID.main)
	}

	// 防止重绘的专用方法
	fun hideHomeFragment() {
		supportFragmentManager.findFragmentByTag(FragmentTag.home)?.let {
			(it as? HomeFragment)?.let {
				supportFragmentManager.beginTransaction().hide(it).commitAllowingStateLoss()
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

	private fun recoveryBackEventFromOtherApp() {
		supportFragmentManager.fragments.last()?.let {
			if (it is BaseOverlayFragment<*>) {
				val child = it.childFragmentManager.fragments.last()
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
			presenter.showNotificationListFragment()
			currentIntent = null
		}
	}

	/**
	 * 接受到 `Push` 弹出闹铃弹窗
	 */
	private fun showNotificationAlarmPopUps(intent: Intent?) {
		val alarmInfo = intent?.getSerializableExtra(IntentKey.alarmInfoFromNotify)
		if (alarmInfo.isNull()) {
			PriceAlarmUtils.stopAlarmReceiver(
				this,
				1
			)
			PriceAlarmReceiver.stopAlarmClock()
			return
		}

		GoldStoneDialog.remove(this)
		GoldStoneDialog.show(this) {
			showButtons(AlarmText.viewAlarm) {
				confirmButtonClickEvent()
			}
			setGoldStoneDialog(
				this,
				alarmInfo as XGPushClickedResult
			)
		}
	}

	private fun cancelButtonClickEvent() {
		PriceAlarmUtils.stopAlarmReceiver(
			this,
			1
		)
		PriceAlarmReceiver.stopAlarmClock()
		GoldStoneDialog.remove(this)
		this.findViewById<RelativeLayout>(ContainerID.main).removeView(findViewById<GoldStoneDialog>(ElementID.dialog))
	}

	private fun confirmButtonClickEvent() {
		addFragmentAndSetArguments<QuotationOverlayFragment>(ContainerID.main) {
			putBoolean(
				ArgumentKey.priceAlarmTitle,
				true
			)
			putSerializable(
				ArgumentKey.quotationOverlayInfo, QuotationModel(
					"symbol",
					"name",
					"price",
					"",
					ArrayList(),
					"marketName",
					0.0,
					"pairDisplay",
					"pair",
					"currencyName",
					"",
					false
				)
			)

			cancelButtonClickEvent()
		}
	}

	private fun setGoldStoneDialog(
		goldStoneDialog: GoldStoneDialog,
		alarmInfo: XGPushClickedResult
	) {
		goldStoneDialog.apply {
			getCancelButton().apply {
				text = AlarmText.gotIt
				onClick {
					cancelButtonClickEvent()
				}
			}

			setImage(R.drawable.price_alarm_banner)

			setContent(
				alarmInfo.title,
				alarmInfo.content
			)
		}
	}

	private fun registerReceiver() {
		netWorkReceiver = ConnectionChangeReceiver()
		val intentFilter = IntentFilter()
		intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
		registerReceiver(netWorkReceiver, intentFilter)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		this.saveDataToSharedPreferences(SharesPreference.activityIsResult, TinyNumber.True.value)
	}
}

fun FragmentActivity?.findIsItExist(fragmentTag: String): Boolean {
	return !this?.supportFragmentManager?.findFragmentByTag(fragmentTag).isNull()
}