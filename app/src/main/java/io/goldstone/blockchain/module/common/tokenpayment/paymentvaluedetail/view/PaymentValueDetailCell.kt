package io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view

import android.content.Context
import android.widget.TextView
import com.blinnnk.component.HoneyRadioButton
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.model.PaymentValueDetailModel
import org.jetbrains.anko.textColor

/**
 * @date 28/03/2018 12:24 PM
 * @author KaySaith
 */

class PaymentValueDetailCell(context: Context) : BaseCell(context) {

  var model: PaymentValueDetailModel by observing(PaymentValueDetailModel()) {
    info.title.text = model.count
    info.subtitle.text = model.info
    feeTypeDescription.text = model.type
    radioButton.isChecked = model.isSelected
  }

  private val info by lazy { TwoLineTitles(context) }
  private val radioButton by lazy { HoneyRadioButton(context) }
  private val feeTypeDescription by lazy { TextView(context) }

  init {

    info
      .apply {
        setBlackTitles()
        setSmallStyle()
      }
      .into(this)

    feeTypeDescription
      .apply {
        textSize = 4.uiPX().toFloat()
        textColor = GrayScale.midGray
        typeface = GoldStoneFont.book(context)
      }
      .into(this)

    radioButton
      .apply {
        setColorStyle(GrayScale.lightGray, Spectrum.green)
      }
      .into(this)

    radioButton.apply {
      setAlignParentRight()
      setCenterInVertical()
    }

    feeTypeDescription.apply {
      setAlignParentRight()
      setCenterInVertical()
      x -= 35.uiPX()
    }

    info.apply {
      setCenterInVertical()
    }

    setGrayStyle()

    hasArrow = false

  }

}