package io.goldstone.blockchain.common.base.baseoverlayfragment.overlayview

import android.content.Context
import android.graphics.Color
import android.widget.RelativeLayout
import com.blinnnk.animation.updateColorAnimation
import com.blinnnk.extension.addTopLRCorner
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.HoneyUIUtils
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.alignParentBottom
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout

/**
 * @date 22/03/2018 2:40 AM
 * @author KaySaith
 */

class OverlayView(context: Context) : RelativeLayout(context) {

  val header = OverlayHeaderLayout(context)
  var overlayLayout: RelativeLayout
  lateinit var contentLayout: RelativeLayout

  init {

    id = ContainerID.overlay

    // 内容的容器用来做动画
    overlayLayout = relativeLayout {

      layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
      isClickable = true

      relativeLayout {
        addTopLRCorner(CornerSize.big, Spectrum.white)
        // header
        addView(header)
        // content
        contentLayout = relativeLayout {
          id = ContainerID.content
        }.lparams {
          width = matchParent
          height = 200.uiPX()
          topMargin = HoneyUIUtils.getHeight(header)
        }

        lparams { alignParentBottom() }
      }
    }

    // 背景防止点击的 `mask` 颜色动画
    updateColorAnimation(Color.TRANSPARENT, GrayScale.Opacity2Black)

  }

}