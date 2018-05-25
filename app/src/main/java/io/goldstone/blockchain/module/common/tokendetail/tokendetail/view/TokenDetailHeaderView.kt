package io.goldstone.blockchain.module.common.tokendetail.tokendetail.view

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.animation.OvershootInterpolator
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import com.db.chart.animation.Animation
import com.db.chart.model.LineSet
import com.db.chart.model.Point
import com.db.chart.view.LineChartView
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.TokenDetailSize
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.toast
import java.util.*

/**
 * @date 27/03/2018 3:20 PM
 * @author KaySaith
 */

class TokenDetailHeaderView(context: Context) : RelativeLayout(context) {

	private val chartView = LineChartView(context)
	private var maxY = 100f
	private var unitY = 10f
	private var chartData: ArrayList<Point>? by observing(null) {
		chartView.apply {
			data.isNotEmpty() isTrue { data.clear() }
			// 设定背景的网格
			setGrid(5, 10,
				Paint().apply { isAntiAlias = true; style = Paint.Style.FILL; color = GrayScale.lightGray })
			// 设定便捷字体颜色
			setLabelsColor(GrayScale.midGray)
			// 设定 `Y` 周波段
			setAxisBorderValues(0f, maxY, unitY)
			// 设定外界 `Border` 颜色
			setAxisColor(Color.argb(0, 0, 0, 0))
			// 设定外边的 `Border` 的粗细
			setAxisThickness(0f)

			val dataSet = LineSet()
			dataSet.apply {
				chartData?.forEach { addPoint(it) }
				// 这个是线的颜色
				color = Spectrum.darkBlue
				// 这个是点的颜色 `circle` 和 `border`
				setDotsColor(Spectrum.white)
				setDotsStrokeColor(Spectrum.blue)
				setDotsRadius(5.uiPX().toFloat())
				setDotsStrokeThickness(3.uiPX().toFloat())
				// 渐变色彩
				setGradientFill(
					intArrayOf(Spectrum.green, Color.argb(0, 255, 255, 255)), floatArrayOf(0.28f, 1f)
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

			setClickablePointRadius(30.uiPX().toFloat())
			setOnEntryClickListener { _, entryIndex, _ ->
				context.toast(chartData!![entryIndex].value.toString())
			}

			try {
				notifyDataUpdate()
			} catch (error: Exception) {
				LogUtil.error(this.javaClass.simpleName, error)
			}

			val animation = Animation(1000)
			animation.setInterpolator(OvershootInterpolator())
			show(animation)
		}
	}

	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, TokenDetailSize.headerHeight)
		chartView.apply {
			layoutParams = RelativeLayout.LayoutParams(ScreenSize.Width - 20.uiPX(), matchParent)
			setMargins<RelativeLayout.LayoutParams> { margin = 10.uiPX() }
		}.into(this)
	}

	fun setCharData(
		data: ArrayList<Point>,
		maxY: Float,
		unitY: Float
	) {
		this.maxY = maxY
		this.unitY = unitY
		chartData = data
	}
}