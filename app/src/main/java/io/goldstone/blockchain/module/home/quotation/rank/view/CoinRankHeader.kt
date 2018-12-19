package io.goldstone.blockchain.module.home.quotation.rank.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.module.home.quotation.rank.model.CoinGlobalModel
import io.goldstone.blockchain.module.home.quotation.rank.presenter.CoinRankPresenter
import org.jetbrains.anko.*

/**
 * @date: 2018-12-12.
 * @author: yangLiHai
 * @description:
 */
@SuppressLint("SetTextI18n")
class CoinRankHeader(context: Context): LinearLayout(context) {
	private val marketCap = TextView(context).apply {
		layoutParams = LayoutParams(matchParent, wrapContent)
		gravity = Gravity.CENTER
	}
	private val volume24h = TextView(context).apply {
		layoutParams = LayoutParams(matchParent, wrapContent)
		gravity = Gravity.CENTER
	}
	private val btcDominance = TextView(context).apply {
		layoutParams = LayoutParams(matchParent, wrapContent)
		gravity = Gravity.CENTER
	}
	
	var model: CoinGlobalModel? by observing(null) {
		model?.let {
			marketCap.text = "$${CoinRankPresenter.parseVolumeText(it.totalMarketCap)} "
			volume24h.text = "$${CoinRankPresenter.parseVolumeText(it.totalVolume)} "
			btcDominance.text = "${it.btcPercentageMarketCap}%"
		}
	}
	
	init {
		layoutParams = ViewGroup.LayoutParams(matchParent, 100.uiPX())
		
	  verticalLayout {
			gravity = Gravity.CENTER
			layoutParams = LayoutParams(ScreenSize.Width / 3, matchParent)
			textView {
				text = "market cap"
				gravity = Gravity.CENTER
			}.lparams(matchParent, wrapContent)
			addView(marketCap)
		}
		
		verticalLayout {
			gravity = Gravity.CENTER
			layoutParams = LayoutParams(ScreenSize.Width / 3, matchParent)
			textView {
				text = "volume 24h"
				gravity = Gravity.CENTER
			}.lparams(matchParent, wrapContent)
			addView(volume24h)
		}
		
		verticalLayout {
			gravity = Gravity.CENTER
			layoutParams = LayoutParams(ScreenSize.Width / 3, matchParent)
			textView {
				text = "btc dominance"
				gravity = Gravity.CENTER
			}.lparams(matchParent, wrapContent)
			addView(btcDominance)
		}
		
		
	}
	
}