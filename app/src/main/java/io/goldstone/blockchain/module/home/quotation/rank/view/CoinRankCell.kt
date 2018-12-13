package io.goldstone.blockchain.module.home.quotation.rank.view

import android.content.Context
import android.view.Gravity
import android.widget.*
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.module.home.quotation.rank.model.CoinRankModel
import org.jetbrains.anko.*

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
		rank.text = "${model?.rank}"
		icon.glideImage("${model?.icon}")
		name.text = "${model?.name}"
		symbol.text = "${model?.symbol}"
		price.text = "${model?.price}"
		changePercent.text = "${model?.changePercent24h}"
		marketCap.text = "${model?.marketCap}"
	}
	
	init {
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



