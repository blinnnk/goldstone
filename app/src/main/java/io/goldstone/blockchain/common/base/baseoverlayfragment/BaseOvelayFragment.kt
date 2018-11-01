package io.goldstone.blockchain.common.base.baseoverlayfragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.blinnnk.extension.hideStatusBar
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.overlayview.OverlayHeaderLayout
import io.goldstone.blockchain.common.base.baseoverlayfragment.overlayview.OverlayView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.setTransparentStatusBar
import io.goldstone.blockchain.common.value.HomeSize
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.HomeFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI

/**
 * @date 22/03/2018 2:28 AM
 * @author KaySaith
 */
abstract class BaseOverlayFragment<out T : BaseOverlayPresenter<BaseOverlayFragment<T>>>
	: Fragment() {

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
			header.layoutParams.height = HomeSize.headerHeight
			contentLayout.setMargins<RelativeLayout.LayoutParams> {
				topMargin = HomeSize.headerHeight
			}
		}
	}

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		presenter.onFragmentAttach()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		// 全屏幕悬浮层展开的时候隐藏 `StatusBar`
		activity?.apply {
			if (!SharedWallet.isNotchScreen()) {
				hideStatusBar()
			}
		}
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
			addView(overlayView, RelativeLayout.LayoutParams(matchParent, matchParent))

			overlayView.apply {
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
		showHomeFragment(false)
		hideTabBarToAvoidOverdraw()
	}

	fun showAddButton(
		status: Boolean,
		isLeft: Boolean = true,
		clickEvent: () -> Unit = {}
	) {
		overlayView.header.showAddButton(status, isLeft, clickEvent)
	}

	private fun showHomeFragment(isShow: Boolean) {
		activity?.apply {
			if (this is MainActivity) {
				if (isShow) showHomeFragment()
				else hideHomeFragment()
			}
		}
	}

	override fun onDetach() {
		super.onDetach()
		presenter.onFragmentDetach()
		showHomeFragment(true)
		showTabBarView()
	}

	open fun setTransparentStatus() {
		activity?.apply {
			if (!SharedWallet.isNotchScreen()) {
				setTransparentStatusBar()
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		presenter.onFragmentDestroy()
		setBackEvent()
		setTransparentStatus()
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

	open fun setBackEvent() {
		// 恢复 `HomeFragment` 的 `ChildFragment` 的回退栈事件
		activity?.apply {
			when (this) {
				is MainActivity -> {
					supportFragmentManager.fragments.last()?.let { lastChild ->
						if (lastChild is HomeFragment) {
							lastChild.childFragmentManager.fragments.last()?.let {
								backEvent = if (it !is WalletDetailFragment) {
									Runnable {
										lastChild.presenter.showWalletDetailFragment()
									}
								} else {
									null
								}
							}
						} else {
							try {
								lastChild.childFragmentManager.fragments.last()?.apply {
									when (this) {
										is BaseFragment<*> -> recoveryBackEvent()
										is BaseRecyclerFragment<*, *> -> recoveryBackEvent()
									}
								}
							} catch (error: Exception) {
								LogUtil.error(javaClass.simpleName, error)
							}
						}
					}
				}

				is SplashActivity -> backEvent = null
			}
		}
	}

	private fun hideTabBarToAvoidOverdraw() {
		getMainActivity()?.getHomeFragment()?.hideTabbarView()
	}

	private fun showTabBarView() {
		getMainActivity()?.getHomeFragment()?.showTabbarView()
	}
}