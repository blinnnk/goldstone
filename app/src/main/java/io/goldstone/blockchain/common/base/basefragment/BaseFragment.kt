package io.goldstone.blockchain.common.base.basefragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.support.v4.UI

/**
 * @date 22/03/2018 2:57 AM
 * @author KaySaith
 */
abstract class BaseFragment<out T : BasePresenter<BaseFragment<T>>> : Fragment() {
	
	abstract val presenter: T
	
	abstract fun AnkoContext<Fragment>.initView()
	
	override fun onAttach(context: Context?) {
		super.onAttach(context)
		presenter.onFragmentAttach()
	}
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		presenter.onFragmentCreateView()
		return UI {
			initView()
		}.view
	}
	
	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(view, savedInstanceState)
		presenter.onFragmentViewCreated()
		activity?.apply {
			when (this) {
				is SplashActivity -> {
					backEvent = Runnable {
						setBaseBackEvent(null, parentFragment)
					}
				}
				
				is MainActivity -> {
					backEvent = Runnable {
						setBaseBackEvent(this, parentFragment)
					}
				}
			}
		}
	}
	
	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		if (!hidden) {
			presenter.onFragmentShowFromHidden()
			/**
			 * 软件为了防止重汇会在有新的窗口全屏的时候隐藏主要的 `HomeFragment` 但是隐藏操作会
			 * 导致 `BackEvent` 在这里被重置, 这里判断在 `Parent` 为 `Null` 的时候不执行
			 */
			if (parentFragment.isNull()) return
			getMainActivity()?.apply {
				backEvent = Runnable {
					setBaseBackEvent(this, parentFragment)
				}
			}
		}
	}
	
	override fun onDetach() {
		super.onDetach()
		presenter.onFragmentDetach()
	}
	
	override fun onResume() {
		super.onResume()
		getMainActivity()?.sendAnalyticsData(this::class.java.simpleName)
		presenter.onFragmentResume()
	}
	
	override fun onDestroy() {
		super.onDestroy()
		presenter.onFragmentDestroy()
	}
	
	open fun recoveryBackEvent() {
		getMainActivity()?.apply {
			backEvent = Runnable {
				setBaseBackEvent(this, parentFragment)
			}
		}
	}
	
	open fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
		if (this is WebViewFragment) {
			presenter.setBackEvent()
		} else if (parent is BaseOverlayFragment<*>) {
			parent.presenter.removeSelfFromActivity()
			// 如果阻碍 `Loading` 存在也一并销毁
			activity?.removeLoadingView()
		}
	}
}