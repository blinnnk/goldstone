package io.goldstone.blockchain.common.utils

import android.graphics.LinearGradient
import android.graphics.Shader
import com.blinnnk.uikit.ScreenSize
import io.goldstone.blockchain.R.drawable.*

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

  fun subtractThenHalf(first: Int, second: Int) = (first - second) / 2

  fun generateAvatar(id: Int): Int {
    val avatars = arrayListOf(
      avatar_1,
      avatar_2,
      avatar_3,
      avatar_4,
      avatar_5,
      avatar_6,
      avatar_7,
      avatar_8,
      avatar_9,
      avatar_10
    )
    return avatars[id % 10]
  }
}