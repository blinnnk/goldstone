package io.goldstone.blockchain.common.base.baseInfocell

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.component.RoundIcon
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize

/**
 * @date 24/03/2018 8:41 PM
 * @author KaySaith
 */

open class BaseValueCell(context: Context) : BaseCell(context) {


  protected val icon by lazy { RoundIcon(context) }
  protected val info by lazy { TwoLineTitles(context) }
  protected val count by lazy { TwoLineTitles(context) }

  init {

    layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 75.uiPX()).apply {
      leftMargin = PaddingSize.device
    }

    icon.iconColor = GrayScale.lightGray
    this.addView(icon)

    this.addView(info
      .apply {
        setBlackTitles()
        x += 60.uiPX()
      })


    icon.setCenterInVertical()
    info.setCenterInVertical()

  }

  fun setValueStyle(isScaleIcon: Boolean = false) {

    if (isScaleIcon) icon.scaleType = ImageView.ScaleType.CENTER_INSIDE
    else icon.scaleType = ImageView.ScaleType.CENTER_CROP

    this.addView(count
      .apply {
        isFloatRight = true
        x -= 30.uiPX()
        setBlackTitles()
      })

    count.apply {
      setCenterInVertical()
      setAlignParentRight()
    }

  }

}