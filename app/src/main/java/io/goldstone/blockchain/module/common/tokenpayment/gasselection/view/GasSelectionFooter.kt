package io.goldstone.blockchain.module.common.tokenpayment.gasselection.view

/**
 * @date 2018/5/16 11:43 PM
 * @author KaySaith
 */

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView

/**
 * @date 28/03/2018 2:34 PM
 * @author KaySaith
 */

class GasSelectionFooter(context: Context) : LinearLayout(context) {

	private val customButton by lazy { BaseCell(context) }
	private val confirmButton by lazy { RoundButton(context) }

	init {

		orientation = VERTICAL
		gravity = Gravity.CENTER_HORIZONTAL

		layoutParams = LinearLayout.LayoutParams(matchParent, 120.uiPX())

		customButton.apply {
			layoutParams = LinearLayout.LayoutParams(matchParent, 40.uiPX())
			textView {
				setGrayStyle()
				text = TokenDetailText.customMiner
				textColor = GrayScale.gray
				textSize = fontSize(15)
				typeface = GoldStoneFont.book(context)
			}.setCenterInVertical()
		}.into(this)

		confirmButton.apply {
			setBlueStyle(20.uiPX())
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

	fun getCustomButton(action: BaseCell.() -> Unit) {
		action(customButton)
	}

}