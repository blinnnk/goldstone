package io.goldstone.blinnnk.common.component.edittext

import android.content.Context
import android.view.Gravity
import android.view.MotionEvent
import android.widget.EditText
import android.widget.LinearLayout
import com.blinnnk.extension.addCorner
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.CornerSize
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.padding
import org.jetbrains.anko.textColor

/**
 * @date 23/03/2018 2:20 AM
 * @author KaySaith
 */
class WalletEditText(context: Context) : EditText(context) {

	init {
		addCorner(CornerSize.normal.toInt(), GrayScale.whiteGray)
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 90.uiPX())
		padding = 20.uiPX()
		hintTextColor = GrayScale.midGray
		textSize = fontSize(15)
		textColor = GrayScale.black
		typeface = GoldStoneFont.heavy(context)
		gravity = Gravity.START
		// EditText 内部滚动优先父布局
		setOnTouchListener { view, event ->
			view.parent.requestDisallowInterceptTouchEvent(true)
			if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
				view.parent.requestDisallowInterceptTouchEvent(false)
			}
			return@setOnTouchListener false
		}
	}

	fun getContent(): String = text.toString()
}