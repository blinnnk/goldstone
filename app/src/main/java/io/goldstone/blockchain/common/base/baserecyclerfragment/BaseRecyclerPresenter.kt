package io.goldstone.blockchain.common.base.baserecyclerfragment

import com.blinnnk.animation.updateHeightAnimation
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.getScreenHeightWithoutStatusBar
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter

/**
 * @date 23/03/2018 3:46 PM
 * @author KaySaith
 */

abstract class BaseRecyclerPresenter<out T : BaseRecyclerFragment<BaseRecyclerPresenter<T, D>, D>, D> {

  abstract val fragment: T

  /**
   * @description
   * 在依赖的 `Fragment` 的对应的生命周期提供的依赖方法
   * @param
   * [fragment] 这个就是依赖的 `Fragment` 实体
   * */

  open fun onFragmentAttach() {
    // Do Something
  }

  open fun onFragmentCreate() {
    // Do Something
  }

  open fun onFragmentCreateView() {
    // Do Something
  }

  open fun onFragmentDetach() {
    // Do Something
  }

  open fun onFragmentViewCreated() {
    // Do Something
  }

  /**
   * @description
   * 这个方法会在 [BaseRecyclerFragment] 中根据 `setSlideUpWithCellHeight` 的设定
   * 状态而决定是否执行.
   */
  open fun updateParentContentLayoutHeight(
    dataCount: Int = 0,
    cellHeight: Int = 0,
    maxHeight: Int = fragment.activity?.getScreenHeightWithoutStatusBar().orZero()
  ) {
    val targetHeight =
      if (dataCount == 0) fragment.activity?.getScreenHeightWithoutStatusBar().orZero()
      else dataCount * cellHeight
    fragment.getParentFragment<BaseOverlayFragment<BaseOverlayPresenter<*>>> {
      overlayView.contentLayout.updateHeightAnimation(
        targetHeight,
        maxHeight
      )
    }
  }

  fun recoveryFragmentHeight() {
    fragment.getParentFragment<BaseOverlayFragment<BaseOverlayPresenter<*>>> {
      overlayView.contentLayout.updateHeightAnimation(
        fragment.asyncData?.size.orZero() * fragment.setSlideUpWithCellHeight().orZero(),
        fragment.activity?.getScreenHeightWithoutStatusBar().orZero()
      )
    }
  }

}