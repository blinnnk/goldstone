package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.BorderSize
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor

/**
 * @date 25/04/2018 8:06 AM
 * @author KaySaith
 */

open class MarketTokenDetailBaseCell(context: Context) : RelativeLayout(context) {

  protected val title = TextView(context).apply {
    textSize = fontSize(11)
    textColor = GrayScale.gray
    typeface = GoldStoneFont.medium(context)
    layoutParams = LinearLayout.LayoutParams(matchParent, 30.uiPX())
    setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
  }

  var showTopLine: Boolean = false

  init {
    this.setWillNotDraw(false)
    layoutParams = LinearLayout.LayoutParams(matchParent, 80.uiPX())
    this.addView(title)
  }

  private val paint = Paint().apply {
    isAntiAlias = true
    color = GrayScale.lightGray
    style = Paint.Style.FILL
  }
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    showTopLine.isTrue {
      canvas?.drawLine(0f, 0f, width.toFloat(), BorderSize.default, paint)
    }
    canvas?.drawLine(0f, height - BorderSize.default, width.toFloat(), height.toFloat(), paint)
  }

}