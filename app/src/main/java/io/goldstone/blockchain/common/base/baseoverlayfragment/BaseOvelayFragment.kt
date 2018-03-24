package io.goldstone.blockchain.common.base.baseoverlayfragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.blinnnk.animation.updateHeightAnimation
import com.blinnnk.extension.getRealScreenHeight
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.baseoverlayfragment.overlayview.OverlayView
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.UI

/**
 * @date 22/03/2018 2:28 AM
 * @author KaySaith
 */

abstract class BaseOverlayFragment<out T : BaseOverlayPresenter<BaseOverlayFragment<T>>> : Fragment() {

  abstract val presenter: T
  abstract fun setContentHeight(): Int
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

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    presenter.onFragmentAttach()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    presenter.onFragmentCreateView()
    return UI {
      overlayView = OverlayView(context!!)
      overlayView.contentLayout.initView()
      addView(overlayView, RelativeLayout.LayoutParams(matchParent, minHeight))

      /** 执行动画 */
      overlayView.contentLayout
        .updateHeightAnimation(setContentHeight(), context?.getRealScreenHeight())

      /** 设置悬浮曾的 `Header` 初始状态 */
      overlayView.header.showBackButton(hasBackButton)
      overlayView.header.showCloseButton(hasCloseButton)

    }.view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    /** 设定标题的时机 */
    overlayView.header.title.text = headerTitle

    /** 关闭悬浮曾 */
    overlayView.header.closeButton.apply {
      onClick {
        presenter.removeSelfFromActivity()
        preventDuplicateClicks()
      }
    }

    /** 返回上一级 */
    overlayView.header.backButton.onClick {
      presenter.backToLastFragment()
    }
    presenter.onFragmentViewCreated()
  }

  override fun onDetach() {
    super.onDetach()
    presenter.onFragmentDetach()
  }

  override fun onResume() {
    super.onResume()
    presenter.onFragmentResume()
  }

}