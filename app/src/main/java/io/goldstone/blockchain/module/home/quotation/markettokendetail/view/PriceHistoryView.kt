package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.content.Context
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.uikit.uiPX
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 25/04/2018 9:04 AM
 * @author KaySaith
 */

class PriceHistoryView(context: Context) : MarketTokenDetailBaseCell(context) {

  private val dayPrice = MarketTokenDetailBaseInfoCell(context)
  private val totalPrice = MarketTokenDetailBaseInfoCell(context)

  init {
    title.text = "Price History"
    layoutParams = RelativeLayout.LayoutParams(matchParent, 160.uiPX())

    verticalLayout {
      dayPrice.into(this)
      totalPrice.into(this)

      dayPrice.setPriceTitle("24 Hours")
      totalPrice.setPriceTitle("Total")

      dayPrice.setPricesubtitle("13.56 / 12.78", "USDT")
      totalPrice.setPricesubtitle("17.56 / 3.65", "USDT")
      y -= 10.uiPX()
    }.setAlignParentBottom()
  }

}