package io.goldstone.blockchain.common.component

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.updateColorAnimation
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.uikit.HoneyColor
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.*

/**
 * @date 07/04/2018 12:29 AM
 * @author KaySaith
 */
@SuppressLint("SetTextI18n")
class LoadingView(context: Context) : RelativeLayout(context) {

  private val introView by lazy { TextView(context) }

  init {

    isClickable = true

    layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)

    updateColorAnimation(GrayScale.Opacity1Black, GrayScale.Opacity5Black)

    val size = (ScreenSize.Width * 0.7).toInt()

    relativeLayout {

      addCorner(CornerSize.default.toInt(), Spectrum.white)

      val loading = ProgressBar(this.context, null, R.attr.progressBarStyleInverse).apply {
        indeterminateDrawable.setColorFilter(HoneyColor.Red, android.graphics.PorterDuff.Mode.MULTIPLY)
        lparams {
          width = 80.uiPX()
          height = 80.uiPX()
          centerInParent()
          y -= 30.uiPX()
        }
      }
      addView(loading)

      introView
        .apply {
          textSize = 4.uiPX().toFloat() + 1f
          textColor = GrayScale.gray
          gravity = Gravity.CENTER_HORIZONTAL
          leftPadding = 30.uiPX()
          rightPadding = 30.uiPX()
          lparams {
            width = matchParent
            height = 50.uiPX()
            centerInParent()
            y += 50.uiPX()
          }
        }
        .into(this)

      lparams {
        centerInParent()
        width = size
        height = size
      }
    }

    setIntroText("obtaining token information from ethereum now just wait a moment")
  }

  fun setIntroText(intro: String) {
    introView.text = intro
  }

}