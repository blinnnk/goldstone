package io.goldstone.blockchain.module.home.profile.currency.view

import android.content.Context
import android.content.res.ColorStateList
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.profile.currency.model.CurrencyModel
import org.jetbrains.anko.radioButton
import org.jetbrains.anko.textColor

/**
 * @date 26/03/2018 2:26 PM
 * @author KaySaith
 */

class CurrencyCell(context: Context) : BaseCell(context) {

  var model: CurrencyModel by observing(CurrencyModel()) {
    symbol.text = model.symbol
  }

  private val symbol = TextView(context)

  init {

    hasArrow = false
    setGrayStyle()

    symbol
      .apply {
        textSize = 5.uiPX().toFloat()
        textColor = GrayScale.black
        typeface = GoldStoneFont.medium(context)
      }
      .into(this)

    symbol.setCenterInVertical()

    radioButton {
      buttonTintList = ColorStateList(
        arrayOf(
          intArrayOf(-android.R.attr.state_checked), //disabled
          intArrayOf(android.R.attr.state_checked) //enabled
        ),
        intArrayOf(
          GrayScale.midGray, // disabled
          Spectrum.blue //enabled
        )
      )
    }.apply {
      setAlignParentRight()
      setCenterInVertical()
    }

    layoutParams.height = 50.uiPX()

  }

}