package io.goldstone.blockchain.module.home.profile.view

import android.content.Context
import android.graphics.Color
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.updateColorAnimation
import com.blinnnk.extension.into
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.ProfileText
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor

/**
 * @date 26/03/2018 12:13 AM
 * @author KaySaith
 */

class ProfileSlideHeader(context: Context) : RelativeLayout(context) {

  private val title = TextView(context)

  init {

    layoutParams = RelativeLayout.LayoutParams(matchParent, 90.uiPX())

    title
      .apply {
        text = ProfileText.profile
        textColor = Spectrum.white
        textSize = 5.uiPX().toFloat()
        typeface = GoldStoneFont.heavy(context)
      }
      .into(this)

    title.setCenterInParent()

  }

  fun onHeaderShowedStyle() {
    updateColorAnimation(Color.TRANSPARENT, Spectrum.green)
  }

  fun onHeaderHidesStyle() {
    updateColorAnimation(Spectrum.green, Color.TRANSPARENT)
  }

}