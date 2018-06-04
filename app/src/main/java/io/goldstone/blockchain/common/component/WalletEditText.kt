package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import com.blinnnk.extension.addCorner
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.padding
import org.jetbrains.anko.textColor

/**
 * @date 23/03/2018 2:20 AM
 * @author KaySaith
 */

class WalletEditText(context: Context) : EditText(context) {

	init {
		addCorner(CornerSize.default.toInt(), GrayScale.whiteGray)
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 100.uiPX()).apply {
			topMargin = 40.uiPX()
			padding = 20.uiPX()
		}
		hintTextColor = GrayScale.midGray
		textSize = fontSize(15)
		textColor = GrayScale.black
		typeface = GoldStoneFont.heavy(context)
		gravity = Gravity.START
	}

}