package io.goldstone.blockchain.common.base.basefragment

import android.os.Bundle
import android.support.v4.app.Fragment
import com.blinnnk.animation.updateHeightAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.AnimationDuration
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.home.home.view.MainActivity

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
		try {
			fragment.getMainActivity()?.showHomeFragment()
		} catch (error: Exception) {
			LogUtil.error("BasePresenter showHomeFragment", error)
		}
	}
	
	fun recoveryFragmentHeight() {
		fragment.getParentFragment<BaseOverlayFragment<BaseOverlayPresenter<*>>> {
			overlayView.contentLayout.updateHeightAnimation(
				fragment.activity?.getScreenHeightWithoutStatusBar().orZero()
			)
		}
		AnimationDuration.Default timeUpThen {
			fragment.getMainActivity()?.hideHomeFragment()
		}
	}
	
	fun updateHeight(height: Int) {
		fragment.getParentFragment<BaseOverlayFragment<BaseOverlayPresenter<*>>> {
			overlayView.contentLayout.updateHeightAnimation(height)
		}
		// 优化重汇
		if (height >= ScreenSize.Height) {
			AnimationDuration.Default timeUpThen {
				fragment.getMainActivity()?.hideHomeFragment()
			}
		} else {
			fragment.getMainActivity()?.showHomeFragment()
		}
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
}