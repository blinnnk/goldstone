package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.view

import android.content.Context
import android.widget.RelativeLayout
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.AlarmCell
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable
import org.jetbrains.anko.matchParent

/**
 * @date 08/08/2018 1:49 PM
 * @author wcx
 */
class PriceAlarmClockListCell(context: Context) : AlarmCell(context) {
  var model: PriceAlarmClockTable by observing(PriceAlarmClockTable()) {
    setTimeTitle(model.createTime.toString())
    setAlarmInfoSubtitle("${model.marketName} ${model.pairDisplay}")
    if (model.priceType == 0) {
      setAlarmInfoTitle("1 ${model.symbol} > ${model.price} ${model.currencyName}")
    } else {
      setAlarmInfoTitle("1 ${model.symbol} < ${model.price} ${model.currencyName}")
    }
  }

  init {
    initializeView()
  }

  private fun initializeView() {
    layoutParams = RelativeLayout.LayoutParams(matchParent, 112.uiPX()).apply {
      leftMargin = 10.uiPX()
      topMargin = 5.uiPX()
      rightMargin = 10.uiPX()
    }
    showSwitchBVutton()
  }
}