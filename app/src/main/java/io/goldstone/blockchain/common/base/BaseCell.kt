package io.goldstone.blockchain.common.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.HoneySvgPathConvert
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.value.*

/**
 * @date 23/03/2018 11:46 PM
 * @author KaySaith
 */

open class BaseCell(context: Context) : RelativeLayout(context) {

  var hasArrow: Boolean by observing(true) {
    invalidate()
  }

  private val paint = Paint().apply {
    isAntiAlias = true
    style = Paint.Style.FILL
    color = Spectrum.opacity2White
  }

  private val iconPaint = Paint().apply {
    isAntiAlias = true
    style = Paint.Style.FILL
    color = Spectrum.opacity5White
  }

  private val path = HoneySvgPathConvert()
  private val arrow = path.parser(SvgPath.arrow)

  init {
    this.setWillNotDraw(false)

    layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 60.uiPX()).apply {
      leftMargin = PaddingSize.device
    }
  }

  @SuppressLint("DrawAllocation")
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)

    if (hasArrow) {
      canvas?.save()
      canvas?.translate(width - 16.uiPX().toFloat(), height / 2f - 7.uiPX())
      canvas?.drawPath(arrow, iconPaint)
      canvas?.restore()
    }

    canvas?.drawLine(
      0f,
      height - BorderSize.default,
      width.toFloat(),
      height - BorderSize.default,
      paint
    )
  }

  fun setGrayStyle() {
    iconPaint.color = GrayScale.Opacity1Black
    paint.color = GrayScale.Opacity1Black
    invalidate()
    addTouchRippleAnimation(Color.WHITE, GrayScale.lightGray, RippleMode.Square)
  }

}