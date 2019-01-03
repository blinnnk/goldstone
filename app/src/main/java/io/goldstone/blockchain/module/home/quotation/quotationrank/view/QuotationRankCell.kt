package io.goldstone.blockchain.module.home.quotation.quotationrank.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.module.home.quotation.quotationrank.model.QuotationRankTable
import io.goldstone.blockchain.module.home.quotation.quotationrank.presenter.QuotationRankPresenter
import org.jetbrains.anko.*
import java.math.BigDecimal


/**
 * @author KaySaith
 * @date  2019/01/02
 */
class QuotationRankCell(context: Context) : BaseCell(context) {
	
	private val iconSize = 50.uiPX()
	private val cellWidth = ScreenSize.widthWithPadding
	
	private val rank = TextView(context).apply {
		layoutParams = LayoutParams(20.uiPX(), wrapContent)
	}
	private val icon = ImageView(context).apply {
		layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
		setColorFilter(Spectrum.white)
		padding = 5.uiPX()
		addCorner(iconSize, Spectrum.white)
	}
	private val name = TextView(context)
	private val symbol = TextView(context)
	private val price = TextView(context)
	private val changePercent = TextView(context)
	private val marketCap = TextView(context)
	private val volume = TextView(context)
	
	var model: QuotationRankTable? by observing(null) {
		model?.let { it ->
			rank.text = "${it.rank}"
			with(icon) {
				if (it.url.isNotEmpty()) {
					glideImage(it.url)
					if (it.color.isNotEmpty()) addCorner(iconSize, Color.parseColor(it.color))
				} else {
					glideImage(null)
					addCorner(iconSize, Spectrum.white)
				}
			}
			name.text = it.name
			symbol.text = it.symbol
			price.text = BigDecimal(it.price.toString()).formatCount(6)
			changePercent.text = it.changePercent24h
			if (it.changePercent24h.contains("-")) {
				changePercent.setTextColor(Spectrum.lightRed)
			} else {
				changePercent.setTextColor(Spectrum.green)
			}
			marketCap.text = QuotationRankPresenter.parseVolumeText(it.marketCap.replace(",", ""))
			volume.text = QuotationRankPresenter.parseVolumeText(it.volume.replace(",", ""))
		}
	}
	
	init {
		hasArrow = false
		linearLayout {
			gravity = Gravity.CENTER_VERTICAL
			layoutParams = LayoutParams(matchParent, matchParent)
			addView(rank)
			addView(icon)
			verticalLayout {
				layoutParams = LayoutParams(cellWidth / 3, wrapContent)
				leftPadding = 10.uiPX()
				addView(symbol)
				addView(name)
			}
			verticalLayout {
				layoutParams = LayoutParams(cellWidth / 5, wrapContent)
				addView(price)
				addView(changePercent)
			}
		}
		verticalLayout {
			layoutParams = LayoutParams(wrapContent, wrapContent)
			addView(marketCap)
			addView(volume)
		}.apply {
			alignParentRight()
			centerInVertical()
		}
	}
	
}