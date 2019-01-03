package io.goldstone.blockchain.module.home.quotation.quotationrank.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.Language.CoinRankText
import io.goldstone.blockchain.common.component.title.twoLineTitles
import io.goldstone.blockchain.module.home.quotation.quotationrank.model.QuotationGlobalModel
import org.jetbrains.anko.matchParent


/**
 * @author KaySaith
 * @date  2019/01/02
 */
@SuppressLint("SetTextI18n")
class QuotationRankHeaderView(context: Context) : LinearLayout(context) {
	
	var model: QuotationGlobalModel? by observing(null) {
		model?.let {
			headerLabelNames.forEach { name ->
				twoLineTitles {
					layoutParams = LayoutParams(ScreenSize.Width / 3, matchParent)
					isCenter = true
					setBlackTitles()
					title.text = name
					subtitle.text = when (name) {
						CoinRankText.marketCap -> it.totalMarketCap
						CoinRankText.volume24h -> it.totalVolume
						else -> it.btcPercentageMarketCap
					}
				}
			}
		}
	}
	private val headerLabelNames =
		listOf(
			CoinRankText.marketCap,
			CoinRankText.volume24h,
			CoinRankText.btcDominance
		)
	init {
		layoutParams = ViewGroup.LayoutParams(matchParent, 100.uiPX())
	}
}