package io.goldstone.blockchain.module.home.quotation.quotation.view

import android.content.Context
import android.view.View
import android.widget.TextView
import com.blinnnk.animation.updateOriginYAnimation
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.CircleButton
import io.goldstone.blockchain.common.component.SliderHeader
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.QuotationText
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.textColor

/**
 * @date 26/03/2018 9:07 PM
 * @author KaySaith
 */

class QuotationSlideHeader(context: Context) : SliderHeader(context) {

  val addTokenButton by lazy { CircleButton(context) }
  val setAlertButton by lazy { CircleButton(context) }
  private val title = TextView(context)

  init {

    addTokenButton
      .apply {
        title = "token"
        src = R.drawable.add_token_icon
        x += PaddingSize.device
        y += 15.uiPX()
      }
      .into(this)

    addTokenButton.apply {
      setCenterInVertical()
    }

    setAlertButton
      .apply {
        title = "alarm"
        src = R.drawable.alarm_icon
        x -= PaddingSize.device
        y += 15.uiPX()
      }
      .into(this)

    setAlertButton.apply {
      setCenterInVertical()
      setAlignParentRight()
    }

    title
      .apply {
        text = QuotationText.market
        textColor = Spectrum.white
        textSize = 5.uiPX().toFloat()
        typeface = GoldStoneFont.heavy(context)
      }
      .into(this)

    title.setCenterInParent()

  }

  override fun onHeaderShowedStyle() {
    super.onHeaderShowedStyle()
    addTokenButton.setUnTransparent()
    setAlertButton.setUnTransparent()
    title.updateOriginYAnimation(23.uiPX().toFloat())
  }

  override fun onHeaderHidesStyle() {
    super.onHeaderHidesStyle()
    addTokenButton.setDefaultStyle()
    setAlertButton.setDefaultStyle()
    title.updateOriginYAnimation(34.uiPX().toFloat())
  }

}