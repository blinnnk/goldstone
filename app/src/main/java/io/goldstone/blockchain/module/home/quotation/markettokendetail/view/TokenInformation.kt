package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.content.Context
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.value.QuotationText
import io.goldstone.blockchain.crypto.formatCurrency
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.TokenInformationModel
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 25/04/2018 9:48 AM
 * @author KaySaith
 */

@Suppress("DEPRECATION")

class TokenInformation(context: Context) : MarketTokenDetailBaseCell(context) {

	var model: TokenInformationModel by observing(TokenInformationModel()) {
		rank.setSubtitle(model.rankValue)
		avalibaleSupply.setSubtitle(model.avaliableSupply)
		// 服务器返回的是带 `,` 的字符串这里加工成 `Double`
		marketCap.setSubtitle(
			model.marketCap.formatCurrency() + " " + GoldStoneApp.currencyCode
		)
	}

	private val rank = MarketTokenDetailBaseInfoCell(context)
	private val avalibaleSupply = MarketTokenDetailBaseInfoCell(context)
	private val marketCap = MarketTokenDetailBaseInfoCell(context)

	init {
		title.text = QuotationText.tokenInforMation
		layoutParams = RelativeLayout.LayoutParams(
			matchParent,
			210.uiPX()
		)
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