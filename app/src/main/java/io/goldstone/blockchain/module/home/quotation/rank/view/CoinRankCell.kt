package io.goldstone.blockchain.module.home.quotation.rank.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import com.blinnnk.extension.*
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.quotation.rank.model.CoinRankModel
import org.jetbrains.anko.*

/**
 * @date: 2018-12-12.
 * @author: yangLiHai
 * @description:
 */
class CoinRankCell(context: Context): BaseCell(context) {
	
	private val cellHeight = 50.uiPX()
	private val cellWidth = ScreenSize.Width - 20.uiPX()
	private val rankWidth = cellWidth / 10
	private val iconWidth = cellWidth / 6
	private val symbolWidth = cellWidth / 6
	private val priceWidth = cellWidth / 6
	private val changeWidth = cellWidth / 8
	
	private val rank = TextView(context).apply {
		layoutParams = LayoutParams(rankWidth, matchParent)
		gravity = Gravity.CENTER
	}
	private val icon = ImageView(context).apply {
		layoutParams = LayoutParams(Math.min(cellHeight, iconWidth), matchParent)
		gravity = Gravity.CENTER
		addCorner(layoutParams.width / 2, Spectrum.white)
	}
	private val name = TextView(context)
	private val symbol = TextView(context)
	private val price = TextView(context).apply {
		layoutParams = LayoutParams(priceWidth, matchParent)
		gravity = Gravity.CENTER
	}
	private val changePercent = TextView(context).apply {
		layoutParams = LayoutParams(changeWidth, matchParent)
		gravity = Gravity.CENTER
	}
	private val marketCap = TextView(context)
	private val volume = TextView(context)
	
	var model: CoinRankModel? by observing(null) {
		model?.let { it ->
			rank.text = "${it.rank}"
			if (it.icon.isNotEmpty()) {
				icon.glideImage("${it.icon}")
				if (it.color.isNotEmpty()) icon.setColorFilter(Color.parseColor(it.color))
			} else {
				icon.glideImage(null)
			}
			
			name.text = "${it.name}"
			symbol.text = "${it.symbol}"
			price.text = "${it.price}"
			changePercent.text = "${it.changePercent24h}"
			marketCap.text = "${it.marketCap}B"
			volume.text = "${it.volume}B"
		}
	}
	
	init {
		hasArrow = false
		linearLayout {
			layoutParams = LayoutParams(matchParent, 50.uiPX())
			setMargins<RelativeLayout.LayoutParams> {
				leftMargin = 10.uiPX()
				rightMargin = 10.uiPX()
			}
			addView(rank)
			addView(icon)
			verticalLayout {
				layoutParams = LayoutParams(symbolWidth, matchParent)
				gravity = Gravity.CENTER
				addView(symbol)
				addView(name)
			}
			addView(price)
			addView(changePercent)
			verticalLayout {
				layoutParams = LayoutParams(matchParent, matchParent)
				gravity = Gravity.CENTER
				addView(marketCap)
				addView(volume)
			}
		}
	}
	
	
	
	
}



