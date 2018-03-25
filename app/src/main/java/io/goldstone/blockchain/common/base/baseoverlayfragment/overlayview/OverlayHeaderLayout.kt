package io.goldstone.blockchain.common.base.baseoverlayfragment.overlayview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.addTopLRCorner
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.*

/**
 * @date 22/03/2018 2:37 AM
 * @author KaySaith
 */

class OverlayHeaderLayout(context: Context) : RelativeLayout(context) {

  var title: TextView

  val closeButton by lazy {
    ImageView(context).apply {
      id = ElementID.closeButton
      imageResource = R.drawable.close
      setColorFilter(GrayScale.lightGray)
      layoutParams = RelativeLayout.LayoutParams(iconSize, iconSize).apply {
        topMargin = 18.uiPX()
        rightMargin = 20.uiPX()
        alignParentRight()
      }
      addTouchRippleAnimation(Color.TRANSPARENT, Spectrum.blue, RippleMode.Round)
    }
  }

  val backButton by lazy {
    ImageView(context).apply {
      id = ElementID.backButton
      imageResource = R.drawable.back
      setColorFilter(GrayScale.lightGray)
      scaleType = ImageView.ScaleType.CENTER_INSIDE
      layoutParams = RelativeLayout.LayoutParams(iconSize + 5.uiPX(), iconSize + 5.uiPX()).apply {
        topMargin = 18.uiPX()
        leftMargin = 15.uiPX()
        alignParentLeft()
      }
      addTouchRippleAnimation(Color.TRANSPARENT, Spectrum.blue, RippleMode.Round)
    }
  }

  private val headerHeight = 65.uiPX()
  private val iconSize = 30.uiPX()

  private val paint = Paint()

  init {

    setWillNotDraw(false)

    layoutParams = RelativeLayout.LayoutParams(ScreenSize.Width, headerHeight)

    title = textView {
      textColor = GrayScale.black
      textSize = FontSize.header
      typeface = GoldStoneFont.heavy(context)
      gravity = Gravity.CENTER
      layoutParams = RelativeLayout.LayoutParams(matchParent, headerHeight)
    }

    paint.color = GrayScale.lightGray
    paint.isAntiAlias = true
    paint.style = Paint.Style.FILL

  }

  fun showCloseButton(isShow: Boolean) {
    if (isShow) addView(closeButton)
    else findViewById<ImageView>(ElementID.closeButton)?.let {
      removeView(it)
    }
  }

  fun showBackButton(isShow: Boolean) {
    if (isShow) addView(backButton)
    else findViewById<ImageView>(ElementID.backButton)?.let {
      removeView(it)
    }
  }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)

    canvas?.drawLine(
      PaddingSize.device.toFloat(),
      height - BorderSize.default,
      (ScreenSize.Width - PaddingSize.device).toFloat(),
      height - BorderSize.default, paint
    )

    canvas?.save()
  }

}