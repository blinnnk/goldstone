package io.goldstone.blockchain.common.base.baseoverlayfragment

import android.support.v4.app.Fragment
import com.blinnnk.extension.removeChildFragment
import com.blinnnk.extension.removeSelfWithAnimation
import com.blinnnk.extension.showChildFragment
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment

/**
 * @date 22/03/2018 2:29 AM
 * @author KaySaith
 */

abstract class BaseOverlayPresenter<out T : BaseOverlayFragment<*>> {

  abstract val fragment: T

  open fun removeSelfFromActivity() {
    fragment.removeSelfWithAnimation(fragment.overlayView.overlayLayout)

    /**
     * 空判断有键盘就销毁没有键盘就不向下执行.`ContentOverlay` 带键盘场景较多增加了 `Super` 方法
     */
    fragment.activity?.apply {
      SoftKeyboard.hide(this)
    }
  }

  /**
   * 这个方法目前只支持 `2` 级内的跳转, 因为业务大多数是 `2` 级跳, 所以简单封装
   */
  inline fun<reified R: Fragment> popFragmentFrom() {
    fragment.apply {
      childFragmentManager.fragments.apply {
        if (last() is R) removeChildFragment(last())
        overlayView.header.apply {
          showBackButton(false)
          showCloseButton(true)
        }
        // 恢复 `TransactionListFragment` 的视图
        this[size - 2].let {
          if (it is BaseRecyclerFragment<*, *>) {
            showChildFragment(it)
            it.presenter.recoveryFragmentHeight()
          }
        }
      }
    }
  }

  open fun backToLastFragment() {
    // Do Something
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

  open fun onFragmentResume() {
    // Do Something
  }

}