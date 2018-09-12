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
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.wrapContent

/**
 * @date 2018/6/25 1:34 PM
 * @author KaySaith
 */
class ViewPagerMenu(context: Context) : LinearLayout(context) {
	
	private var titles: ArrayList<String> by observing(arrayListOf()) {
		val unitWidth = if (titles.size < 4) ScreenSize.Width / titles.size else 100.uiPX()
		underLineWidth = unitWidth.toFloat()
		titles.forEachIndexed { index, content ->
			textView(content) {
				id = index
				textSize = fontSize(14)
				typeface = GoldStoneFont.heavy(context)
				textColor = Spectrum.white
				gravity = Gravity.CENTER
				layoutParams = LinearLayout.LayoutParams(unitWidth, 43.uiPX())
			}
		}
		invalidate()
	}
	private val paint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.STROKE
		strokeWidth = fontSize(7)
	}
	private val barHeight = 45.uiPX()
	private var underLineLeft = 0f
	private var underLineWidth = 0f
	private val boderSize = fontSize(4)
	
	init {
		setWillNotDraw(false)
		layoutParams = LinearLayout.LayoutParams(wrapContent, barHeight)
		backgroundColor = Spectrum.deepBlue
		elevation = 3.uiPX().toFloat()
	}
	
	fun setMemnuTitles(titles: ArrayList<String>, hold: (button: TextView, index: Int) -> Unit) {
		this.titles = titles
		(0 until titles.size).forEach {
			findViewById<TextView>(it)?.let {
				hold(it, it.id)
			}
		}
	}
	
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		paint.color = Spectrum.lightBlue
		canvas?.drawLine(
			underLineLeft,
			height - boderSize,
			underLineLeft + underLineWidth,
			height - boderSize,
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