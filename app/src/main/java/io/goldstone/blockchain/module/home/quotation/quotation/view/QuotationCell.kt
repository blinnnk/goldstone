package io.goldstone.blockchain.module.home.quotation.quotation.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import com.github.mikephil.charting.data.Entry
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.GSCard
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

@SuppressLint("SetTextI18n")
/**
 * @date 20/04/2018 8:18 PM
 * @author KaySaith
 */
class QuotationCell(context: Context) : LinearLayout(context) {

	// 判断标价是否需要刷新价格及 `LineChart` 的标记
	private var previousPrice: Double? = null
	var model: QuotationModel by observing(QuotationModel()) {
		val price = if (model.price == ValueTag.emptyPrice) model.price
		else model.price.toDoubleOrNull()?.toBigDecimal()?.toPlainString()
		// 如果价格没有变动就不用乡下执行了
		if (price?.toDoubleOrNull().orZero() == previousPrice) return@observing
		tokenInfo.title.text = model.pairDisplay.toUpperCase()
		tokenInfo.subtitle.text = model.name
		tokenPrice.title.text = CustomTargetTextStyle(
			model.quoteSymbol.toUpperCase(),
			model.quoteSymbol.toUpperCase() + " " + price,
			GrayScale.midGray,
			13.uiPX(),
			false,
			false
		)

		tokenPrice.subtitle.text = model.percent + "%"
		when {
			model.isDisconnected -> {
				tokenPrice.setColorStyle(GrayScale.midGray)
				lineChart.setChartColorAndShadowResource(GrayScale.lightGray, R.drawable.fade_gray)
			}

			model.percent.toDouble() < 0 -> {
				tokenPrice.setColorStyle(Spectrum.red)
				lineChart.setChartColorAndShadowResource(Spectrum.red, R.drawable.fade_red)
			}

			else -> {
				tokenPrice.setColorStyle(Spectrum.green)
				lineChart.setChartColorAndShadowResource(Spectrum.green, R.drawable.fade_green)
			}
		}

		lineChart.resetDataWithTargetLabelCount(
			model.chartData.mapIndexed { index, chartPoint ->
				Entry(index.toFloat(), chartPoint.value, chartPoint.label)
			}.toArrayList()
		)

		exchangeName.text = model.exchangeName
		previousPrice = price?.toDoubleOrNull().orZero()
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
	private val lineChart = object : QuotationLineChart(context) {
		override val isDrawPoints: Boolean = false
		override val isPerformBezier: Boolean = true
		override val dragEnable: Boolean = false
		override val touchEnable: Boolean = false
		override val animateEnable: Boolean = false
		override fun lineLabelCount(): Int = 5
	}

	private var cardView: GSCard

	init {
		orientation = VERTICAL
		gravity = Gravity.CENTER_HORIZONTAL
		layoutParams = LinearLayout.LayoutParams(matchParent, 180.uiPX())
		cardView = GSCard(context).apply {
			layoutParams = LinearLayout.LayoutParams(ScreenSize.card, wrapContent)
			relativeLayout {
				layoutParams = RelativeLayout.LayoutParams(ScreenSize.card, 170.uiPX())
				addCorner(CornerSize.default.toInt(), Spectrum.white)

				addView(tokenInfo)
				addView(tokenPrice)

				addView(exchangeName)

				tokenPrice.alignParentRight()

				lineChart.apply {
					id = ElementID.chartView
					layoutParams = RelativeLayout.LayoutParams(matchParent, 110.uiPX())
					setMargins<RelativeLayout.LayoutParams> {
						margin = 10.uiPX()
					}
					y = 45.uiPX().toFloat()
				}.into(this)
			}
		}
		cardView.into(this)
	}

	fun setClickEvent(action: () -> Unit) {
		cardView.onClick {
			action()
			cardView.preventDuplicateClicks()
		}
	}
}