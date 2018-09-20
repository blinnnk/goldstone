package io.goldstone.blockchain.common.base.basefragment

import android.os.Bundle
import android.support.v4.app.Fragment
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.hideChildFragment
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity

/**
 * @date 22/03/2018 2:58 AM
 * @author KaySaith
 */
abstract class BasePresenter<out T : BaseFragment<*>> {

	abstract val fragment: T

	open fun onFragmentAttach() {
		// Do Something When fragment Attach
	}

	open fun onFragmentCreateView() {
		// Do Something When fragment Attach
	}

	open fun onFragmentViewCreated() {
		// Do Something
	}

	open fun onFragmentDetach() {
		// Do Something
	}

	open fun onFragmentResume() {
		// Do Something
	}

	open fun onFragmentShowFromHidden() {
		// Do Something
	}

	open fun onFragmentDestroy() {
	}

	// 当 `BaseFragment` 加载在 `BaseOverlayFragment` 的时候提供支持回退的加载卸载方法
	inline fun <reified T : Fragment, reified Parent : BaseOverlayFragment<*>> showTargetFragment(
		title: String,
		popTitle: String,
		arguments: Bundle? = null,
		hasBackButton: Boolean = true
	) {
		if (fragment.parentFragment is Parent) {
			fragment.getParentFragment<Parent>()?.apply {
				// 隐藏当前 `Fragment` 节省内存
				hideChildFragment(fragment)
				addFragmentAndSetArgument<T>(ContainerID.content) {
					arguments?.let { putAll(it) }
				}
				overlayView.header.apply {
					showBackButton(hasBackButton) {
						presenter.popFragmentFrom<T>()
						headerTitle = popTitle
					}
					showCloseButton(!hasBackButton)
				}
				headerTitle = title
			}
		}
	}

	companion object {

		// SplashActivity 的回退栈在公用组件下的特殊设定
		inline fun <reified T : BaseOverlayFragment<*>> setRootChildFragmentBackEvent(
			fragment: Fragment
		) {
			fragment.activity?.let {
				if (it is SplashActivity) {
					fragment.getParentFragment<T> {
						it.backEvent = Runnable {
							presenter.removeSelfFromActivity()
						}
					}
				}
			}
		}
	}
}