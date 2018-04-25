package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.animation.OvershootInterpolator
import android.widget.RelativeLayout
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import com.db.chart.animation.Animation
import com.db.chart.model.LineSet
import com.db.chart.model.Point
import com.db.chart.view.LineChartView
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.matchParent

/**
 * @date 25/04/2018 7:44 AM
 * @author KaySaith
 */

class MarketTokenChart(context: Context) : LineChartView(context) {

  private var chartLineColor = Spectrum.green
  private var chartColor = Spectrum.lightGreen

  var chartData: ArrayList<Point> by observing(arrayListOf()) {
    val dataSet = LineSet()
    dataSet.apply {
      chartData.forEach { addPoint(it) }
      // 这个是线的颜色
      color = chartLineColor
      // 渐变色彩
      setGradientFill(intArrayOf(chartColor, Color.TRANSPARENT), floatArrayOf(0.28f, 1f))
      // 线条联动用贝塞尔曲线
      isSmooth = true
      // 线条的粗细
      thickness = 3.uiPX().toFloat()
      // 设定字体
      setTypeface(GoldStoneFont.heavy(context))
      setFontSize(9.uiPX())
    }

    data.isEmpty() isTrue {
      addData(dataSet)
      notifyDataUpdate()
    } otherwise {
      data.clear()
      addData(dataSet)
      notifyDataUpdate()
    }

    val animation = Animation(1000)
    animation.setInterpolator(OvershootInterpolator())
    show(animation)

  }

  init {
    id = ElementID.chartView
    layoutParams = RelativeLayout.LayoutParams(matchParent, 150.uiPX())
    setMargins<RelativeLayout.LayoutParams> { topMargin = 20.uiPX() }
    // 设定背景的网格
    setGrid(5,
      10,
      Paint().apply { isAntiAlias = true; style = Paint.Style.FILL; color = GrayScale.lightGray })
    // 设定便捷字体颜色
    setLabelsColor(GrayScale.midGray)
    // 设定 `Y` 周波段
    setAxisBorderValues(0f, 100f, 25f)
    // 设定外界 `Border` 颜色
    setAxisColor(Color.argb(0, 0, 0, 0))
    // 设定外边的 `Border` 的粗细
    setAxisThickness(0f)
    setClickablePointRadius(30.uiPX().toFloat())
  }

}