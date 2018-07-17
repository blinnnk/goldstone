package io.goldstone.blockchain.common.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent

/**
 * @date 25/04/2018 8:06 AM
 * @author KaySaith
 */
open class TopBottomLineCell(context: Context) : LinearLayout(context) {
	
	private val titleHeight = 40.uiPX()
	private val title = TextView(context).apply {
		typeface = GoldStoneFont.medium(context)
		layoutParams = RelativeLayout.LayoutParams(wrapContent, titleHeight)
		setMargins<RelativeLayout.LayoutParams> { topMargin = 10.uiPX() }
	}
	private val button = TextView(context).apply {
		textSize = fontSize(12)
		textColor = Spectrum.green
		typeface = GoldStoneFont.medium(context)
		layoutParams = RelativeLayout.LayoutParams(wrapContent, titleHeight)
		gravity = Gravity.END
		setMargins<RelativeLayout.LayoutParams> { topMargin = 10.uiPX() }
	}
	var showTopLine: Boolean = false
	private var titleLayout: RelativeLayout
	
	init {
		orientation = VERTICAL
		this.setWillNotDraw(false)
		layoutParams = LinearLayout.LayoutParams(matchParent, 40.uiPX())
		titleLayout = relativeLayout {
			lparams(matchParent, titleHeight)
			title.into(this)
		}
	}
	
	private val paint = Paint().apply {
		isAntiAlias = true
		color = GrayScale.midGray
		style = Paint.Style.FILL
	}
	
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		showTopLine.isTrue {
			canvas?.drawLine(0f, 0f, width.toFloat(), 0f, paint)
		}
		canvas?.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), paint)
	}
	
	fun setTitle(
		text: String,
		textSize: Float = fontSize(12),
		textColor: Int = GrayScale.midGray
	) {
		title.text = text
		title.textSize = textSize
		title.textColor = textColor
	}
	
	fun showButton(text: String, event: () -> Unit) {
		button
			.apply { this.text = text }
			.click { event() }
			.into(titleLayout)
		button.setAlignParentRight()
	}
	
	fun hideButton() {
		button.visibility = View.GONE
	}
	
	fun updateButtonTitle(text: String) {
		button.text = text
	}
}