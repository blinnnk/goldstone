package io.goldstone.blockchain.common.component

import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent


/**
 * @author KaySaith
 * @date  2018/09/11
 */

class SessionTitleView(context: Context) : TextView(context) {
	init {
		textSize = fontSize(12)
		textColor = GrayScale.midGray
		typeface = GoldStoneFont.black(context)
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
		setPadding(0, 15.uiPX(), 0, 10.uiPX())
	}

	fun setTitle(text: String): SessionTitleView {
		this.text = text
		return this
	}
}