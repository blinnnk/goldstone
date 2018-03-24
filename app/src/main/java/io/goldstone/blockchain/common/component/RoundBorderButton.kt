package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.BorderSize
import io.goldstone.blockchain.common.value.Spectrum

/**
 * @date 23/03/2018 10:31 PM
 * @author KaySaith
 */

class RoundBorderButton(context: Context) : View(context) {

  var text by observing("") {
    invalidate()
  }

  var touchColor: Int by observing(0) {
    addTouchRippleAnimation(Color.TRANSPARENT, touchColor, RippleMode.Round)
  }

  private val paint = Paint().apply {
    isAntiAlias = true
    style = Paint.Style.STROKE
    strokeWidth = BorderSize.default
    color = Spectrum.white
  }

  private val titleSize = 11.uiPX().toFloat()

  private val textPaint = Paint().apply {
    isAntiAlias = true
    style = Paint.Style.FILL
    color = Spectrum.white
    textSize = titleSize
    typeface = GoldStoneFont.heavy(context)
  }

  @SuppressLint("DrawAllocation")
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)

    val rectF = RectF(
      BorderSize.default,
      BorderSize.default,
      width - BorderSize.default,
      height - BorderSize.default
    )

    canvas?.drawRoundRect(rectF, height / 2f, height / 2f, paint)

    val textX = (width - textPaint.measureText(text)) / 2f
    val textY = (height + titleSize) / 2f - 2.uiPX()
    canvas?.drawText(text, textX, textY, textPaint)

  }

}