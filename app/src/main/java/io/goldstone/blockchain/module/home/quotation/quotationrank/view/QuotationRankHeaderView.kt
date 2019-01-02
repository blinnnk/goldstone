package io.goldstone.blockchain.module.home.quotation.quotationrank.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.Language.CoinRankText
import io.goldstone.blockchain.module.home.quotation.quotationrank.model.QuotationGlobalModel
import io.goldstone.blockchain.module.home.quotation.quotationrank.presenter.QuotationRankPresenter
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2019/01/02
 */
@SuppressLint("SetTextI18n")
class QuotationRankHeaderView(context: Context) : LinearLayout(context) {
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
	
	var model: QuotationGlobalModel? by observing(null) {
		model?.let {
			marketCap.text = "$${QuotationRankPresenter.parseVolumeText(it.totalMarketCap)} "
			volume24h.text = "$${QuotationRankPresenter.parseVolumeText(it.totalVolume)} "
			btcDominance.text = "${it.btcPercentageMarketCap}%"
		}
	}
	
	init {
		layoutParams = ViewGroup.LayoutParams(matchParent, 100.uiPX())
		
		verticalLayout {
			gravity = Gravity.CENTER
			layoutParams = LayoutParams(ScreenSize.Width / 3, matchParent)
			textView {
				text = CoinRankText.marketCap
				gravity = Gravity.CENTER
			}.lparams(matchParent, wrapContent)
			addView(marketCap)
		}
		
		verticalLayout {
			gravity = Gravity.CENTER
			layoutParams = LayoutParams(ScreenSize.Width / 3, matchParent)
			textView {
				text = CoinRankText.volume24h
				gravity = Gravity.CENTER
			}.lparams(matchParent, wrapContent)
			addView(volume24h)
		}
		
		verticalLayout {
			gravity = Gravity.CENTER
			layoutParams = LayoutParams(ScreenSize.Width / 3, matchParent)
			textView {
				text = CoinRankText.btcDominance
				gravity = Gravity.CENTER
			}.lparams(matchParent, wrapContent)
			addView(btcDominance)
		}
		
		
	}
}