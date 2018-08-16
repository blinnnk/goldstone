package io.goldstone.blockchain.common.component.cell

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.RelativeLayout.BELOW
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*

/**
 * @date 25/04/2018 8:06 AM
 * @author KaySaith
 * @rewriteDate 10/08/2018 16:13 PM
 * @rewriter wcx
 * @description 添加addCustomizeView添加view
 */
open class TopBottomLineCell(context: Context) : LinearLayout(context) {

  private val titleHeight = 40.uiPX()
  private val title = TextView(context).apply {
    id = ElementID.topBottomLineCellTitle
    typeface = GoldStoneFont.medium(context)
    layoutParams = RelativeLayout.LayoutParams(wrapContent, titleHeight)
    setMargins<RelativeLayout.LayoutParams> { topMargin = 10.uiPX() }
  }
  private val button = TextView(context).apply {
    textSize = fontSize(11)
    textColor = Spectrum.green
    typeface = GoldStoneFont.heavy(context)
    layoutParams = RelativeLayout.LayoutParams(wrapContent, titleHeight)
    gravity = Gravity.END
    setMargins<RelativeLayout.LayoutParams> { topMargin = 10.uiPX() }
  }
  var showTopLine: Boolean = false
  private var titleLayout: RelativeLayout

  init {
    orientation = VERTICAL
    this.setWillNotDraw(false)
    layoutParams = LinearLayout.LayoutParams(matchParent, 40.uiPX())
    titleLayout = relativeLayout {
      lparams(matchParent, titleHeight)
      title.into(this)
    }
  }

  private val paint = Paint().apply {
    isAntiAlias = true
    color = GrayScale.midGray
    style = Paint.Style.FILL
  }
  private var horizontalPaddingSize = 0f

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    showTopLine.isTrue {
      canvas?.drawLine(horizontalPaddingSize, 0f, width - horizontalPaddingSize, 0f, paint)
    }
    canvas?.drawLine(
      horizontalPaddingSize, height.toFloat(), width - horizontalPaddingSize, height.toFloat(),
      paint
    )
  }

  fun setHorizontalPadding(paddingSize: Float) {
    horizontalPaddingSize = paddingSize
    button.leftPadding = paddingSize.toInt()
    title.leftPadding = paddingSize.toInt()
    invalidate()
  }

  fun setTitle(
    text: String,
    textSize: Float = fontSize(12),
    textColor: Int = GrayScale.midGray
  ) {
    title.text = text
    title.textSize = textSize
    title.textColor = textColor
  }

  fun showButton(text: String, left: Int = 0, event: () -> Unit) {
    button.x -= left
    button.setAlignParentRight()
    button
      .apply { this.text = text }
      .click { event() }
      .into(titleLayout)
  }

  fun hideButton() {
    button.visibility = View.GONE
  }

  fun updateButtonTitle(text: String) {
    button.text = text
  }

  fun addCustomizeView(view: View) {
    titleLayout.apply {
      layoutParams = LinearLayout.LayoutParams(
        matchParent,
        wrapContent
      )
      addView(view)
      val layoutParams = RelativeLayout.LayoutParams(view.layoutParams)
      layoutParams.addRule(
        BELOW,
        ElementID.topBottomLineCellTitle
      )
      view.layoutParams = layoutParams
    }
  }
}