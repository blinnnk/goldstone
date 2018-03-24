package io.goldstone.blockchain.common.base.BaseInfoCell

import android.content.Context
import android.widget.LinearLayout
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.component.RoundIcon
import io.goldstone.blockchain.common.component.TwoLineTitles
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

    this.addView(icon)

    this.addView(info
      .apply {
        setBlackTitles()
        x += 60.uiPX()
      })

    this.addView(count
      .apply {
        isFloatRight = true
        x -= 30.uiPX()
        setBlackTitles()
      })

    icon.setCenterInVertical()
    info.setCenterInVertical()
    count.apply {
      setCenterInVertical()
      setAlignParentRight()
    }

  }

}