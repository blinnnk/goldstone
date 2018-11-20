package io.goldstone.blockchain.common.base.basefragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.overlayview.OverlayView
import io.goldstone.blockchain.common.base.gsfragment.GSFragment
import io.goldstone.blockchain.common.component.overlay.TopMiniLoadingView
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ElementID
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.support.v4.UI

/**
 * @date 22/03/2018 2:57 AM
 * @author KaySaith
 */
abstract class BaseFragment<out T : BasePresenter<BaseFragment<T>>> : GSFragment() {

	abstract val presenter: T

	abstract fun AnkoContext<Fragment>.initView()
	// 非阻碍的 `LoadingView`
	private var topMiniLoadingView: TopMiniLoadingView? = null
	private lateinit var scrollView: ScrollView

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		presenter.onFragmentCreateView()
		return UI {
			initView()
		}.view
	}

	fun <V : ViewGroup> getContainer(): V? {
		return if (::scrollView.isInitialized) scrollView.findViewById(ElementID.baseFragmentContainer) else null
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter.onFragmentViewCreated()
	}

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		if (!hidden) presenter.onFragmentShowFromHidden()
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

	fun getParentContainer(): OverlayView? {
		val parent = parentFragment
		return when {
			parent is BaseOverlayFragment<*> -> parent.getContainer()
			parent?.parentFragment is BaseOverlayFragment<*> ->
				(parent.parentFragment as? BaseOverlayFragment<*>)?.getContainer()
			else -> null
		}
	}

	// 非阻碍的小圆 `LoadingView`
	fun showLoadingView() {
		getParentContainer()?.contentLayout?.apply {
			findViewById<TopMiniLoadingView>(ElementID.loadingView).isNull() isTrue {
				topMiniLoadingView = TopMiniLoadingView(context).apply {
					y += 50.uiPX()
				}
				addView(topMiniLoadingView)
			}
		}
	}

	fun removeLoadingView() {
		topMiniLoadingView?.let {
			getParentContainer()?.contentLayout?.removeView(it)
		}
	}

}