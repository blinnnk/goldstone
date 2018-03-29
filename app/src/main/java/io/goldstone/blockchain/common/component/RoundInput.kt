package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import com.blinnnk.honey.setCursorColor
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.BorderSize
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.singleLine
import org.jetbrains.anko.textColor

/**
 * @date 22/03/2018 3:11 PM
 * @author KaySaith
 */

class RoundInput(context: Context) : EditText(context) {

  var text by observing("") {
    invalidate()
  }

  private val paint = Paint()
  private val textPaint = Paint()
  private val backgroundPaint = Paint()

  private val titleSize = 16.uiPX().toFloat()

  init {

    paint.isAntiAlias = true
    paint.style = Paint.Style.STROKE
    paint.color = GrayScale.lightGray
    paint.strokeWidth = BorderSize.bold

    backgroundPaint.isAntiAlias = true
    backgroundPaint.style = Paint.Style.FILL
    backgroundPaint.color = Spectrum.white

    textPaint.isAntiAlias = true
    textPaint.style = Paint.Style.FILL
    textPaint.color = GrayScale.midGray
    textPaint.typeface = GoldStoneFont.heavy(context)
    textPaint.textSize = titleSize
    singleLine = true

    hintTextColor = GrayScale.lightGray

    setWillNotDraw(false)

    layoutParams = LinearLayout.LayoutParams(
      ScreenSize.Width - PaddingSize.device * 2,
      65.uiPX()
    ).apply {
      leftMargin = PaddingSize.device
    }
    leftPadding = 35.uiPX()
    backgroundTintMode = PorterDuff.Mode.CLEAR
    textColor = GrayScale.black
    typeface = GoldStoneFont.heavy(context)
    setCursorColor(Spectrum.blue)
  }

  private val paddingSize = 5.uiPX()

  @SuppressLint("DrawAllocation")
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)

    val rectF = RectF(
      BorderSize.bold + paddingSize,
      BorderSize.bold + paddingSize,
      width - BorderSize.bold * 2 - paddingSize,
      height - BorderSize.bold * 2 - paddingSize
    )

    canvas?.drawRoundRect(rectF, height / 2f, height / 2f, paint)

    val textBackground = RectF(
      25.uiPX().toFloat(),
      0f,
      textPaint.measureText(text) + 50.uiPX(),
      titleSize
    )
    canvas?.drawRect(textBackground, backgroundPaint)

    canvas?.drawText(text, 35.uiPX().toFloat(), 12.uiPX().toFloat(), textPaint)
  }

  fun setNumberInput() {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
  }

  fun setPasswordInput(show: Boolean = false) {
    inputType =
      if (show == false) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
      else InputType.TYPE_CLASS_TEXT
  }

}