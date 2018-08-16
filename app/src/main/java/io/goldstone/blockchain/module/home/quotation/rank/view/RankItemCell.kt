package io.goldstone.blockchain.module.home.quotation.rank.view

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.Gravity
import android.widget.*
import com.blinnnk.extension.*
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.quotation.rank.model.RankTable
import org.jetbrains.anko.*
import java.lang.Exception

/**
 * @date: 2018/8/15.
 * @author: yanglihai
 * @description:
 */
class RankItemCell(context: Context) : LinearLayout(context) {
	private val textViewIndex: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(ScreenSize.Width/10, matchParent)
			typeface = GoldStoneFont.heavy(context)
			textSize = fontSize(15)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
		}
	}
	private val imageViewIcon: ImageView by lazy {
		ImageView(context).apply {
			layoutParams = LayoutParams(ScreenSize.Width/10,ScreenSize.Width/10)
			setVerticalGravity(Gravity.CENTER_VERTICAL)
		}
	}
	private val textViewSymbol: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(wrapContent, wrapContent)
			typeface = GoldStoneFont.heavy(context)
			textSize = fontSize(15)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
		}
	}
	private val textViewSymbolDescription: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(wrapContent, wrapContent)
			textSize = fontSize(10)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
		}
	}
	
	private val linearLayoutSymbol: LinearLayout by lazy {
		LinearLayout(context).apply {
			orientation = LinearLayout.VERTICAL
			layoutParams = MarginLayoutParams(ScreenSize.Width/5, matchParent)
			gravity = Gravity.CENTER
			addView(textViewSymbol)
			addView(textViewSymbolDescription)
		}
	}
	private val textViewPrice: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(ScreenSize.Width/5, matchParent)
			textSize = fontSize(15)
			textColor = Color.BLACK
			gravity = Gravity.CENTER
		}
	}
	private val textViewChange: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(ScreenSize.Width/5, matchParent)
			textSize = fontSize(15)
			textColor = Spectrum.lightGreen
			gravity = Gravity.CENTER
		}
	}
	private val textViewCapValue: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(ScreenSize.Width/5, matchParent)
			textSize = fontSize(15)
			textColor = Color.BLACK
			gravity = Gravity.CENTER
		}
	}
	
	init {
		orientation = LinearLayout.HORIZONTAL
	  layoutParams = MarginLayoutParams(matchParent, 50.uiPX())
		setMargins<MarginLayoutParams> (){
			margin = 5.uiPX()
		}
		setPadding(0, 0, 5.uiPX(), 0)
		addCorner(CornerSize.default.toInt(), Spectrum.white)
		
		addView(textViewIndex)
		addView(imageViewIcon)
		addView(linearLayoutSymbol)
		addView(textViewPrice)
		addView(textViewChange)
		addView(textViewCapValue)
	}
	
	var rankModel: RankTable by observing(RankTable()) {
		textViewIndex.text = rankModel.rank
		
		try {
			Glide.with(context)
				.load(Uri.parse(rankModel.icon))
				.apply(RequestOptions().placeholder(R.mipmap.ic_launcher))
				.into(imageViewIcon)
		}catch (e: Exception){
			LogUtil.error(RankItemCell::class.java.simpleName, e)
		}
		
		
		
		textViewSymbol.text = rankModel.symbol
		textViewSymbolDescription.text = rankModel.name
		textViewPrice.text = rankModel.price
		textViewChange.text = rankModel.changePercent24h
		textViewCapValue.text = rankModel.marketCap
	}
	
}