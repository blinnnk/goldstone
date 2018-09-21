package io.goldstone.blockchain.common.component.title

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.CustomTargetTextStyle
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/09/11
 */

class SessionTitleView(context: Context) : RelativeLayout(context) {

	private val titleView = TextView(context).apply {
		textSize = fontSize(12)
		textColor = GrayScale.midGray
		typeface = GoldStoneFont.black(context)
		layoutParams = RelativeLayout.LayoutParams(matchParent, wrapContent)
		setPadding(0, 15.uiPX(), 0, 10.uiPX())
	}

	private val subtitle by lazy {
		TextView(context).apply {
			visibility = View.GONE
			textSize = fontSize(11)
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.black(context)
			layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
			setPadding(0, 14.uiPX(), 0, 10.uiPX())
			gravity = Gravity.END
		}
	}

	init {
		leftPadding = 20.uiPX()
		rightPadding = 20.uiPX()
		addView(titleView)
		addView(subtitle)
	}

	fun setTitle(text: String): SessionTitleView {
		titleView.text = text
		return this
	}

	fun setSubtitle(specificText: String, wholeText: String, specificColor: Int) {
		subtitle.visibility = View.VISIBLE
		subtitle.text = CustomTargetTextStyle(specificText, wholeText, specificColor, 11.uiPX(), false, false)
	}
}