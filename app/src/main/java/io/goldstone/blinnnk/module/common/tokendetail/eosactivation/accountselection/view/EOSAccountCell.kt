package io.goldstone.blinnnk.module.common.tokendetail.eosactivation.accountselection.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.RadioButton
import com.blinnnk.extension.CustomTargetTextStyle
import com.blinnnk.extension.alignParentRight
import com.blinnnk.extension.centerInVertical
import com.blinnnk.extension.isDefaultStyle
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.base.view.GrayCardView
import io.goldstone.blinnnk.common.component.title.TwoLineTitles
import io.goldstone.blinnnk.common.language.EOSAccountText
import io.goldstone.blinnnk.common.language.ImportWalletText
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.fontSize
import io.goldstone.blinnnk.crypto.eos.accountregister.EOSActor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.radioButton
import org.jetbrains.anko.relativeLayout

/**
 * @author KaySaith
 * @date  2018/09/12
 */
class EOSAccountCell(context: Context) : GrayCardView(context) {

	private val info = TwoLineTitles(context).apply {
		setBlackTitles(fontSize(14), 3.uiPX())
	}
	private lateinit var radio: RadioButton

	init {
		layoutParams = ViewGroup.LayoutParams(ScreenSize.widthWithPadding, 80.uiPX())
		setContentPadding(15.uiPX(), 10.uiPX(), 15.uiPX(), 10.uiPX())
		relativeLayout {
			lparams(matchParent, matchParent)
			addView(info)
			info.centerInVertical()
			radio = radioButton {
				isDefaultStyle(Spectrum.blue)
				isClickable = false
			}
			radio.alignParentRight()
			radio.centerInVertical()
		}
	}

	@SuppressLint("SetTextI18n")
	fun setAccountInfo(name: String, authorization: String, isSinglePermission: Boolean) {
		info.title.text = "${ImportWalletText.eosAccountName}: $name"
		val permission =
			if (!isSinglePermission) EOSActor.Owner.value + "/" + EOSActor.Active.value
			else authorization
		val wholeString = "${EOSAccountText.authorization}: $permission"
		info.subtitle.text = CustomTargetTextStyle(
			permission,
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