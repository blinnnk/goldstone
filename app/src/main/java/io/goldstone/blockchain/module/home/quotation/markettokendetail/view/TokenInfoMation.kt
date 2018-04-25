package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.content.Context
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 25/04/2018 9:48 AM
 * @author KaySaith
 */


data class TokenInfomationModel(
  val rankValue: String = "",
  val avaliableSupply: String = "",
  val marketCap: String = ""
)
class TokenInfomation(context: Context) : MarketTokenDetailBaseCell(context) {

  var model: TokenInfomationModel by observing(TokenInfomationModel()) {
    rank.setSubtitle(model.rankValue)
    avalibaleSupply.setSubtitle(model.avaliableSupply)
    marketCap.setSubtitle(model.marketCap)
  }

  private val rank = MarketTokenDetailBaseInfoCell(context)
  private val avalibaleSupply = MarketTokenDetailBaseInfoCell(context)
  private val marketCap = MarketTokenDetailBaseInfoCell(context)

  init {
    title.text = "Token Infomation"
    layoutParams = RelativeLayout.LayoutParams(matchParent, 210.uiPX())
    verticalLayout {
      rank.into(this)
      avalibaleSupply.into(this)
      marketCap.into(this)

      rank.setTitle("Rank")
      avalibaleSupply.setTitle("Avaliable Supply")
      marketCap.setTitle("Market Cap")
      y -= 10.uiPX()
    }.setAlignParentBottom()

  }

}