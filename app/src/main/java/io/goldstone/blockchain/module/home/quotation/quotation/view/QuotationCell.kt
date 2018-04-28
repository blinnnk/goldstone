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
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import com.db.chart.model.LineSet
import com.db.chart.model.Point
import com.db.chart.view.LineChartView
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
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
    tokenInfo.title.text = model.symbol
    tokenInfo.subtitle.text = model.name
    tokenPrice.title.text = model.price
    tokenPrice.subtitle.text = model.percent + "%"
    if (model.percent.toDouble() < 0) {
      tokenPrice.setColorStyle(Spectrum.red)
      chartColor = Spectrum.lightRed
      chartLineColor = Spectrum.red
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
      textSize = 4.uiPX().toFloat()
      textColor = GrayScale.midGray
      typeface = GoldStoneFont.medium(context)
      gravity = Gravity.END
      layoutParams = RelativeLayout.LayoutParams(matchParent, 20.uiPX())
      x -= 20.uiPX()
      y += 52.uiPX()
    }
  }

  private var chartColor = Spectrum.lightGreen
  private var chartLineColor = Spectrum.green

  private val chartView = LineChartView(context)

  private var cellLayout: RelativeLayout? = null
  private var chartData: ArrayList<Point> by observing(arrayListOf()) {
    chartView.apply {
      // 设定背景的网格
      setGrid(
        5,
        10,
        Paint().apply { isAntiAlias = true; style = Paint.Style.FILL; color = GrayScale.lightGray })
      // 设定便捷字体颜色
      setLabelsColor(GrayScale.midGray)
      val maxValue = chartData.max()?.value ?: 0f
      val minValue = chartData.min()?.value ?: 0f
      // 设定 `Y` 周波段
      val stepDistance = generateStepDistance(minValue.toDouble(), maxValue.toDouble())
      val max =  (1f + Math.floor((maxValue / stepDistance).toDouble()).toFloat()) * stepDistance
      val min = Math.floor(minValue.toDouble() / stepDistance).toFloat() * stepDistance
      setAxisBorderValues( min, max, stepDistance)
      // 设定外界 `Border` 颜色
      setAxisColor(Color.argb(0, 0, 0, 0))
      // 设定外边的 `Border` 的粗细
      setAxisThickness(0f)

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

      setClickablePointRadius(30.uiPX().toFloat())
      show()
    }
  }

  private fun generateStepDistance(minValue: Double,  maxValue: Double): Float {
    val stepsCount = 5   //代表希望分成几个阶段
    val roughStep = (maxValue - minValue) / stepsCount
    val stepLevel = Math.pow(10.0, Math.floor(Math.log10(roughStep))) //代表gap的数量级
    return (Math.ceil(roughStep / stepLevel) * stepLevel).toFloat()
  }

  init {

    layoutParams = LinearLayout.LayoutParams(matchParent, 180.uiPX())

    cellLayout = relativeLayout {
      layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, 170.uiPX())
      addCorner(CornerSize.default.toInt(), Spectrum.white)
      x += PaddingSize.device

      addView(tokenInfo)
      addView(tokenPrice)

      addView(exchangeName)

      tokenPrice.setAlignParentRight()

      chartView.apply {
        id = ElementID.chartView
        layoutParams = RelativeLayout.LayoutParams(matchParent, 90.uiPX())
        setMargins<RelativeLayout.LayoutParams> { margin = 10.uiPX() }
        y = 60.uiPX().toFloat()
      }.into(this)
    }
  }

}