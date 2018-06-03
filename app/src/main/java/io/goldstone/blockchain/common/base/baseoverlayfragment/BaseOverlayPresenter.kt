package io.goldstone.blockchain.common.base.baseoverlayfragment

import android.os.Bundle
import android.support.v4.app.Fragment
import com.blinnnk.extension.hideChildFragment
import com.blinnnk.extension.removeChildFragment
import com.blinnnk.extension.showChildFragment
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ContainerID

/**
 * @date 22/03/2018 2:29 AM
 * @author KaySaith
 */
abstract class BaseOverlayPresenter<out T : BaseOverlayFragment<*>> {
	
	abstract val fragment: T
	
	open fun removeSelfFromActivity() {
		fragment.removeSelf()
		/**
		 * 空判断有键盘就销毁没有键盘就不向下执行.`ContentOverlay` 带键盘场景较多增加了 `Super` 方法
		 */
		fragment.activity?.apply {
			SoftKeyboard.hide(this)
		}
		fragment.getMainActivity()?.backEvent = null
	}
	
	/**
	 * 为了体验, 之前的 `Fragment` 都是使用隐藏,
	 * @important 当级别超过 `2` 层记得去从隐藏到显示
	 * 状态的 `Fragment` 中, 在 `presenter.onFragmentShowFromHidden` 方法
	 * 中重设回退按钮点击状态
	 * @example [TransactionDetailPresenter.onFragmentShowFromHidden]
	 */
	inline fun <reified R : Fragment> popFragmentFrom() {
		fragment.apply {
			childFragmentManager.fragments.apply {
				if (last() is R) removeChildFragment(last())
				// 组内只有一个 `Fragment` 的时候销毁掉回退按钮
				if (size == 2) {
					overlayView.header.apply {
						showBackButton(false)
						showCloseButton(true)
					}
				}
				// 恢复 `TransactionListFragment` 的视图
				this[size - 2]?.let {
					showChildFragment(it)
					// 两种不同的父级进行高度恢复
					if (it is BaseRecyclerFragment<*, *>) {
						it.presenter.recoveryFragmentHeight()
					} else if (it is BaseFragment<*>) {
						it.presenter.recoveryFragmentHeight()
					}
				}
			}
		}
	}
	
	inline fun <reified T : Fragment> showTargetFragment(
		title: String,
		previousTitle: String,
		bundle: Bundle = Bundle()
	) {
		fragment.apply {
			headerTitle = title
			childFragmentManager.fragments.last()?.let {
				hideChildFragment(it)
				addFragmentAndSetArgument<T>(ContainerID.content) {
					putAll(bundle)
				}
				overlayView.header.apply {
					showBackButton(true) {
						headerTitle = previousTitle
						popFragmentFrom<T>()
					}
					showCloseButton(false)
				}
			}
		}
	}
	
	fun onFragmentAttach() {
		// Do Something
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
	
	open fun onFragmentDestroy() {
		// Do Something
		fragment.getMainActivity()?.showHomeFragment()
	}
	
	open fun onFragmentResume() {
		// Do Something
	}
	
	open fun onFragmentShowFromHidden() {
		// Do Something
	}
}

fun Fragment.removeSelf(callback: () -> Unit = { }) {
	activity?.let {
		it.supportFragmentManager.beginTransaction().remove(this).commit()
		callback()
	}
}