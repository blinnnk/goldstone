package io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView

/**
 * @date 28/03/2018 2:34 PM
 * @author KaySaith
 */

class PaymentValueDetailFooter(context: Context) : LinearLayout(context) {

	var customGasEvent: Runnable? = null
	private val customButton by lazy { BaseCell(context) }
	private val confirmButton by lazy { RoundButton(context) }

	init {

		orientation = VERTICAL
		gravity = Gravity.CENTER_HORIZONTAL

		layoutParams = LinearLayout.LayoutParams(matchParent, 120.uiPX())

		customButton.apply {
			layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 40.uiPX())
			textView {
				setGrayStyle()
				text = TokenDetailText.customMiner
				textColor = GrayScale.gray
				textSize = 5.uiPX().toFloat()
				typeface = GoldStoneFont.book(context)
			}.setCenterInVertical()
		}.click {
			customGasEvent?.run()
		}.into(this)

		confirmButton.apply {
			setGrayStyle(20.uiPX())
			text = CommonText.next.toUpperCase()
		}.into(this)
	}

	fun setCanUseStyle(isSelected: Boolean) {
		if (isSelected) confirmButton.setBlueStyle(20.uiPX())
		else confirmButton.setGrayStyle(20.uiPX())
	}

	fun getConfirmButton(action: RoundButton.() -> Unit) {
		action(confirmButton)
	}

}