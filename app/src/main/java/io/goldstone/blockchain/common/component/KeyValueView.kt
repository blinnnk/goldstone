package io.goldstone.blockchain.common.component

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.padding
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent


/**
 * @author KaySaith
 * @date  2018/09/25
 */
class KeyValueView(context: Context) : TextView(context) {
	init {
		addCorner(CornerSize.small.toInt(), GrayScale.whiteGray)
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
		maxLines = 8
		movementMethod = ScrollingMovementMethod()
		padding = 20.uiPX()
		gravity = Gravity.CENTER_VERTICAL
		textSize = fontSize(16)
		textColor = GrayScale.black
		typeface = GoldStoneFont.heavy(context)
	}

	fun getContent(): String {
		return text.toString()
	}
}