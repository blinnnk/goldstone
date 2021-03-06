package io.goldstone.blinnnk.common.base.baseoverlayfragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.blinnnk.extension.hideChildFragment
import com.blinnnk.extension.removeChildFragment
import com.blinnnk.extension.showChildFragment
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blinnnk.common.utils.getMainActivity
import io.goldstone.blinnnk.common.value.ContainerID

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
	 */
	inline fun <reified R : Fragment> popFragmentFrom(
		showCloseButton: Boolean = true,
		viewPagerSize: Int = 0
	) {
		fragment.apply {
			childFragmentManager.fragments.apply {
				if (last() is R) removeChildFragment(last())
				// 组内只有一个 `Fragment` 的时候销毁掉回退按钮
				if (size == 2 || viewPagerSize > 0) {
					showBackButton(false) {}
					showCloseButton(showCloseButton) {
						presenter.removeSelfFromActivity()
					}
				}

				when {
					viewPagerSize > 0 -> (0 until size).forEach {
						if (this[it].isHidden) showChildFragment(this[it])
					}
					size >= 2 -> this[size - 2]?.let {
						showChildFragment(it)
					}
					else -> this[size - 1]?.let {
						showChildFragment(it)
					}
				}
			}
		}
	}

	inline fun <reified T : Fragment> showTargetFragment(bundle: Bundle = Bundle(), viewPagerSize: Int = 0) {
		fragment.apply {
			if (viewPagerSize > 0) {
				childFragmentManager.fragments.apply {
					forEachIndexed { index, fragment ->
						if (index in lastIndex - viewPagerSize .. lastIndex) {
							hideChildFragment(fragment)
						}
					}
					addSubFragment<T>(bundle, viewPagerSize)
				}
			} else {
				try {
					childFragmentManager.fragments.last()?.let {
						hideChildFragment(it)
						addSubFragment<T>(bundle, 0)
					}
				} catch (error: Exception) {
					Log.e("showTargetFragment", error.message)
				}
			}
		}
	}

	inline fun <reified T : Fragment> BaseOverlayFragment<*>.addSubFragment(
		bundle: Bundle,
		viewPagerSize: Int
	) {
		addFragmentAndSetArgument<T>(ContainerID.content) {
			putAll(bundle)
		}
		showBackButton(true) {
			popFragmentFrom<T>(true, viewPagerSize)
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