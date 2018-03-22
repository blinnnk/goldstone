package io.goldstone.blockchain.common.base.baseoverlayfragment.overlayview

import android.content.Context
import android.graphics.Color
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.common.value.ContainerID
import org.jetbrains.anko.*

/**
 * @date 22/03/2018 2:40 AM
 * @author KaySaith
 */

class OverlayView(context: Context) : RelativeLayout(context) {

  val header = OverlayHeaderLayout(context)
  var overlayLayout: RelativeLayout
  lateinit var contentLayout: RelativeLayout

  init {

    isClickable = true
    id = ContainerID.overlay

    layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)

    // 内容的容器用来做动画
    overlayLayout = relativeLayout {

      layoutParams = RelativeLayout.LayoutParams(matchParent, 0)

      relativeLayout {
        // header
        addView(header)
        // content
        contentLayout = relativeLayout {
          id = ContainerID.content
          backgroundColor = Color.WHITE
        }.lparams {
          width = matchParent
          height = 200.uiPX()
          topMargin = UIUtils.getHeight(header)
        }
      }

      lparams { alignParentBottom() }
    }

  }

}