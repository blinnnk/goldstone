package io.goldstone.blockchain.module.home.quotation.rank.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.isTrue
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.quotation.rank.model.CoinRankModel
import org.jetbrains.anko.*
import java.lang.Exception

/**
 * @date: 2018-12-12.
 * @author: yangLiHai
 * @description:
 */
class CoinRankCell(context: Context): BaseCell(context) {
	private val rank = TextView(context).apply {
		layoutParams = LayoutParams(ScreenSize.Width / 6, matchParent)
		gravity = Gravity.CENTER
	}
	private val icon = ImageView(context).apply {
		layoutParams = LayoutParams(ScreenSize.Width / 6, matchParent)
		gravity = Gravity.CENTER
		addCorner(ScreenSize.Width / 12, Spectrum.white)
	}
	private val name = TextView(context)
	private val symbol = TextView(context)
	private val price = TextView(context).apply {
		layoutParams = LayoutParams(ScreenSize.Width / 6, matchParent)
		gravity = Gravity.CENTER
	}
	private val changePercent = TextView(context).apply {
		layoutParams = LayoutParams(ScreenSize.Width / 6, matchParent)
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
			marketCap.text = "${it.marketCap}"
		}
	}
	
	init {
		hasArrow = false
		linearLayout {
			layoutParams = LayoutParams(matchParent, 50.uiPX())
			addView(rank)
			addView(icon)
			verticalLayout {
				layoutParams = LayoutParams(ScreenSize.Width / 6, matchParent)
				gravity = Gravity.CENTER
				addView(name)
				addView(symbol)
			}
			addView(price)
			addView(changePercent)
			verticalLayout {
				layoutParams = LayoutParams(ScreenSize.Width / 6, matchParent)
				gravity = Gravity.CENTER
				addView(marketCap)
				addView(volume)
			}
		}
	}
	
	
	
	
}



