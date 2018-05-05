package io.goldstone.blockchain.module.home.home.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.RelativeLayout
import com.blinnnk.extension.addFragment
import com.blinnnk.extension.hideStatusBar
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import io.goldstone.blockchain.common.component.LoadingView
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.FragmentTag
import org.jetbrains.anko.relativeLayout
import android.content.IntentFilter
import io.goldstone.blockchain.common.utils.ConnectionChangeReceiver

class MainActivity : AppCompatActivity() {

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

	private fun registerReceiver() {
		netWorkReceiver = ConnectionChangeReceiver()
		val intentFilter = IntentFilter()
		intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
		registerReceiver(netWorkReceiver, intentFilter)
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

}
