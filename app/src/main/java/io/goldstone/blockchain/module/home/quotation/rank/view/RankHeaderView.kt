package io.goldstone.blockchain.module.home.quotation.rank.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.home.quotation.rank.model.RankHeaderModel
import org.jetbrains.anko.*

/**
 * @date: 2018/8/15.
 * @author: yanglihai
 * @description:
 */

class RankHeaderView(context: Context) : LinearLayout(context) {
	
	private var rankHeaderModel: RankHeaderModel? = null
	
	private val totalMarketCap by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(ScreenSize.Width/3, matchParent)
			textSize = fontSize(15)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
		}
	}
	private val totalvalue by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(ScreenSize.Width/3, matchParent)
			textSize = fontSize(15)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
		}
	}
	private val totalBitcoinPercentageOfMarketCap by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(ScreenSize.Width/3, matchParent)
			textSize = fontSize(15)
			textColor = Spectrum.lightRed
			gravity = Gravity.CENTER
		}
	}
	
	init {
		orientation = LinearLayout.HORIZONTAL
		layoutParams = LinearLayout.LayoutParams(matchParent, 100.uiPX())
		backgroundColor = Color.WHITE
		
		addView(totalMarketCap)
		addView(totalvalue)
		addView(totalBitcoinPercentageOfMarketCap)
	}
	
	fun updateHeaderData(rankHeaderModel: RankHeaderModel) {
		this.rankHeaderModel = rankHeaderModel
		totalMarketCap.text = rankHeaderModel.total_market_cap
		totalvalue.text = rankHeaderModel.total_volume_24h
		totalBitcoinPercentageOfMarketCap.text = rankHeaderModel.bitcoin_percentage_of_market_cap
	}
	
}