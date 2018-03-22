package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.BorderSize
import io.goldstone.blockchain.common.value.Grayscale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.Spectrum

/**
 * @date 22/03/2018 3:52 PM
 * @author KaySaith
 */

class AgreementView(context: Context) : View(context) {

  val text = "Agree on Terms of service and privacy policy"

  private val paint = Paint()
  private val radius = 5.uiPX().toFloat()

  private val textPaint = Paint()

  init {
    paint.isAntiAlias = true
    paint.style = Paint.Style.STROKE
    paint.color = Grayscale.midGray
    paint.strokeWidth = BorderSize.bold

    textPaint.isAntiAlias = true
    textPaint.style = Paint.Style.FILL
    textPaint.color = Grayscale.gray
    textPaint.textSize = 11.uiPX().toFloat()
    textPaint.typeface = GoldStoneFont.light(context)

    layoutParams = LinearLayout.LayoutParams(ScreenSize.Width, 30.uiPX()).apply {
      topMargin = 20.uiPX()
    }
  }

  @SuppressLint("DrawAllocation")
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    val left = (width - textPaint.measureText(text) - radius * 2) / 2
    canvas?.drawCircle(radius + left, radius + 3.uiPX().toFloat(), radius, paint)
    var textX = 15.uiPX().toFloat()
    text.forEachIndexed { index, char ->
      if (index <= 8) textPaint.color = Grayscale.midGray
      else textPaint.color = Spectrum.blue
      canvas?.drawText(char.toString(), textX + left, 11.uiPX().toFloat(), textPaint)
      textX += textPaint.measureText(char.toString())
    }

  }

}