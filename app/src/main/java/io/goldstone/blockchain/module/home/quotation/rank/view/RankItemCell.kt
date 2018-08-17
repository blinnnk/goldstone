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
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.quotation.rank.model.RankTable
import org.jetbrains.anko.*

/**
 * @date: 2018/8/15.
 * @author: yanglihai
 * @description: rank的item
 */
class RankItemCell(context: Context) : LinearLayout(context) {
	
	private val cellWidth = ScreenSize.Width - 15.uiPX()
	
	private val itemWidth = cellWidth / 10
	
	private val textViewIndex: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(itemWidth, matchParent)
			typeface = GoldStoneFont.heavy(context)
			textSize = fontSize(17)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
		}
	}
	private val imageViewIcon: ImageView by lazy {
		ImageView(context).apply {
			layoutParams = LayoutParams(itemWidth, cellWidth / 10)
			(layoutParams as LinearLayout.LayoutParams).gravity = Gravity.CENTER_VERTICAL
			setPadding(itemWidth / 10, itemWidth / 10, itemWidth / 10, itemWidth / 10)
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
			layoutParams = MarginLayoutParams(itemWidth * 3 / 2, matchParent)
			gravity = Gravity.CENTER
			addView(textViewSymbol)
			addView(textViewSymbolDescription)
		}
	}
	private val textViewPrice: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(itemWidth * 2, matchParent)
			textSize = fontSize(15)
			textColor = Color.BLACK
			gravity = Gravity.CENTER
		}
	}
	private val textViewChange: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(itemWidth * 3 / 2, matchParent)
			textSize = fontSize(15)
			textColor = Spectrum.lightGreen
			gravity = Gravity.CENTER
		}
	}
	private val textViewCapValue: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(itemWidth * 3, matchParent)
			textSize = fontSize(15)
			textColor = Color.BLACK
			gravity = Gravity.CENTER
		}
	}
	
	init {
		
		layoutParams = MarginLayoutParams(matchParent, 60.uiPX())
		backgroundColor = Spectrum.darkBlue
		orientation = LinearLayout.VERTICAL
		
		linearLayout {
			orientation = LinearLayout.HORIZONTAL
			layoutParams = LayoutParams(matchParent, matchParent)
			setMargins<LayoutParams> {
				leftMargin = 5.uiPX()
				rightMargin = 5.uiPX()
				topMargin = 10.uiPX()
			}
			addCorner(CornerSize.default.toInt(), Spectrum.white)
			setPadding(0, 0, 5.uiPX(), 0)
			
			addView(textViewIndex)
			addView(imageViewIcon)
			addView(linearLayoutSymbol)
			addView(textViewPrice)
			addView(textViewChange)
			addView(textViewCapValue)
		}
		
	}
	
	var rankModel: RankTable by observing(RankTable()) {
		textViewIndex.text = rankModel.rank
		rankModel.icon.isNull() isFalse {
			Glide.with(context)
				.load(Uri.parse(rankModel.icon))
				.apply(RequestOptions().placeholder(R.mipmap.ic_launcher))
				.into(imageViewIcon)
		}
		textViewSymbol.text = rankModel.symbol
		textViewSymbolDescription.text = rankModel.name
		textViewPrice.text = rankModel.price
		textViewChange.text = rankModel.changePercent24h
		if (!rankModel.color.isNull() && rankModel.color.isNotEmpty()) {
			textViewChange.textColor = Color.parseColor(rankModel.color)
		} else {
			textViewChange.textColor = if (rankModel.changePercent24h.contains("-")) Spectrum.lightRed else Spectrum.lightGreen
			
		}
		textViewCapValue.text = rankModel.marketCap
	}
	
}