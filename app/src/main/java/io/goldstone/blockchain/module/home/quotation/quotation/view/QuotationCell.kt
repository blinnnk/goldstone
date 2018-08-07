package io.goldstone.blockchain.module.home.quotation.quotation.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import com.github.mikephil.charting.data.Entry
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.component.chart.line.LineChart
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import org.jetbrains.anko.*

@SuppressLint("SetTextI18n")
/**
 * @date 20/04/2018 8:18 PM
 * @author KaySaith
 */
class QuotationCell(context: Context) : LinearLayout(context) {
	
	var model: QuotationModel by observing(QuotationModel()) {
		val price =
			if (model.price == ValueTag.emptyPrice) model.price
			else model.price.toDoubleOrNull()?.toBigDecimal().toString()
		
		tokenInfo.title.text = model.pairDisplay.toUpperCase()
		tokenInfo.subtitle.text = model.name
		tokenPrice.title.text = CustomTargetTextStyle(
			model.quoteSymbol.toUpperCase(),
			model.quoteSymbol.toUpperCase() + " " + price,
			GrayScale.midGray, 13.uiPX(),
			false,
			false
		)
		
		tokenPrice.subtitle.text = model.percent + "%"
		when {
			model.isDisconnected -> {
				tokenPrice.setColorStyle(GrayScale.midGray)
			}
			
			model.percent.toDouble() < 0 -> {
				tokenPrice.setColorStyle(Spectrum.red)
				blinnnkLineChart.setChartColor(Spectrum.red)
				blinnnkLineChart.setShadowResource(R.drawable.fade_red)
			}
			
			else -> {
				tokenPrice.setColorStyle(Spectrum.green)
				blinnnkLineChart.setChartColor(Spectrum.green)
				blinnnkLineChart.setShadowResource(R.drawable.fade_green)
			}
		}
		
		var entrySet = arrayListOf<Entry>()
		model.chartData.forEachIndexed {
			index, point -> entrySet.add(Entry(index.toFloat(), point.value, point.label))
		}
		blinnnkLineChart.resetData(entrySet)
		
		exchangeName.text = model.exchangeName
	}
	private val tokenInfo by lazy {
		TwoLineTitles(context).apply {
			x += 20.uiPX()
			setBlackTitles()
			setQuotationStyle()
		}
	}
	private val tokenPrice by lazy {
		TwoLineTitles(context).apply {
			x -= 20.uiPX()
			setQuotationStyle()
			setColorStyle(Spectrum.green)
			isFloatRight = true
		}
	}
	private val exchangeName by lazy {
		TextView(context).apply {
			textSize = fontSize(12)
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.medium(context)
			gravity = Gravity.END
			layoutParams = RelativeLayout.LayoutParams(
				matchParent, 20.uiPX()
			)
			x -= 20.uiPX()
			y += 52.uiPX()
		}
	}
	
	private val blinnnkLineChart = object : LineChart(context) {
		override fun isDrawPoints(): Boolean = false

		override fun isPerformBezier(): Boolean = true

	}
	
	init {
		orientation = VERTICAL
		gravity = Gravity.CENTER_HORIZONTAL
		layoutParams = LinearLayout.LayoutParams(matchParent, 180.uiPX())
		
		relativeLayout {
			layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, 170.uiPX())
			addCorner(CornerSize.default.toInt(), Spectrum.white)
			
			addView(tokenInfo)
			addView(tokenPrice)
			
			addView(exchangeName)
			
			tokenPrice.setAlignParentRight()
			
			blinnnkLineChart.apply {
				id = ElementID.chartView
				layoutParams = RelativeLayout.LayoutParams(matchParent, 110.uiPX())
				setMargins<RelativeLayout.LayoutParams> {
					margin = 10.uiPX()
				}
				y = 45.uiPX().toFloat()
			}.into(this)
		}
	}
}