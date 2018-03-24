package io.goldstone.blockchain.common.utils

import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.View
import android.view.WindowManager
import com.blinnnk.uikit.ScreenSize

/**
 * @date 21/03/2018 9:07 PM
 * @author KaySaith
 */

object UIUtils {

  // easy to set gradient color
  fun setGradientColor(
    startColor: Int,
    endColor: Int,
    width: Float = ScreenSize.Width.toFloat(),
    height: Float = ScreenSize.Height.toFloat()
  ) = LinearGradient(0f,0f, width, height, startColor, endColor, Shader.TileMode.CLAMP)
}