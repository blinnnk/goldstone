package io.goldstone.blockchain.module.home.quotation.rank.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.quotation.rank.model.RankHeaderModel
import org.jetbrains.anko.*

/**
 * @date: 2018/8/15.
 * @author: yanglihai
 * @description: rank的头部view
 */

class RankHeaderView(context: Context) : LinearLayout(context) {
	
	private var rankHeaderModel: RankHeaderModel? = null
	
	private val textViewMarketCap = TextView(context).apply {
		layoutParams = LayoutParams(matchParent, wrapContent)
		textSize = fontSize(15)
		textColor = Spectrum.darkBlue
		gravity = Gravity.CENTER
		typeface = GoldStoneFont.heavy(context)
		text = "market Cap"
	}
	
	private val totalMarketCap by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(matchParent, wrapContent)
			textSize = fontSize(15)
			textColor = GrayScale.black
			gravity = Gravity.CENTER
		}
	}
	
	private val linearLayoutMarketCap by lazy {
		LinearLayout(context).apply {
			layoutParams = LayoutParams(ScreenSize.Width / 3, matchParent)
			gravity = Gravity.CENTER
			orientation = LinearLayout.VERTICAL
			addView(textViewMarketCap)
			addView(totalMarketCap)
		}
	}
	
	private val textviewTotalVolume by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(matchParent, wrapContent)
			textSize = fontSize(15)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
			typeface = GoldStoneFont.heavy(context)
			text = "24h Volume"
		}
	}
	private val totalVolume by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(matchParent, wrapContent)
			textSize = fontSize(15)
			textColor = GrayScale.black
			gravity = Gravity.CENTER
		}
	}
	
	private val linearLayoutVolume by lazy {
		LinearLayout(context).apply {
			layoutParams = LayoutParams(ScreenSize.Width / 3, matchParent)
			gravity = Gravity.CENTER
			orientation = LinearLayout.VERTICAL
			addView(textviewTotalVolume)
			addView(totalVolume)
		}
	}
	
	private val textviewBTCDominance by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(matchParent, wrapContent)
			textSize = fontSize(15)
			textColor = Spectrum.darkBlue
			typeface = GoldStoneFont.heavy(context)
			text = "BTC Dominance"
			gravity = Gravity.CENTER
		}
	}
	
	private val totalBTCDominance by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(matchParent, wrapContent)
			textSize = fontSize(15)
			textColor = GrayScale.black
			gravity = Gravity.CENTER
		}
	}
	
	private val linearLayoutBTCDominance by lazy {
		LinearLayout(context).apply {
			layoutParams = LayoutParams(ScreenSize.Width / 3, matchParent)
			gravity = Gravity.CENTER
			orientation = LinearLayout.VERTICAL
			addView(textviewBTCDominance)
			addView(totalBTCDominance)
		}
	}
	
	init {
		orientation = LinearLayout.HORIZONTAL
		layoutParams = LinearLayout.LayoutParams(matchParent, 100.uiPX())
		backgroundColor = Spectrum.white
		
		addView(linearLayoutMarketCap)
		addView(linearLayoutVolume)
		addView(linearLayoutBTCDominance)
	}
	
	fun updateHeaderData(rankHeaderModel: RankHeaderModel) {
		this.rankHeaderModel = rankHeaderModel
		totalMarketCap.text = rankHeaderModel.totalMarketCap
		totalVolume.text = rankHeaderModel.totalVolume24h
		totalBTCDominance.text = rankHeaderModel.BtcPercentage+"%"
	}
	
}