package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.view

import android.content.Context
import android.graphics.Color
import android.widget.RelativeLayout
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basecell.BaseSwitchCell
import io.goldstone.blockchain.common.value.ElementID.createTimeTextView
import io.goldstone.blockchain.common.value.ElementID.priceTextView
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable
import org.jetbrains.anko.backgroundColor

/**
 * @date 08/08/2018 1:49 PM
 * @author wcx
 */
class PriceAlarmClockListCell(context: Context) : BaseSwitchCell(context) {
  var model: PriceAlarmClockTable by observing(PriceAlarmClockTable()) {
    createTime.setText(model.createTime)
    market.setText("${model.marketName} pro ${model.pairDisplay}")
    if (model.priceType == 0) {
      price.setText("1 BTC > ${model.price} ${model.currencyName}")
    } else {
      price.setText("1 BTC < ${model.price} ${model.currencyName}")
    }
  }

  fun setSwitchImage(
    width: Int,
    height: Int) {
    val layoutParams = switchImageView.layoutParams
    layoutParams.width = width.uiPX()
    layoutParams.height = height.uiPX()
    switchImageView.layoutParams = layoutParams
  }

  fun initializeView() {
    backgroundColor = Color.GRAY
    val priceAlarmClockLayoutParams = RelativeLayout.LayoutParams(layoutParams)
    priceAlarmClockLayoutParams.setMargins(
      10.uiPX(),
      10.uiPX(),
      10.uiPX(),
      10.uiPX()
    )
    layoutParams = priceAlarmClockLayoutParams

    val createTimeLayoutParams = android.widget.RelativeLayout.LayoutParams(createTime.layoutParams)
    createTimeLayoutParams.setMargins(
      20.uiPX(),
      20.uiPX(),
      20.uiPX(),
      20.uiPX()
    )
    createTime.layoutParams = createTimeLayoutParams

    val priceLayoutParams = android.widget.RelativeLayout.LayoutParams(price.layoutParams)
    priceLayoutParams.setMargins(
      20.uiPX(),
      0.uiPX(),
      20.uiPX(),
      10.uiPX()
    )
    priceLayoutParams.addRule(
      BELOW,
      createTimeTextView)
    price.layoutParams = priceLayoutParams

    val marketLayoutParams = android.widget.RelativeLayout.LayoutParams(market.layoutParams)
    marketLayoutParams.setMargins(
      20.uiPX(),
      0,
      20.uiPX(),
      20.uiPX()
    )
    marketLayoutParams.addRule(
      BELOW,
      priceTextView
    )
    market.layoutParams = marketLayoutParams
  }
}