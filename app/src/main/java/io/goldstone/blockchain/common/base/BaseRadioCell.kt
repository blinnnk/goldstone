package io.goldstone.blockchain.common.base

import android.content.Context
import android.widget.TextView
import com.blinnnk.component.HoneyRadioButton
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.profile.currency.model.CurrencyModel
import org.jetbrains.anko.textColor

/**
 * @date 26/03/2018 6:49 PM
 * @author KaySaith
 */

open class BaseRadioCell(context: Context) : BaseCell(context) {

  protected var checkedStatus = false
  protected val title = TextView(context)

  private val radioButton = HoneyRadioButton(context)

  init {

    hasArrow = false
    setGrayStyle()

    this.addView(title
      .apply {
        textSize = 5.uiPX().toFloat()
        textColor = GrayScale.black
        typeface = GoldStoneFont.medium(context)
      })

    title.setCenterInVertical()

    this.addView(radioButton
      .apply {
        isChecked = checkedStatus
        setColorStyle(GrayScale.midGray, Spectrum.green)
      })

    radioButton.apply {
      setAlignParentRight()
      setCenterInVertical()
    }

    layoutParams.height = 50.uiPX()

  }

  fun setSwitchStatus() {
    radioButton.isChecked = !radioButton.isChecked
  }

}