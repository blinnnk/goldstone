package io.goldstone.blinnnk.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.HomeSize
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.wrapContent

/**
 * @date 2018/6/25 1:34 PM
 * @author KaySaith
 */
@SuppressLint("ViewConstructor")
class ViewPagerMenu(
	context: Context,
	maxWidth: Int = ScreenSize.Width,
	titleColor: Int = Spectrum.white,
	titleSize: Int = 12
) : LinearLayout(context) {

	private var titles: List<String> by observing(arrayListOf()) {
		val unitWidth = if (titles.size < 4) maxWidth / titles.size else 100.uiPX()
		underLineWidth = unitWidth.toFloat()
		titles.forEachIndexed { index, content ->
			textView(content) {
				id = index
				textSize = fontSize(titleSize)
				typeface = GoldStoneFont.heavy(context)
				textColor = titleColor
				gravity = Gravity.CENTER
				layoutParams = LinearLayout.LayoutParams(unitWidth, HomeSize.menuHeight - 2.uiPX())
			}
		}
		invalidate()
	}
	private val paint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.STROKE
		strokeWidth = fontSize(7)
	}

	private val defaultLinePaint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.STROKE
		strokeWidth = 2f
	}

	private var underLineLeft = 0f
	private var underLineWidth = 0f
	private val borderSize = fontSize(4)

	init {
		setWillNotDraw(false)
		layoutParams = LinearLayout.LayoutParams(wrapContent, HomeSize.menuHeight)
		backgroundColor = Spectrum.deepBlue
		elevation = 3.uiPX().toFloat()
	}

	fun setMenuTitles(titles: List<String>, hold: (button: TextView, index: Int) -> Unit) {
		this.titles = titles
		(0 until titles.size).forEach { index ->
			findViewById<TextView>(index)?.let {
				hold(it, it.id)
			}
		}
	}

	private var defaultLineColor: Int? = null

	fun setColor(backgroundColor: Int, lineColor: Int, defaultLineColor: Int) {
		paint.color = lineColor
		if (defaultLineColor != Color.TRANSPARENT) {
			this.defaultLineColor = defaultLineColor
			defaultLinePaint.color = defaultLineColor
		}
		this.backgroundColor = backgroundColor
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)

		defaultLineColor?.let {
			canvas?.drawLine(
				0f,
				height - 2f,
				width.toFloat(),
				height - 2f,
				defaultLinePaint
			)
		}

		canvas?.drawLine(
			underLineLeft,
			height - borderSize,
			underLineLeft + underLineWidth,
			height - borderSize,
			paint
		)
	}

	fun getUnitWidth(): Float {
		return underLineWidth
	}

	fun moveUnderLine(distance: Float) {
		underLineLeft = distance
		invalidate()
	}
}