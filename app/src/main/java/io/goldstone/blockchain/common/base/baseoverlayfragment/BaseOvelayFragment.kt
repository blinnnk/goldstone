package io.goldstone.blockchain.common.base.baseoverlayfragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.blinnnk.animation.updateHeightAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.AnimationDuration
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.baseoverlayfragment.overlayview.OverlayHeaderLayout
import io.goldstone.blockchain.common.base.baseoverlayfragment.overlayview.OverlayView
import io.goldstone.blockchain.common.utils.getMainActivity
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.UI

/**
 * @date 22/03/2018 2:28 AM
 * @author KaySaith
 */
abstract class BaseOverlayFragment<out T : BaseOverlayPresenter<BaseOverlayFragment<T>>> :
	Fragment() {
	
	abstract val presenter: T
	abstract fun ViewGroup.initView()
	
	/** 观察悬浮曾的 `Header` 状态 */
	open var hasBackButton: Boolean by observing(false) {
		overlayView.header.showBackButton(hasBackButton)
	}
	open var hasCloseButton: Boolean by observing(true) {
		overlayView.header.showCloseButton(hasCloseButton)
	}
	open var headerTitle: String by observing("") {
		overlayView.header.title.text = headerTitle
	}
	lateinit var overlayView: OverlayView
	private val minHeight = 265.uiPX()
	/**
	 * 通过对 `Runnable` 的变化监控, 重新定制控件的 `Header`
	 */
	open var customHeader: (OverlayHeaderLayout.() -> Unit)? by observing(null) {
		overlayView.header.apply {
			title.visibility = View.GONE
			customHeader?.let {
				it()
				overlayView.contentLayout.setMargins<RelativeLayout.LayoutParams> {
					topMargin = layoutParams.height
				}
			}
		}
	}
	
	// 这个是用来还原 `Header` 的边界方法, 当自定义 `Header` 后还原的操作
	fun recoveryOverlayHeader() {
		overlayView.apply {
			header.title.visibility = View.VISIBLE
			header.layoutParams.height = 65.uiPX()
			contentLayout.setMargins<RelativeLayout.LayoutParams> {
				topMargin = 65.uiPX()
			}
		}
	}
	
	open fun setContentHeight(): Int = minHeight
	var afterSetHeightAnimation: Runnable? = null
	
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
			overlayView = OverlayView(context!!)
			overlayView.contentLayout.initView()
			addView(overlayView, RelativeLayout.LayoutParams(matchParent, minHeight))
			
			overlayView.apply {
				val maxHeight = context?.getRealScreenHeight().orZero()
				/** 执行伸展动画 */
				contentLayout.updateHeightAnimation(setContentHeight(), maxHeight, 0) {
					AnimationDuration.Default timeUpThen { afterSetHeightAnimation?.run() }
				}
				/** 设置悬浮曾的 `Header` 初始状态 */
				header.apply {
					showBackButton(hasBackButton)
					showCloseButton(hasCloseButton)
				}
			}
		}.view
	}
	
	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(view, savedInstanceState)
		
		overlayView.apply {
			/** 设定标题的时机 */
			header.title.text = headerTitle
			/** 关闭悬浮曾 */
			header.closeButton.apply {
				onClick {
					presenter.removeSelfFromActivity()
					preventDuplicateClicks()
				}
			}
		}
		presenter.onFragmentViewCreated()
		
		// 让出动画时间
		150L timeUpThen {
			hideTabBarToAvoidOverdraw()
		}
	}
	
	override fun onDetach() {
		super.onDetach()
		presenter.onFragmentDetach()
		showTabBarView()
	}
	
	override fun onDestroy() {
		super.onDestroy()
		presenter.onFragmentDestroy()
	}
	
	override fun onResume() {
		super.onResume()
		presenter.onFragmentResume()
	}
	
	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		if (!hidden) {
			presenter.onFragmentShowFromHidden()
		}
	}
	
	private fun hideTabBarToAvoidOverdraw() {
		getMainActivity()?.getHomeFragment()?.hideTabbarView()
	}
	
	private fun showTabBarView() {
		getMainActivity()?.getHomeFragment()?.showTabbarView()
	}
}