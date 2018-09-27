package io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RadioButton
import android.widget.RelativeLayout
import com.blinnnk.extension.CustomTargetTextStyle
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.view.GrayCardView
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.utils.isDefaultStyle
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.radioButton

/**
 * @author KaySaith
 * @date  2018/09/12
 */
class EOSAccountCell(context: Context) : GrayCardView(context) {
	private val info = TwoLineTitles(context).apply {
		setBlackTitles(fontSize(14), 3.uiPX())
	}

	private val cellContainer = RelativeLayout(context).apply {
		setPadding(15.uiPX(), 10.uiPX(), 15.uiPX(), 10.uiPX())
		layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
	}

	private lateinit var radio: RadioButton

	init {
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, 70.uiPX())
		addView(cellContainer)
		cellContainer.addView(info)
		info.setCenterInVertical()
		cellContainer.apply {
			radio = radioButton {
				isDefaultStyle()
				scaleX = 0.8f
				scaleY = 0.8f
				x -= 10.uiPX()
				isClickable = false
			}
			radio.setAlignParentRight()
			radio.setCenterInVertical()
		}
	}

	@SuppressLint("SetTextI18n")
	fun setAccountInfo(name: String, authorization: String) {
		info.title.text = "Account Name: $name"
		val wholeString = "Authorization: $authorization"
		info.subtitle.text = CustomTargetTextStyle(
			authorization,
			wholeString,
			GrayScale.gray,
			12.uiPX(),
			false,
			false
		)
	}

	fun getName(): String {
		return info.title.text.toString().substringAfterLast(" ")
	}

	fun setRadioStatus(status: Boolean) {
		radio.isChecked = status
	}
}