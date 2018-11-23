package io.goldstone.blockchain.common.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*

/**
 * @date 2018/6/25 1:34 PM
 * @author KaySaith
 */
class ViewPagerMenu(context: Context, private var itemWidth: Int = 0) : LinearLayout(context) {
	
	private var titles: ArrayList<String> by observing(arrayListOf()) {
		if (itemWidth == 0) {
			itemWidth = if (titles.size < 4) ScreenSize.Width / titles.size else 100.uiPX()
		}
		underLineWidth = itemWidth.toFloat()
		titles.forEachIndexed { index, content ->
			textView(content) {
				id = index
				textSize = fontSize(14)
				typeface = GoldStoneFont.heavy(context)
				textColor = Spectrum.white
				gravity = Gravity.CENTER
				singleLine = true
				layoutParams = LinearLayout.LayoutParams(itemWidth, 43.uiPX())
			}
		}
		invalidate()
	}
	private val paint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.STROKE
		strokeWidth = fontSize(7)
		color = Spectrum.lightBlue
	}
	private val barHeight = 45.uiPX()
	private var underLineLeft = 0f
	private var underLineWidth = 0f
	private val borderSize = fontSize(4)
	
	init {
		setWillNotDraw(false)
		layoutParams = LinearLayout.LayoutParams(wrapContent, barHeight)
		backgroundColor = Spectrum.deepBlue
		elevation = 3.uiPX().toFloat()
	}
	
	fun setMenuTitles(titles: ArrayList<String>, hold: (button: TextView, index: Int) -> Unit) {
		this.titles = titles
		(0 until titles.size).forEach { index ->
			findViewById<TextView>(index)?.let {
				hold(it, it.id)
			}
		}
	}
	
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
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
	
	fun setBorderLineColor(color: Int) {
		paint.color = color
	}
}