package io.goldstone.blockchain.module.home.profile.chain.nodeselection.model

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.component.HoneyRadioButton
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent

/**
 * @date 2018/6/20 9:00 PM
 * @author KaySaith
 */
class NodeSelectionCell(context: Context) : RelativeLayout(context) {
	
	private val radio = HoneyRadioButton(context).apply {
		layoutParams = LinearLayout.LayoutParams(50.uiPX(), 50.uiPX())
		setColorStyle(GrayScale.midGray, Spectrum.green)
	}
	private val title = TextView(context).apply {
		textSize = fontSize(14)
		textColor = GrayScale.black
		typeface = GoldStoneFont.heavy(context)
		layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent)
	}
	private val paint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		color = GrayScale.midGray
	}
	
	init {
		layoutParams = LinearLayout.LayoutParams(matchParent, 50.uiPX())
		setWillNotDraw(false)
		title.into(this)
		title.x = 70.uiPX().toFloat()
		title.setCenterInVertical()
		radio.into(this)
		radio.setAlignParentRight()
		radio.isClickable = false
	}
	
	@SuppressLint("DrawAllocation")
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		
		canvas?.drawLine(
			70.uiPX().toFloat(),
			height - BorderSize.default,
			width - PaddingSize.device * 1f,
			height - BorderSize.default,
			paint
		)
	}
	
	fun selectRadio() {
		radio.isChecked = true
	}
	
	fun clearRadio() {
		radio.isChecked = false
	}
	
	fun setData(name: String, isSelected: Boolean, id: Int? = null): NodeSelectionCell {
		title.text = name
		radio.isChecked = isSelected
		id?.let { this.id = it }
		return this
	}
}