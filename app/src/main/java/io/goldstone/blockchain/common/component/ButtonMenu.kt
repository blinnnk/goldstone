package io.goldstone.blockchain.common.component

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView

/**
 * @date 25/04/2018 7:11 AM
 * @author KaySaith
 */

class ButtonMenu(context: Context) : LinearLayout(context) {

  private val menuHeight = 35.uiPX()

  var titles: ArrayList<String> by observing(arrayListOf()) {
    val buttonWidth = layoutParams.width / titles.size
    titles.forEachIndexed { index, title ->
      textView {
        id = index
        layoutParams = LinearLayout.LayoutParams(buttonWidth, menuHeight)
        text = title
        textColor = GrayScale.midGray
        typeface = GoldStoneFont.heavy(context)
        textSize = fontSize(12)
        gravity = Gravity.CENTER
        addTouchRippleAnimation(Color.WHITE, GrayScale.lightGray, RippleMode.Square)
      }
    }
  }

  init {
    layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, menuHeight)
  }

  fun selected(index: Int = 0) {
    getButton {
      it.apply {
        textColor = if (id == index) {
          addTouchRippleAnimation(Spectrum.green, Spectrum.yellow, RippleMode.Square)
          Spectrum.white
        } else {
          addTouchRippleAnimation(Spectrum.white, GrayScale.lightGray, RippleMode.Square)
          GrayScale.midGray
        }
      }
    }
  }

  fun getButton(hold: (TextView) -> Unit) {
    (0 until titles.size).forEach {
      findViewById<TextView>(it)?.apply {
        hold(this)
      }
    }
  }

}