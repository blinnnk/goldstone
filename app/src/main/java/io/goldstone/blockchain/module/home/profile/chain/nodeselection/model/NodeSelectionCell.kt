package io.goldstone.blockchain.module.home.profile.chain.nodeselection.model

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.alignParentRight
import com.blinnnk.extension.centerInVertical
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.radioButton
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent


/**
 * @date 2018/6/20 9:00 PM
 * @author KaySaith
 */
class NodeSelectionCell(context: Context) : RelativeLayout(context) {

	private var radio: RadioButton
	private val title = TextView(context).apply {
		textSize = fontSize(14)
		textColor = GrayScale.gray
		typeface = GoldStoneFont.heavy(context)
		layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent)
	}
	private val paint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		color = GrayScale.midGray
	}

	private val leftPadding = 50.uiPX()

	init {
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 40.uiPX())
		setWillNotDraw(false)
		title.into(this)
		title.x = leftPadding.toFloat()
		title.centerInVertical()
		radio = radioButton {
			//			isDefaultStyle(Spectrum.blue)
			isClickable = false
		}
		radio.alignParentRight()
		radio.centerInVertical()
	}

	@SuppressLint("DrawAllocation")
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)

		canvas?.drawLine(
			leftPadding.toFloat(),
			height.toFloat(),
			width.toFloat(),
			height.toFloat(),
			paint
		)

		canvas?.drawLine(
			20.uiPX().toFloat(),
			height / 2f,
			45.uiPX().toFloat(),
			height / 2f,
			paint
		)

		canvas?.drawLine(
			20.uiPX().toFloat(),
			0f,
			20.uiPX().toFloat(),
			height.toFloat(),
			paint
		)
	}

	fun setSelectedStatus(status: Boolean) {
		radio.isChecked = status
	}

	fun isChecked() = radio.isChecked

	fun setData(name: String, isSelected: Boolean, id: Int): NodeSelectionCell {
		title.text = name
		radio.isChecked = isSelected
		this.id = id
		return this
	}
}