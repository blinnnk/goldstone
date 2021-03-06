package io.goldstone.blinnnk.common.component.title

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewManager
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.CustomTargetTextStyle
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.sdk27.coroutines.onClick


/**
 * @author KaySaith
 * @date  2018/09/11
 */

class SessionTitleView(context: Context) : RelativeLayout(context) {

	private val titleView = TextView(context).apply {
		textSize = fontSize(12)
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
			setPadding(0, 15.uiPX(), 5.uiPX(), 10.uiPX())
			gravity = Gravity.END
		}
	}

	init {
		leftPadding = 20.uiPX()
		rightPadding = 10.uiPX()
		addView(titleView)
		addView(subtitle)
	}

	fun setTitle(text: String, color: Int = GrayScale.midGray): SessionTitleView {
		titleView.text = text
		titleView.textColor = color
		return this
	}

	fun setSubtitle(
		specificText: String,
		wholeText: String,
		specificColor: Int,
		color: Int = GrayScale.midGray,
		subtitleClickEvent: () -> Unit = {}
	): SessionTitleView {
		subtitle.visibility = View.VISIBLE
		subtitle.textColor = color
		subtitle.text = CustomTargetTextStyle(specificText, wholeText, specificColor, 11.uiPX(), false, false)
		subtitle.onClick {
			subtitleClickEvent()
			subtitle.preventDuplicateClicks()
		}
		return this
	}
}

fun ViewManager.sessionTitle(title: String) =
	ankoView(
		{ SessionTitleView(it) },
		0
	) {
		setTitle(title)
	}

inline fun ViewManager.sessionTitle(init: SessionTitleView.() -> Unit) =
	ankoView({ SessionTitleView(it) }, 0, init)