package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView

/**
 * @date 23/03/2018 11:32 PM
 * @author KaySaith
 */

class TwoLineTitles(context: Context) : LinearLayout(context) {

  val title = textView {
    textSize = 5.uiPX().toFloat()
    textColor = Spectrum.white
  }

  val subtitle = textView {
    textSize = 4.uiPX().toFloat()
    typeface = GoldStoneFont.medium(context)
    textColor = Spectrum.opacity5White
    y -= 2.uiPX()
  }

  var isFloatRight by observing(false) {
    gravity = Gravity.END
  }

  var isCenter by observing(false) {
    gravity = Gravity.CENTER_HORIZONTAL
    subtitle.gravity = Gravity.CENTER_HORIZONTAL
    title.gravity = Gravity.CENTER_HORIZONTAL
  }

  init {
    orientation = VERTICAL
  }

  fun setBlackTitles() {
    title.apply {
      typeface = GoldStoneFont.heavy(context)
      textColor = GrayScale.black
    }
    subtitle.textColor = GrayScale.gray
  }

  fun setColorStyle(color: Int) {
    title.textColor = color
    subtitle.textColor = color
  }

  fun setNormalTitles() {
    title.apply {
      typeface = GoldStoneFont.book(context)
      textColor = GrayScale.black
    }
    subtitle.textColor = GrayScale.gray
  }

  fun setGrayTitles() {
    title.apply {
      typeface = GoldStoneFont.book(context)
      textColor = GrayScale.black
    }
    title.textColor = GrayScale.gray
    subtitle.y += 3.uiPX()
    subtitle.textColor = GrayScale.midGray
  }

  fun setSmallStyle() {
    title.textSize = 4.uiPX() + 1f
    subtitle.textSize = 3.uiPX() + 1f
    title.y += 1.uiPX()
  }

  fun setWildStyle() {
    title.typeface = GoldStoneFont.heavy(context)
    subtitle.y += 5.uiPX()
  }

  fun setQuotationStyle() {
    y += 10.uiPX()
    title.apply {
      textSize = 8.uiPX().toFloat()
      typeface = GoldStoneFont.black(context)
      textColor = GrayScale.black
    }
    subtitle.apply {
      textSize = 5.uiPX().toFloat()
      textColor = GrayScale.midGray
      typeface = GoldStoneFont.medium(context)
      y -= 8.uiPX()
    }
  }

  fun getSubtitleValue() = subtitle.text.toString()

}