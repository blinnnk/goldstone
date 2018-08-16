package io.goldstone.blockchain.module.home.quotation.rank.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import com.bumptech.glide.Glide
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.home.quotation.rank.RankModel
import io.goldstone.blockchain.module.home.quotation.rank.model.RankTable
import org.jetbrains.anko.*

/**
 * @date: 2018/8/15.
 * @author: yanglihai
 * @description:
 */
class RankItemCell(context: Context) : LinearLayout(context) {
	private val textViewIndex: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(wrapContent, matchParent)
			textSize = fontSize(15)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
		}
	}
	private val imageViewIcon: ImageView by lazy {
		ImageView(context).apply {
			layoutParams = LayoutParams(30.uiPX(), 30.uiPX())
		}
	}
	private val textViewSymbol: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(wrapContent, matchParent)
			textSize = fontSize(15)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
		}
	}
	private val textViewSymbolDescription: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(wrapContent, matchParent)
			textSize = fontSize(15)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
		}
	}
	private val textViewPrice: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(wrapContent, matchParent)
			textSize = fontSize(15)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
		}
	}
	private val textViewChange: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(wrapContent, matchParent)
			textSize = fontSize(15)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
		}
	}
	private val textViewCapValue: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(wrapContent, matchParent)
			textSize = fontSize(15)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
		}
	}
	
	init {
		orientation = LinearLayout.HORIZONTAL
	  layoutParams = MarginLayoutParams(matchParent, 50.uiPX())
		backgroundColor = Color.WHITE
		
		addView(textViewIndex)
		addView(imageViewIcon)
		addView(textViewSymbol)
		addView(textViewSymbolDescription)
		addView(textViewPrice)
		addView(textViewChange)
		addView(textViewCapValue)
	}
	
	var rankModel: RankTable by observing(RankTable()) {
		textViewIndex.text = index.toString()
		Glide.with(imageViewIcon).load(rankModel.icon)
		textViewSymbol.text = rankModel.symbol
		textViewSymbolDescription.text = rankModel.name
		textViewPrice.text = rankModel.price
		textViewChange.text = rankModel.changePercent24h
		textViewCapValue.text = rankModel.marketCap
	}
	
	var index: Int = 0
	
}