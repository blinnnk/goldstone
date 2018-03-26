package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.View
import com.blinnnk.extension.getRealScreenHeight
import com.blinnnk.extension.orZero
import com.blinnnk.uikit.ScreenSize
import io.goldstone.blockchain.common.utils.UIUtils.setGradientColor
import io.goldstone.blockchain.common.value.GradientType

/**
 * @date 21/03/2018 8:54 PM
 * @author KaySaith
 */

class GradientView(context: Context) : View(context) {

  private val paint = Paint()
  private var shaderStyle: LinearGradient? = null

  @SuppressLint("DrawAllocation")
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())

    shaderStyle?.let { paint.shader = it }
    canvas?.drawRect(rectF, paint)
  }

  // Set Style through `GradientType`
  fun setStyle(type: GradientType, distance: Int = context.getRealScreenHeight().orZero()) {
    shaderStyle = when(type) {
      GradientType.Blue ->
        setGradientColor(Color.parseColor("#FF0E2552"), Color.parseColor("#FF4FA9C8"))
      GradientType.PinkToYellow ->
        setGradientColor(Color.parseColor("#FF00FF80"), Color.parseColor("#FF0076FF"))
      GradientType.BlueGreen ->
        LinearGradient(0f, 0f, ScreenSize.Width.toFloat(), distance.toFloat(),
          intArrayOf(Color.rgb(2, 209, 142), Color.rgb(1, 128, 175)),
          floatArrayOf(0f, distance * 1.3f),
          Shader.TileMode.CLAMP)
      GradientType.DarkGreen ->
        LinearGradient(0f, 0f, ScreenSize.Width.toFloat(), distance.toFloat(),
          intArrayOf(Color.parseColor("#FF204E4D"), Color.parseColor("#FF40857B")),
          floatArrayOf(0f, distance * 1f),
          Shader.TileMode.CLAMP)

    }
  }

}