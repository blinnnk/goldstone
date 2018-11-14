package io.goldstone.blockchain.common.base.gsfragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity


/**
 * @author KaySaith
 * @date  2018/11/09
 */

abstract class GSFragment : Fragment() {

	/**
	 * 为了解耦, 每个页面自己管理自己的 `Title` 完全杜绝 `Title` 需要有上一个页面带入或者复杂的逻辑
	 * 恢复自身 `Title` 的耦合业务. 这个 `Abstract `方法会在 `OnFragmentHidden`, `OnCreateView` 的时候
	 * 检查和更新
	 */
	abstract val pageTitle: String

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		if (!hidden) {
			setPageTitle()
			/**
			 * 软件为了防止重汇会在有新的窗口全屏的时候隐藏主要的 `HomeFragment` 但是隐藏操作会
			 * 导致 `BackEvent` 在这里被重置, 这里判断在 `Parent` 为 `Null` 的时候不执行
			 */
			if (!parentFragment.isNull()) {
				when (val parentActivity = activity) {
					is MainActivity -> {
						parentActivity.backEvent = Runnable {
							setBaseBackEvent(parentActivity, parentFragment)
						}
					}
					is SplashActivity -> {
						parentActivity.backEvent = Runnable {
							setBaseBackEvent(null, parentFragment)
						}
					}
				}
			}
		}
	}

	open fun recoveryBackEvent() {
		getMainActivity()?.apply {
			backEvent = Runnable { setBaseBackEvent(this, parentFragment) }
		}
	}

	open fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		if (this is WebViewFragment) {
			presenter.setBackEvent()
		} else if (parent is BaseOverlayFragment<*>) {
			parent.presenter.removeSelfFromActivity()
			// 如果阻碍 `Loading` 存在也一并销毁
			activity?.removeLoadingView()
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		setPageTitle()
		super.onViewCreated(view, savedInstanceState)
		activity?.apply {
			when (this) {
				is SplashActivity -> {
					backEvent = Runnable { setBaseBackEvent(null, parentFragment) }
				}
				is MainActivity -> {
					backEvent = Runnable { setBaseBackEvent(this, parentFragment) }
				}
			}
		}
	}

	private fun setPageTitle() {
		val parent = parentFragment
		if (parent is BaseOverlayFragment<*>) {
			parent.headerTitle = pageTitle
		}
	}

}