package io.goldstone.blockchain.common.component

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.*

/**
 * @date 24/03/2018 12:54 AM
 * @author KaySaith
 */

class CircleButton(context: Context) : LinearLayout(context) {

  var title: String by observing("") {
    buttonTitle.text = title
  }

  var src: Int by observing(0) {
    icon.imageResource = src
  }

  private lateinit var iconView: LinearLayout
  private val icon by lazy { ImageView(context) }
  private val buttonTitle by lazy { TextView(context) }

  init {

    orientation = VERTICAL
    layoutParams = LinearLayout.LayoutParams(30.uiPX(), 55.uiPX())

    relativeLayout {
      // 透明背景色
      layoutParams = RelativeLayout.LayoutParams(30.uiPX(), 30.uiPX())

      iconView = linearLayout {
        lparams(matchParent, matchParent)
        addCorner(18.uiPX(), Spectrum.opacity2White)
      }
      // ICON 图形
      icon
        .apply {
          layoutParams = LinearLayout.LayoutParams(matchParent, 30.uiPX())
          setColorFilter(Spectrum.white)
          scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
        .into(this)
    }

    buttonTitle
      .apply {
        layoutParams = LinearLayout.LayoutParams(matchParent, 25.uiPX())
        textSize = 3.uiPX().toFloat()
        typeface = GoldStoneFont.book(context)
        textColor = Spectrum.opacity5White
        gravity = Gravity.CENTER_HORIZONTAL
        y += 3.uiPX()
      }
      .into(this)

    addTouchRippleAnimation(Color.TRANSPARENT, Spectrum.yellow, RippleMode.Round)
  }

  fun setIconViewColor(color: Int) {
    iconView.addCorner(18.uiPX(), color)
  }

  fun setUnTransparent() {
    buttonTitle.textColor = Spectrum.white
    setIconViewColor(Color.TRANSPARENT)
  }

  fun setDefaultStyle() {
    buttonTitle.textColor = Spectrum.opacity5White
    setIconViewColor(Spectrum.opacity2White)
  }

}