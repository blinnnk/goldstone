package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.Grayscale
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent

/**
 * @date 22/03/2018 11:43 PM
 * @author KaySaith
 */

class AttentionTextView(context: Context) : TextView(context) {

  init {
    textSize = 4.uiPX().toFloat()
    textColor = Grayscale.midGray
    typeface = GoldStoneFont.medium(context)
    layoutParams = LinearLayout.LayoutParams(ScreenSize.Width -  30.uiPX() * 2, wrapContent).apply {
      leftMargin = 30.uiPX()
      topMargin = 50.uiPX()
    }
    gravity = Gravity.CENTER
  }

}