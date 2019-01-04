package io.goldstone.blinnnk.module.home.quotation.quotationrank.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blinnnk.common.base.basecell.BaseCell
import io.goldstone.blinnnk.common.component.title.TwoLineTitles
import io.goldstone.blinnnk.common.component.title.twoLineTitles
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.utils.glideImage
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.PaddingSize
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.crypto.utils.formatCurrency
import io.goldstone.blinnnk.module.home.quotation.quotationrank.model.QuotationRankTable
import io.goldstone.blinnnk.module.home.quotation.quotationrank.presenter.QuotationRankPresenter
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2019/01/02
 */
class QuotationRankCell(context: Context) : BaseCell(context) {

	private val iconSize = 50.uiPX()
	private lateinit var rank: TextView
	private lateinit var icon: ImageView
	private lateinit var nameInfo: TwoLineTitles
	private lateinit var marketInfo: TwoLineTitles

	var model: QuotationRankTable? by observing(null) {
		model?.let { it ->
			rank.text = "${it.rank}.".setItalic()
			with(icon) {
				if (it.url.isNotEmpty()) {
					glideImage("${it.url}?imageView2/1/w/120/h/120")
					if (it.color.isNotEmpty()) addCorner(iconSize, Color.parseColor(it.color))
				} else {
					glideImage(null)
					addCorner(iconSize, GrayScale.whiteGray)
				}
			}
			val priceDescription =
				it.price.formatCurrency() suffix  "(${SharedWallet.getCurrencyCode()})" + " / " + it.changePercent24h
			nameInfo.title.text = it.symbol
			val targetColor = if (it.changePercent24h.contains("-")) Spectrum.red else Spectrum.green
			nameInfo.subtitle.text = CustomTargetTextStyle(
				it.changePercent24h,
				priceDescription,
				targetColor,
				11.uiPX(),
				false,
				false
			)
			marketInfo.title.text = QuotationRankPresenter.parseVolumeText(
				it.marketCap.replace(",", "").toDoubleOrZero().formatCurrency()
			).setItalic()
			
			marketInfo.subtitle.text = ("24H" suffix  QuotationRankPresenter.parseVolumeText(
				it.volume.replace(",", "").toDoubleOrZero().formatCurrency()
			) suffix SharedWallet.getCurrencyCode()).setItalic()
		}
	}

	init {
		setHorizontalPadding(PaddingSize.content)
		hasArrow = false
		setGrayStyle()
		linearLayout {
			gravity = Gravity.CENTER_VERTICAL
			layoutParams = LayoutParams(matchParent, matchParent)
			rank = textView {
				textColor = GrayScale.midGray
				typeface = GoldStoneFont.medium(context)
				layoutParams = LayoutParams(25.uiPX(), wrapContent)
			}
			icon = imageView {
				layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
				setColorFilter(Spectrum.white)
				padding = 5.uiPX()
				addCorner(iconSize, Spectrum.white)
			}
			nameInfo = twoLineTitles {
				layoutParams = LayoutParams(matchParent, wrapContent)
				leftPadding = 10.uiPX()
				setBlackTitles(lineSpace = 1.uiPX())
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