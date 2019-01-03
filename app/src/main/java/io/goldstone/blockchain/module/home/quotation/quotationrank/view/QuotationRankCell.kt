package io.goldstone.blockchain.module.home.quotation.quotationrank.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.quotation.quotationrank.model.QuotationRankTable
import io.goldstone.blockchain.module.home.quotation.quotationrank.presenter.QuotationRankPresenter
import org.jetbrains.anko.*
import java.math.BigDecimal


/**
 * @author KaySaith
 * @date  2019/01/02
 */
class QuotationRankCell(context: Context) : BaseCell(context) {
	
	private val cellHeight = 50.uiPX()
	private val cellWidth = ScreenSize.Width - 20.uiPX()
	private val rankWidth = cellWidth / 10
	private val iconWidth = cellWidth / 6
	private val symbolWidth = cellWidth / 3
	private val priceWidth = cellWidth / 5
	
	private val rank = TextView(context).apply {
		layoutParams = LayoutParams(rankWidth, matchParent)
		gravity = Gravity.CENTER
	}
	private val icon = ImageView(context).apply {
		layoutParams = LayoutParams(Math.min(cellHeight, iconWidth), matchParent)
		gravity = Gravity.CENTER
		setColorFilter(Spectrum.white)
		padding = 5.uiPX()
		addCorner(layoutParams.width / 2, Spectrum.white)
	}
	private val name = TextView(context).apply {
		singleLine = true
	}
	private val symbol = TextView(context)
	private val price = TextView(context).apply {
		gravity = Gravity.CENTER
	}
	private val changePercent = TextView(context).apply {
		gravity = Gravity.CENTER
	}
	private val marketCap = TextView(context).apply {
		layoutParams = LayoutParams(matchParent, wrapContent)
		gravity = Gravity.END
	}
	private val volume = TextView(context).apply {
		layoutParams = LayoutParams(matchParent, wrapContent)
		gravity = Gravity.END
	}
	
	var model: QuotationRankTable? by observing(null) {
		model?.let { it ->
			rank.text = "${it.rank}"
			icon.apply {
				if (it.icon.isNotEmpty()) {
					glideImage(it.icon)
					if (it.color.isNotEmpty()) addCorner(layoutParams.width / 2, Color.parseColor(it.color))
				} else {
					glideImage(null)
					addCorner(layoutParams.width / 2, Spectrum.white)
				}
			}
			
			name.text = it.name
			symbol.text = it.symbol
			price.text = "${BigDecimal(it.price.toString()).setScale(2, BigDecimal.ROUND_HALF_UP)}"
			changePercent.text = it.changePercent24h
			if (it.changePercent24h.contains("-")) {
				changePercent.setTextColor(Spectrum.green)
			} else {
				changePercent.setTextColor(Spectrum.lightRed)
			}
			marketCap.text = QuotationRankPresenter.parseVolumeText(it.marketCap.replace(",", ""))
			volume.text = QuotationRankPresenter.parseVolumeText(it.volume.replace(",", ""))
		}
	}
	
	init {
		hasArrow = false
		leftPadding = 10.uiPX()
		rightPadding = 10.uiPX()
		linearLayout {
			layoutParams = LayoutParams(matchParent, 50.uiPX())
			addView(rank)
			addView(icon)
			verticalLayout {
				layoutParams = LayoutParams(symbolWidth, matchParent)
				setMargins<RelativeLayout.LayoutParams> { leftMargin = 10.uiPX()  }
				gravity = Gravity.CENTER
				addView(symbol)
				addView(name)
			}
			verticalLayout {
				layoutParams = LayoutParams(priceWidth, 50.uiPX())
				gravity = Gravity.CENTER_VERTICAL
				addView(price)
				addView(changePercent)
			}
			verticalLayout {
				layoutParams = LayoutParams(matchParent, matchParent)
				gravity = Gravity.CENTER
				addView(marketCap)
				addView(volume)
			}
		}
	}
	
	
}