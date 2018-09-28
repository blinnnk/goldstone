package io.goldstone.blockchain.common.base.basefragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ElementID
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
	/**
	 * 为了解耦, 每个页面自己管理自己的 `Title` 完全杜绝 `Title` 需要有上一个页面带入或者复杂的逻辑
	 * 恢复自身 `Title` 的耦合业务. 这个 `Abstract `方法会在 `OnFragmentHidden`, `OnCreateView` 的时候
	 * 检查和更新
	 */
	abstract val pageTitle: String

	abstract fun AnkoContext<Fragment>.initView()
	open val isRelativeContainer = false
	private lateinit var scrollView: ScrollView

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
		setPageTitle()
		return UI {
			initView()
		}.view
	}

	fun <V : ViewGroup> getContainer(): V? {
		return if (::scrollView.isInitialized) scrollView.findViewById(ElementID.baseFragmentContainer) else null
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
			setPageTitle()
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

	fun getParentContainer(): ViewGroup? {
		val parent = parentFragment
		return if (parent is BaseOverlayFragment<*>) {
			parent.overlayView
		} else {
			null
		}
	}

	private fun setPageTitle() {
		val parent = parentFragment
		if (parent is BaseOverlayFragment<*>) {
			parent.headerTitle = pageTitle
		}
	}
}