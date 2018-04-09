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
import com.blinnnk.extension.into
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.backgroundColor
import com.blinnnk.uikit.ScreenSize

/**
 * @date 23/03/2018 1:00 AM
 * @author KaySaith
 */

class MenuBar(context: Context) : LinearLayout(context) {

  private val titles = arrayListOf("mnemonic", "keystore", "private key", "watch only")
  private var totalItemWidth = 0

  init {
    backgroundColor = Spectrum.white
    titles.forEachIndexed { index, string ->
      Item(context)
        .apply {
          id = index
          text = string
          layoutParams = LinearLayout.LayoutParams(getTextWidth(string), 35.uiPX()).apply {
            setMargins(PaddingSize.device, 20.uiPX(), 10.uiPX(), 0)
            totalItemWidth += getTextWidth(string) + 30.uiPX() // 计算总的 `Menu` 宽度
          }
        }
        .into(this)
    }

    layoutParams = LinearLayout.LayoutParams(ScreenSize.Width + 50.uiPX(), 70.uiPX())
  }

  fun selectItem(index: Int) {
    (0 until titles.size).forEach {
      findViewById<Item>(it)?.apply {
        if (it == index) setSelectedStyle(true)
        else setSelectedStyle(false)
      }
    }
  }

  fun floatRight() {
    (0 until titles.size).forEach {
      findViewById<Item>(it)?.apply {
        x -= totalItemWidth - ScreenSize.Width + 12.uiPX().toFloat()
      }
    }
  }

  fun floatLeft() {
    (0 until titles.size).forEach {
      findViewById<Item>(it)?.apply {
        x += totalItemWidth - ScreenSize.Width + 12.uiPX().toFloat()
      }
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
  private var titleColor = GrayScale.black
  private val paint = Paint()
  private val textPaint = Paint()
  private val textSize = 16.uiPX().toFloat()

  init {
    paint.isAntiAlias = true
    paint.style = Paint.Style.FILL
    paint.color = GrayScale.black

    textPaint.isAntiAlias = true
    textPaint.style = Paint.Style.FILL
    textPaint.color = titleColor
    textPaint.textSize = textSize
    textPaint.typeface = GoldStoneFont.heavy(context)
  }

  @SuppressLint("DrawAllocation")
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)

    textPaint.color = GrayScale.midGray

    if (hasUnderLine) {
      val rectF = RectF(0f, height - BorderSize.bold, width.toFloat(), height.toFloat())
      canvas?.drawRect(rectF, paint)
      textPaint.color = GrayScale.black
    }

    val textY = (height + textSize) / 2 - 3.uiPX()
    canvas?.drawText(text, 0f, textY, textPaint)

  }

  fun setSelectedStyle(isSelect: Boolean) {
    hasUnderLine = isSelect
    invalidate()
  }

}