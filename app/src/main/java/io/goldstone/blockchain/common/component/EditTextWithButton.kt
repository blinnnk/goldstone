package io.goldstone.blockchain.common.component

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.view.Gravity
import android.view.KeyEvent
import android.view.View.OnKeyListener
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.honey.setCursorColor
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.*

/**
 * @date 27/03/2018 12:15 PM
 * @author KaySaith
 */
class EditTextWithButton(context: Context) : RelativeLayout(context) {
	
	val editText = EditText(context)
	private val button by lazy { TextView(context) }
	
	init {
		id = ElementID.searchInput
		editText.apply {
			hint = "search contracts address or token name"
			textSize = fontSize(12)
			textColor = GrayScale.black
			hintTextColor = GrayScale.midGray
			layoutParams = RelativeLayout.LayoutParams(ScreenSize.Width - 100.uiPX(), 35.uiPX())
			x += PaddingSize.device
			singleLine = true
			leftPadding = 20.uiPX()
			setCursorColor(Spectrum.blue)
			backgroundTintMode = PorterDuff.Mode.CLEAR
			addCorner(CornerSize.default.toInt(), GrayScale.whiteGray)
		}.into(this)
		editText.setCenterInVertical()
	}
	
	fun setCancelButton(setClickEvent: () -> Unit = {}) {
		button.apply {
			text = CommonText.cancel
			textColor = GrayScale.midGray
			textSize = fontSize(13)
			typeface = GoldStoneFont.book(context)
			layoutParams = RelativeLayout.LayoutParams(70.uiPX(), matchParent)
			gravity = Gravity.CENTER
		}.click { setClickEvent() }.into(this)
		
		button.apply {
			setAlignParentRight()
			setCenterInVertical()
		}
	}
	
	fun onPressKeyboardEnterButton(action: () -> Unit) {
		editText.setOnKeyListener(OnKeyListener { _, keyCode, event ->
			if (event.action == KeyEvent.ACTION_DOWN) {
				when (keyCode) {
					KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
						action()
						SoftKeyboard.hide(context as Activity)
						return@OnKeyListener true
					}
					
					else -> {
					}
				}
			}
			false
		})
	}
}