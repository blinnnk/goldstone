package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.imageResource

/**
 * @date 23/03/2018 11:11 PM
 * @author KaySaith
 */

class SquareIcon(context: Context) : LinearLayout(context) {

  var src: Int by observing(0) {
    image.imageResource = src
  }

  private val image by lazy { ImageView(context) }
  private val iconSize = 28.uiPX()

  init {

    setWillNotDraw(false)

    layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)

    image
      .apply {
        layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
        scaleType = ImageView.ScaleType.CENTER_INSIDE
        setColorFilter(Spectrum.white)
      }
      .into(this)
  }

  private val paint = Paint().apply {
    isAntiAlias = true
    style = Paint.Style.FILL
    color = Spectrum.opacity2White
  }

  @SuppressLint("DrawAllocation")
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)

    val rectF = RectF(0f, 0f, iconSize.toFloat(), iconSize.toFloat())
    canvas?.drawRoundRect(rectF, CornerSize.default, CornerSize.default, paint)

  }

  fun setGrayStyle() {
    paint.color = GrayScale.Opacity2Black
    invalidate()
  }

}