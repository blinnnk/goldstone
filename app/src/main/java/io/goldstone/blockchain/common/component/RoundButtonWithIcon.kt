package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.HoneySvgPathConvert
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.ShadowSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.SvgPath
import org.jetbrains.anko.backgroundColor

/**
 * @date 23/03/2018 5:06 PM
 * @author KaySaith
 */

class RoundButtonWithIcon(context: Context) : View(context) {

  var text by observing("") {
    invalidate()
  }

  private val viewHeight = 30.uiPX()

  init {
    layoutParams = LinearLayout.LayoutParams(220.uiPX(), viewHeight)
    backgroundColor = Color.WHITE
    addTouchRippleAnimation(Spectrum.green, Spectrum.yellow, RippleMode.Square, viewHeight / 2f)
    elevation = ShadowSize.Button
  }

  private val titleSize = 11.uiPX().toFloat()

  private val textPaint = Paint().apply {
    isAntiAlias = true
    style = Paint.Style.FILL
    color = Spectrum.white
    typeface = GoldStoneFont.heavy(context)
    textSize = titleSize
  }

  private val iconPaint = Paint().apply {
    isAntiAlias = true
    style = Paint.Style.FILL
    color = Spectrum.white
  }

  private val path = HoneySvgPathConvert()
  private val arrow = path.parser(SvgPath.arrow)

  @SuppressLint("DrawAllocation")
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)

    canvas?.save()
    canvas?.translate(width - 24.uiPX().toFloat(), height / 2f - 7.uiPX())
    canvas?.drawPath(arrow, iconPaint)
    canvas?.restore()

    val textY = (height + titleSize) / 2f - 2.uiPX()
    val textX = (width - textPaint.measureText(text)) / 2 - 3.uiPX()
    canvas?.drawText(text, textX, textY, textPaint)

  }

}