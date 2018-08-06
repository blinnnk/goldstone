package io.goldstone.blockchain.module.home.quotation.quotation.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.numberDate
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import com.db.chart.model.LineSet
import io.goldstone.blockchain.common.component.LineChart
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.utils.daysAgoInMills
import io.goldstone.blockchain.module.home.quotation.quotation.model.ChartPoint
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.textColor

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
				chartView.setDisconnectedStyle()
			}
			
			model.percent.toDouble() < 0 -> {
				tokenPrice.setColorStyle(Spectrum.red)
				chartView.setRedColor()
			}
			
			else -> {
				tokenPrice.setColorStyle(Spectrum.green)
				chartView.setGreenColor()
			}
		}
		
		chartView.updateData(
			model.chartData.map {
				ChartPoint(numberDate(it.label.toLong()), it.value)
			}.toArrayList()
		)
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
	private val chartView = object : LineChart(context) {
		override fun setChartValueType() = LineChart.Companion.ChartType.Quotation
		override fun canClickPoint() = false
		override fun setChartStyle() = LineChart.Companion.Style.LineStyle
		override fun hasAnimation() = false
		override fun setEvnetWhenDataIsEmpty(chartData: ArrayList<ChartPoint>): Boolean {
			return if (model.price != ValueTag.emptyPrice) {
				chartData.addAll(
					arrayListOf(
						ChartPoint(1.daysAgoInMills().toString(), 0f),
						ChartPoint(0.daysAgoInMills().toString(), model.price.toFloat())
					)
				)
				true
			} else {
				false
			}
		}
		
		override fun modifyLineDataSet(chartData: ArrayList<ChartPoint>, dataSet: LineSet) {
			// 比对如果最后一个不是今天那么把当前长连接的价格插入表格
			if (
				chartData.last().label != numberDate(0.daysAgoInMills())
				&& chartData.size < 8
			) {
				dataSet.addPoint(
					numberDate(0.daysAgoInMills()),
					model.price.toFloatOrNull() ?: chartData.last().value
				)
			}
		}
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
			
			chartView.apply {
				id = ElementID.chartView
				layoutParams = RelativeLayout.LayoutParams(matchParent, 90.uiPX())
				setClickablePointRadius(30.uiPX().toFloat())
				setMargins<RelativeLayout.LayoutParams> { margin = 10.uiPX() }
				y = 60.uiPX().toFloat()
			}.into(this)
		}
	}
}