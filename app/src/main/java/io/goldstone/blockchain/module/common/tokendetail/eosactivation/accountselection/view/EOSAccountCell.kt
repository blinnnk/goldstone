package io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RelativeLayout
import com.blinnnk.component.HoneyRadioButton
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.GrayCardView
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.matchParent

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

	private val radio by lazy {
		HoneyRadioButton(context).apply {
			setColorStyle(GrayScale.midGray, Spectrum.blue)
			scaleX = 0.8f
			scaleY = 0.8f
			x -= 10.uiPX()
			isClickable = false
		}
	}

	init {
		setCardParams(ScreenSize.widthWithPadding, 70.uiPX())
		addView(cellContainer)
		cellContainer.addView(info)
		info.setCenterInVertical()
		cellContainer.addView(radio)
		radio.setAlignParentRight()
		radio.setCenterInVertical()
	}

	@SuppressLint("SetTextI18n")
	fun setAccountInfo(name: String, authorization: String) {
		info.title.text = "Account Name: $name"
		info.subtitle.text = "Authorization: $authorization"
	}

	fun getName(): String {
		return info.title.text.toString().substringAfterLast(" ")
	}

	fun setRadioStatus(status: Boolean) {
		radio.isChecked = status
	}
}