package io.goldstone.blockchain.common.base.baseoverlayfragment

import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.utils.removeSelfWithAnimation

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