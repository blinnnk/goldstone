package io.goldstone.blockchain.module.home.quotation.quotation.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.numberDate
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import com.db.chart.model.LineSet
import com.db.chart.view.LineChartView
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.daysAgoInMills
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
		tokenInfo.title.text = model.pairDisplay.toUpperCase()
		tokenInfo.subtitle.text = model.name
		tokenPrice.title.text = CustomTargetTextStyle(
			model.quoteSymbol.toUpperCase(), model.quoteSymbol.toUpperCase() + " " + model.price,
			GrayScale.midGray, 13.uiPX(), false, false
		)
		tokenPrice.subtitle.text = model.percent + "%"
		if (model.percent.toDouble() < 0) {
			tokenPrice.setColorStyle(Spectrum.red)
			chartColor = Spectrum.lightRed
			chartLineColor = Spectrum.red
		} else {
			tokenPrice.setColorStyle(Spectrum.green)
			chartColor = Spectrum.lightGreen
			chartLineColor = Spectrum.green
		}
		
		chartData = model.chartData
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
	private var chartColor = Spectrum.lightGreen
	private var chartLineColor = Spectrum.green
	private val chartView = LineChartView(context)
	private var cellLayout: RelativeLayout
	private var chartData: ArrayList<ChartPoint> by observing(arrayListOf()) {
		if (chartData.isEmpty()) {
			if (model.price != ValueTag.emptyPrice) {
				chartData.addAll(
					arrayListOf(
						ChartPoint(1.daysAgoInMills().toString(), 0f),
						ChartPoint(0.daysAgoInMills().toString(), model.price.toFloat())
					)
				)
			} else {
				return@observing
			}
		}
		
		chartView.apply {
			data.isNotEmpty() isTrue { data.clear() }
			// 设定背景的网格
			setGrid(5, 7, Paint().apply {
				isAntiAlias = true
				style = Paint.Style.FILL; color = GrayScale.lightGray
			})
			// 设定便捷字体颜色
			setLabelsColor(GrayScale.midGray)
			val maxValue = chartData.max()?.value ?: 1f
			val minValue = chartData.min()?.value ?: 0f
			QuotationCell.getChardGridValue(maxValue, minValue) { min, max, step ->
				setAxisBorderValues(min, max, step)
			}
			// 设定外界 `Border` 颜色
			setAxisColor(Color.argb(0, 0, 0, 0))
			// 设定外边的 `Border` 的粗细
			setAxisThickness(0f)
			val dataSet = LineSet()
			dataSet.apply {
				chartData.forEach {
					dataSet.addPoint(numberDate(it.label.toLong()), it.value)
				}
				// 比对如果最后一个不是今天那么把当前长连接的价格插入表格
				if (chartData.last().label.toLong() != 0.daysAgoInMills()) {
					dataSet.addPoint(
						numberDate(0.daysAgoInMills()),
						model.price.toFloatOrNull() ?: chartData.last().value
					)
				}
				// 这个是线的颜色
				color = chartLineColor
				// 渐变色彩
				setGradientFill(
					intArrayOf(chartColor, Color.argb(0, 255, 255, 255)),
					floatArrayOf(0.28f, 1f)
				)
				// 线条联动用贝塞尔曲线
				isSmooth = true
				// 线条的粗细
				thickness = 3.uiPX().toFloat()
				// 设定字体
				setTypeface(GoldStoneFont.heavy(context))
				setFontSize(9.uiPX())
			}
			addData(dataSet)
			try {
				notifyDataUpdate()
				show()
			} catch (error: Exception) {
				LogUtil.error(this.javaClass.simpleName, error)
			}
		}
	}
	
	init {
		orientation = VERTICAL
		gravity = Gravity.CENTER_HORIZONTAL
		layoutParams = LinearLayout.LayoutParams(matchParent, 180.uiPX())
		
		cellLayout = relativeLayout {
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
	
	companion object {
		
		fun getChardGridValue(
			maxValue: Float,
			minValue: Float,
			hold: (min: Float, max: Float, step: Float) -> Unit
		) {
			val stepsCount = 5   //代表希望分成几个阶段
			val max: Double = if (maxValue == minValue) {
				if (maxValue == 0f)
					(stepsCount - 1).toDouble()
				else maxValue + Math.abs(maxValue * 0.5)
			} else {
				maxValue.toDouble()
			}
			val min: Double = if (maxValue == minValue) {
				maxValue - Math.abs(maxValue * 0.5)
			} else {
				minValue.toDouble()
			}
			val roughStep = (max - min) / (stepsCount - 1)
			val stepLevel = Math.pow(10.0, Math.floor(Math.log10(roughStep))) //代表gap的数量级
			val step = (Math.ceil(roughStep / stepLevel) * stepLevel)
			val minChartHeight = Math.floor(min / step).toFloat() * step
			val maxChartHeight = (1.0 + Math.floor(max / step)).toFloat() * step
			hold(minChartHeight.toFloat(), maxChartHeight.toFloat(), step.toFloat())
		}
	}
	
}