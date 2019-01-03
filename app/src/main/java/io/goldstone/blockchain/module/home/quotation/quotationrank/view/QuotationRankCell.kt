package io.goldstone.blockchain.module.home.quotation.quotationrank.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.component.title.twoLineTitles
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
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
		textColor = GrayScale.midGray
		typeface = GoldStoneFont.medium(context)
		layoutParams = LayoutParams(25.uiPX(), wrapContent)
	}
	private val icon = ImageView(context).apply {
		layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
		setColorFilter(Spectrum.white)
		padding = 5.uiPX()
		addCorner(iconSize, Spectrum.white)
	}
	private lateinit var nameInfo: TwoLineTitles
	private lateinit var priceInfo: TwoLineTitles
	private lateinit var marketInfo: TwoLineTitles

	var model: QuotationRankTable? by observing(null) {
		model?.let { it ->
			rank.text = "${it.rank}".setItalic()
			with(icon) {
				if (it.url.isNotEmpty()) {
					glideImage(it.url)
					if (it.color.isNotEmpty()) addCorner(iconSize, Color.parseColor(it.color))
				} else {
					glideImage(null)
					addCorner(iconSize, Spectrum.white)
				}
			}
			nameInfo.title.text = it.symbol
			nameInfo.subtitle.text = it.name
			priceInfo.title.text = BigDecimal(it.price.toString()).formatCount(6)
			priceInfo.subtitle.text = it.changePercent24h
			if (it.changePercent24h.contains("-")) {
				priceInfo.subtitle.setTextColor(Spectrum.lightRed)
			} else {
				priceInfo.subtitle.setTextColor(Spectrum.green)
			}
			marketInfo.title.text =
				QuotationRankPresenter.parseVolumeText(it.marketCap.replace(",", "")).setItalic()
			marketInfo.subtitle.text =
				QuotationRankPresenter.parseVolumeText(it.volume.replace(",", "")).setItalic()
		}
	}
	
	init {
		setHorizontalPadding(PaddingSize.content)
		hasArrow = false
		setGrayStyle()
		linearLayout {
			gravity = Gravity.CENTER_VERTICAL
			layoutParams = LayoutParams(matchParent, matchParent)
			addView(rank)
			addView(icon)
			nameInfo = twoLineTitles {
				layoutParams = LayoutParams(cellWidth / 3, wrapContent)
				leftPadding = 10.uiPX()
				setBlackTitles()
			}
			priceInfo = twoLineTitles {
				layoutParams = LayoutParams(80.uiPX(), wrapContent)
				setBlackTitles()
			}
		}
		marketInfo = twoLineTitles {
			layoutParams = LayoutParams(wrapContent, wrapContent)
			isFloatRight = true
			setGrayTitles()
		}.apply {
			alignParentRight()
			x -= PaddingSize.content
			centerInVertical()
		}
	}
	
}