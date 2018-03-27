package io.goldstone.blockchain.module.common.tokendetail.tokendetail.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.RoundBorderButton
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent

/**
 * @date 27/03/2018 3:20 PM
 * @author KaySaith
 */

class TokenDetailFooter(context: Context) : LinearLayout(context) {

  private val sendButton = RoundBorderButton(context)
  private val receivedButton = RoundBorderButton(context)

  init {
    isClickable = true
    layoutParams = LinearLayout.LayoutParams(matchParent, 70.uiPX())
    backgroundColor = Spectrum.white

    val buttonWidth = ScreenSize.widthWithPadding / 2 - 5.uiPX()

    sendButton
      .apply {
        text = "Deposit".toUpperCase()
        themeColor = Spectrum.green
        layoutParams = LinearLayout.LayoutParams(buttonWidth, 40.uiPX()).apply {
          leftMargin = PaddingSize.device
          topMargin = 15.uiPX()
        }
        setBorderWidth(BorderSize.bold)
      }
      .into(this)

    receivedButton
      .apply {
        text = "Send".toUpperCase()
        themeColor = Spectrum.blue
        layoutParams = LinearLayout.LayoutParams(buttonWidth, 40.uiPX()).apply {
          topMargin = 15.uiPX()
          leftMargin = 10.uiPX()
        }
        setBorderWidth(BorderSize.bold)
      }
      .into(this)

  }

  private val paint = Paint().apply {
    isAntiAlias = true
    color = GrayScale.Opacity1Black
    style = Paint.Style.FILL
  }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)

    canvas?.drawLine(
      PaddingSize.device.toFloat(),
      BorderSize.default,
      width - PaddingSize.device.toFloat(),
      BorderSize.default,
      paint
    )
  }

}