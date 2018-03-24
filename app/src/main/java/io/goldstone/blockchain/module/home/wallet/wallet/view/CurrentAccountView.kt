package io.goldstone.blockchain.module.home.wallet.wallet.view

import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.TwoLineTitles
import com.blinnnk.extension.into
import io.goldstone.blockchain.common.value.ShadowSize
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.imageResource

/**
 * @date 24/03/2018 12:21 AM
 * @author KaySaith
 */

class CurrentAccountView(context: Context) : RelativeLayout(context) {

  private val avatar by lazy { ImageView(context) }
  private val info by lazy { TwoLineTitles(context) }
  private val qrIcon by lazy { ImageView(context) }

  init {
    layoutParams = RelativeLayout.LayoutParams(160.uiPX(), 40.uiPX())
    addTouchRippleAnimation(Spectrum.white, Spectrum.blue, RippleMode.Square, 20.uiPX().toFloat())
    elevation = ShadowSize.Overlay

    avatar
      .apply {
        layoutParams = RelativeLayout.LayoutParams(34.uiPX(), 34.uiPX()).apply {
          leftMargin = 3.uiPX()
          topMargin = 3.uiPX()
          scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
        addCorner(17.uiPX(), Spectrum.blue)
      }
      .into(this)

    info
      .apply {
        setBlackTitles()
        setSmallStyle()
        title.text = "KaySaith"
        subtitle.text = "0x8d9...6s8d8"
        x += 45.uiPX()
      }
      .into(this)

    info.setCenterInVertical()

    qrIcon
      .apply {
        scaleType = ImageView.ScaleType.CENTER_INSIDE
        imageResource = R.drawable.qrcode_icon
        layoutParams = RelativeLayout.LayoutParams(20.uiPX(), 20.uiPX())
        x -= 15.uiPX()
      }
      .into(this)

    qrIcon.apply {
      setAlignParentRight()
      setCenterInVertical()
    }

  }

}