package io.goldstone.blinnnk.module.common.tokendetail.tokendetail.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.component.button.RoundButton
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.value.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent

/**
 * @date 27/03/2018 3:20 PM
 * @author KaySaith
 */
class TokenDetailFooter(context: Context) : LinearLayout(context) {

	val sendButton = RoundButton(context)
	val receivedButton = RoundButton(context)

	init {
		setPadding(PaddingSize.device, 8.uiPX(), 10.uiPX(), 0)
		isClickable = true
		layoutParams = LinearLayout.LayoutParams(matchParent, 60.uiPX())
		backgroundColor = Spectrum.white
		val buttonWidth = ScreenSize.widthWithPadding / 2 - 5.uiPX()
		val buttonHeight = 46.uiPX()
		val marginSize = 15.uiPX()
		sendButton.apply {
			setGreenStyle(marginSize)
			text = CommonText.send
			layoutParams = LinearLayout.LayoutParams(buttonWidth, buttonHeight)
		}.into(this)

		receivedButton.apply {
			text = CommonText.deposit
			setBlueStyle(marginSize)
			layoutParams = LinearLayout.LayoutParams(buttonWidth, buttonHeight).apply {
				leftMargin = 10.uiPX()
			}
		}.into(this)
	}

	private val paint = Paint().apply {
		isAntiAlias = true
		color = GrayScale.Opacity1Black
		style = Paint.Style.FILL
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		canvas?.drawLine(
			PaddingSize.device.toFloat(),
			BorderSize.default,
			width - PaddingSize.device.toFloat(),
			BorderSize.default,
			paint
		)
	}
}