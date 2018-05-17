package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.view

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.AnimationDuration
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.keyboardHeightListener
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor

/**
 * @date 2018/5/16 11:30 AM
 * @author KaySaith
 */

class MemoInputView(context: Context) : RelativeLayout(context) {

	private val inputView by lazy { EditText(context) }
	private val confirmButton by lazy {
		Button(context).apply {
			backgroundColor = Spectrum.blue
			textSize = fontSize(14)
			textColor = Spectrum.white
			layoutParams = RelativeLayout.LayoutParams(matchParent, buttonHeight)
			text = CommonText.confirm
		}
	}
	private val buttonHeight = 50.uiPX()
	private var viewHeight = 0
	private var keyboardHeight = 0

	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, ScreenSize.Height)
		backgroundColor = Spectrum.white
		inputView.apply {
			textSize = fontSize(18)
			textColor = GrayScale.black
			typeface = GoldStoneFont.heavy(context)
			gravity = Gravity.CENTER
			backgroundTintMode = PorterDuff.Mode.CLEAR
			layoutParams =
				RelativeLayout.LayoutParams(
					io.goldstone.blockchain.common.value.ScreenSize.widthWithPadding, ScreenSize.Height
				)
		}.into(this)
		inputView.setCenterInHorizontal()
		AnimationDuration.Default timeUpThen {
			inputView.requestFocus()
			SoftKeyboard.show(context as Activity, inputView)
		}

		confirmButton.into(this)

		val barHeight = if ((context as Activity).navigationBarIsHidden()) {
			HomeSize.tabBarHeight
		} else {
			0
		}

		keyboardHeightListener {
			if (keyboardHeight != it) {
				viewHeight = ScreenSize.Height - it - ScreenSize.statusBarHeight -
					if (context.navigationBarIsHidden()) 0 else HomeSize.tabBarHeight - 8.uiPX()
				inputView.layoutParams.height = viewHeight
				inputView.requestLayout()
				confirmButton.y = viewHeight - buttonHeight * 1f - barHeight - 5.uiPX()
				keyboardHeight = it
			}
		}
	}

	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		SoftKeyboard.hide(context as Activity)
	}

	fun getMemoContent(): String {
		return inputView.text.toString()
	}

	fun updateConfirmButtonEvent(hold: (Button) -> Unit) {
		hold(confirmButton)
	}
}

