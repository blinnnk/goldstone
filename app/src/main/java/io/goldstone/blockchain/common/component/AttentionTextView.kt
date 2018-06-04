package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent

/**
 * @date 22/03/2018 11:43 PM
 * @author KaySaith
 */

class AttentionTextView(context: Context) : TextView(context) {

  init {
    id = ElementID.attentionText
    textSize = fontSize(14)
    textColor = GrayScale.midGray
    typeface = GoldStoneFont.medium(context)
    layoutParams = LinearLayout.LayoutParams(ScreenSize.Width -  30.uiPX() * 2, wrapContent).apply {
      topMargin = 50.uiPX()
    }
    gravity = Gravity.CENTER
  }

}