package io.goldstone.blockchain.common.component.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.isNull
import com.github.mikephil.charting.components.IMarker
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import io.goldstone.blockchain.R
import org.jetbrains.anko.*

/**
 * @date: 2018/8/7.
 * @author: yanglihai
 * @description:
 */
abstract class BaseMarkerView(context: Context) : RelativeLayout(context), IMarker {
	private var offsetMPPointF: MPPointF = MPPointF()
	private val drawingOffsetMPPointF = MPPointF()

	protected var textViewContent: TextView = TextView(context)

	init {
		with(this) {
			relativeLayout {
				backgroundResource = R.drawable.btn_round_gray
				textViewContent.apply {
					textSize = 10f
					textColor = Color.BLACK
				}.lparams(wrapContent, wrapContent) {
					margin = 10
				}
				addView(textViewContent)
				measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
					View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
				layout(0, 0, measuredWidth, measuredHeight)
			}
		}

	}

	fun setOffset(offset: MPPointF) {
		offsetMPPointF = offset

		if (offsetMPPointF.isNull()) {
			offsetMPPointF = MPPointF()
		}
	}

	fun setOffset(
		offsetX: Float,
		offsetY: Float
	) {
		offsetMPPointF.x = offsetX
		offsetMPPointF.y = offsetY
	}

	abstract fun getChartWidth(): Int

	abstract fun getChartHeight(): Int

	override fun getOffset(): MPPointF {
		return offsetMPPointF
	}

	override fun getOffsetForDrawingAtPoint(
		positionX: Float,
		positionY: Float
	): MPPointF {
		val offset = getOffset()
		drawingOffsetMPPointF.x = offset.x
		drawingOffsetMPPointF.y = offset.y


		val chartWidht = getChartWidth()
		val chartHeight = getChartHeight()

		val width = width.toFloat()
		val height = height.toFloat()

		if (positionX + drawingOffsetMPPointF.x < 0) {
			drawingOffsetMPPointF.x = -positionX
		} else if (positionX + width + drawingOffsetMPPointF.x > chartWidht) {
			drawingOffsetMPPointF.x = chartWidht.toFloat() - positionX - width
		}

		if (positionY + drawingOffsetMPPointF.y < 0) {
			drawingOffsetMPPointF.y = -positionY
		} else if (positionY + height + drawingOffsetMPPointF.y > chartHeight) {
			drawingOffsetMPPointF.y = chartHeight.toFloat() - positionY - height
		}

		return drawingOffsetMPPointF
	}

	override fun refreshContent(
		entry: Entry,
		highlight: Highlight
	) {

		measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
			View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
		layout(0, 0, measuredWidth, measuredHeight)

	}

	override fun draw(
		canvas: Canvas,
		positionX: Float,
		positionY: Float
	) {

		val offset = getOffsetForDrawingAtPoint(positionX, positionY)

		val saveId = canvas.save()
		// translate to the correct position and draw
		canvas.translate(positionX + offset.x, positionY + offset.y)
		draw(canvas)
		canvas.restoreToCount(saveId)
	}
}
