package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.animation.updateAlphaAnimation
import com.blinnnk.uikit.uiPX
import com.github.mmin18.widget.RealtimeBlurView
import io.goldstone.blockchain.common.value.HomeSize
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.matchParent

/**
 * @date 21/04/2018 3:56 AM
 * @author KaySaith
 */

@SuppressLint("SetTextI18n")
open class SliderHeader(context: Context) : RelativeLayout(context) {

  private val blurView by lazy { RealtimeBlurView(context, null) }

  init {
    layoutParams = RelativeLayout.LayoutParams(matchParent, 90.uiPX())

    blurView
      .apply {
        alpha = 0f
        setOverlayColor(Spectrum.opacity8Green)
        setBlurRadius(20.uiPX().toFloat())
        layoutParams = LinearLayout.LayoutParams(matchParent, HomeSize.sliderHeaderHeight)
      }
    this.addView(blurView)

  }

  open fun onHeaderShowedStyle() {
    blurView.updateAlphaAnimation(1f)
  }

  open fun onHeaderHidesStyle() {
    blurView.updateAlphaAnimation(0f)
  }

}