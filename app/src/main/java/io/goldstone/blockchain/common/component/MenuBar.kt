package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.BorderSize
import io.goldstone.blockchain.common.value.Grayscale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent

/**
 * @date 23/03/2018 1:00 AM
 * @author KaySaith
 */

class MenuBar(context: Context) : LinearLayout(context) {

  private val titles = arrayListOf("mnemonic", "keystore", "private key", "watch only")

  init {

    layoutParams = LinearLayout.LayoutParams(matchParent, 70.uiPX())
    backgroundColor = Spectrum.white
    titles.forEach {
      addView(Item(context).apply {
        if (it == titles[0]) { setUnSelectedStyle(true) }
        text = it
        layoutParams = LinearLayout.LayoutParams(getTextWidth(it), 35.uiPX()).apply {
          leftMargin = PaddingSize.device
          rightMargin = 10.uiPX()
          topMargin = 20.uiPX()
        }
      })
    }
  }

  private fun getTextWidth(text: String): Int {
    val textPaint = Paint()
    textPaint.textSize = 16.uiPX().toFloat()
    textPaint.typeface = GoldStoneFont.heavy(context)
    return textPaint.measureText(text).toInt()
  }

}

private class Item(context: Context) : View(context) {

  var text by observing("") {
    invalidate()
  }

  private var hasUnderLine = false
  private var titleColor = Grayscale.black
  private val paint = Paint()
  private val textPaint = Paint()
  private val textSize = 16.uiPX().toFloat()

  init {
    paint.isAntiAlias = true
    paint.style = Paint.Style.FILL
    paint.color = Grayscale.black

    textPaint.isAntiAlias = true
    textPaint.style = Paint.Style.FILL
    textPaint.color = titleColor
    textPaint.textSize = textSize
    textPaint.typeface = GoldStoneFont.heavy(context)
  }

  @SuppressLint("DrawAllocation")
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)

    textPaint.color = Grayscale.midGray

    if (hasUnderLine) {
      val rectF = RectF(0f, height - BorderSize.bold, width.toFloat(), height.toFloat())
      canvas?.drawRect(rectF, paint)
      textPaint.color = Grayscale.black
    }

    val textY = (height + textSize) / 2 - 3.uiPX()
    canvas?.drawText(text, 0f, textY, textPaint)

  }

  fun setUnSelectedStyle(isSelect: Boolean) {
    hasUnderLine = isSelect
    invalidate()
  }

}