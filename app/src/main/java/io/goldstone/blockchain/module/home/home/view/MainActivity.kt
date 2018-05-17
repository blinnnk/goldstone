package io.goldstone.blockchain.module.home.home.view

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.LoadingView
import io.goldstone.blockchain.common.utils.ConnectionChangeReceiver
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.common.value.IntentKey
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import org.jetbrains.anko.relativeLayout

class MainActivity : AppCompatActivity() {

	var backEvent: Runnable? = null
	private var loadingView: LoadingView? = null
	private var netWorkReceiver: ConnectionChangeReceiver? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		hideStatusBar()

		relativeLayout {
			id = ContainerID.main
			savedInstanceState.isNull {
				// 判断 `SaveInstanceState` 防止旋转屏幕重新创建 `Fragment`
				addFragment<HomeFragment>(this.id, FragmentTag.home)
			}
		}.let {
			setContentView(it)
		}
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
		if (currentIntent.isNull()) {
			// App 不存在的时候使用传递的参数
			showTransactionDetailFragment(intent)
		} else {
			showTransactionDetailFragment(currentIntent)
		}
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
	}

	override fun onBackPressed() {
		if (backEvent.isNull()) {
			super.onBackPressed()
		} else {
			backEvent?.run()
		}
	}

	fun getHomeFragment(): HomeFragment? {
		return supportFragmentManager.findFragmentByTag(FragmentTag.home) as? HomeFragment
	}

	/**
	 * 接受到 `Push` 跳转到 `NotificationFragment`
	 */
	private fun showTransactionDetailFragment(intent: Intent?) {
		val hash = intent?.getStringExtra(IntentKey.hashFromNotify)
		if (hash.isNull()) return
		getHomeFragment()?.findChildFragmentByTag<WalletDetailFragment>(FragmentTag.walletDetail)
			?.apply {
				// 如果有正在打开的悬浮层, 直接关闭
				supportFragmentManager.fragments.find { it is BaseOverlayFragment<*> }?.let {
					(it as? BaseOverlayFragment<*>)?.presenter?.removeSelfFromActivity()
				}
				// 展示通知中心
				presenter.showNotificationListFragment()
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