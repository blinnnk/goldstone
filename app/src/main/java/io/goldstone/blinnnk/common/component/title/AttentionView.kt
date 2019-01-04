package io.goldstone.blinnnk.common.component.title

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.language.EOSAccountText.activeByContractMethod
import io.goldstone.blinnnk.common.sharedpreference.SharedValue
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.CornerSize
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.common.value.Spectrum
import org.jetbrains.anko.padding
import org.jetbrains.anko.wrapContent

/**
 * @date 22/03/2018 1:48 PM
 * @author KaySaith
 */

class AttentionView(context: Context) : TextView(context) {
	init {
		typeface = GoldStoneFont.medium(context)
		gravity = Gravity.CENTER_VERTICAL

		layoutParams =
			LinearLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
		padding = 20.uiPX()

		setBackgroundColor(Spectrum.green)
	}

	override fun setBackgroundColor(color: Int) {
		addCorner(CornerSize.small.toInt(), color)
	}

	fun isSmartContractRegister(): AttentionView {
		text = activeByContractMethod(if (SharedValue.isTestEnvironment()) "goldstonenew" else "signupeoseos")
		setBackgroundColor(Spectrum.lightRed)
		return this
	}

}