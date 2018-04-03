package io.goldstone.blockchain.common.component

import android.content.Context
import android.widget.RelativeLayout
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.uikit.HoneyColor
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.progressBar

/**
 * @date 01/04/2018 2:22 AM
 * @author KaySaith
 */

class LoadingOverlayView(context: Context) : RelativeLayout(context) {

  init {
    layoutParams = RelativeLayout.LayoutParams((ScreenSize.Width * 0.75).toInt(), 300.uiPX())
    addCorner(CornerSize.big.toInt(), Spectrum.white)
    progressBar {
      indeterminateDrawable.setColorFilter(HoneyColor.Red, android.graphics.PorterDuff.Mode.MULTIPLY)
    }.setCenterInParent()
    elevation = 50.uiPX().toFloat()
    this.setCenterInParent()
  }

}