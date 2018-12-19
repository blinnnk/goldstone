package io.goldstone.blockchain.common.component.cell

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.ViewManager
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import com.blinnnk.extension.alignParentRight
import com.blinnnk.extension.centerInVertical
import com.blinnnk.extension.isDefaultStyle
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.sdk27.coroutines.onClick


/**
 * @author KaySaith
 * @date  2018/12/18
 */
class SwitchCell(context: Context) : RelativeLayout(context) {

	var horizontalSize: Float by observing(0f) {
		leftPadding = horizontalSize.toInt()
		rightPadding = horizontalSize.toInt()
		invalidate()
	}

	var hasTopLine = false
	var clickEvent: Runnable? = null

	private var switch: Switch
	private var textView: TextView

	init {
		setWillNotDraw(false)
		layoutParams = RelativeLayout.LayoutParams(matchParent, wrapContent)
		minimumHeight = 50.uiPX()
		textView = textView {
			textSize = fontSize(14)
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.heavy(context)
		}
		switch = switch {
			isDefaultStyle(Spectrum.blue)
			onClick {
				clickEvent?.run()
				preventDuplicateClicks()
			}
		}
		textView.centerInVertical()
		switch.centerInVertical()
		switch.alignParentRight()
	}

	fun setSelectedStatus(status: Boolean) {
		switch.isChecked = status
	}

	fun setTitle(text: String) {
		textView.text = text
	}

	private val paint = Paint().apply {
		isAntiAlias = true
		color = GrayScale.midGray
		style = Paint.Style.FILL
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		canvas?.drawLine(
			horizontalSize,
			height.toFloat(),
			width - horizontalSize,
			height.toFloat(),
			paint
		)
		if (hasTopLine) canvas?.drawLine(
			horizontalSize,
			0f,
			width - horizontalSize,
			0f,
			paint
		)
	}
}


fun ViewManager.switchCell() = switchCell {}
inline fun ViewManager.switchCell(init: SwitchCell.() -> Unit) = ankoView({ SwitchCell(it) }, 0, init)