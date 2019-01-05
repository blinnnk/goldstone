package io.goldstone.blinnnk.module.common.tokenpayment.paymentdetail.view

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import com.blinnnk.extension.centerInHorizontal
import com.blinnnk.extension.into
import com.blinnnk.extension.keyboardHeightListener
import com.blinnnk.extension.timeUpThen
import com.blinnnk.uikit.AnimationDuration
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.*
import io.goldstone.blinnnk.crypto.eos.EOSUtils
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
			y = ScreenSize.fullHeight - buttonHeight * 1f
		}
	}
	private val buttonHeight = 50.uiPX()
	private var viewHeight = 0
	private var keyboardHeight = 0

	init {
		isClickable = true
		y = HomeSize.headerHeight.toFloat()
		layoutParams = RelativeLayout.LayoutParams(matchParent, ScreenSize.heightWithOutHeader)
		backgroundColor = Spectrum.white
		inputView.apply {
			textSize = fontSize(18)
			textColor = GrayScale.black
			typeface = GoldStoneFont.heavy(context)
			gravity = Gravity.CENTER
			backgroundTintMode = PorterDuff.Mode.CLEAR
			layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, matchParent)
		}.into(this)
		inputView.centerInHorizontal()
		AnimationDuration.Default timeUpThen {
			inputView.requestFocus()
			(context as? Activity)?.let { SoftKeyboard.show(it, inputView) }
		}

		confirmButton.into(this)

		keyboardHeightListener {
			if (keyboardHeight != it) {
				viewHeight = ScreenSize.heightWithOutHeader - it
				inputView.layoutParams.height = viewHeight
				inputView.requestLayout()
				confirmButton.y = viewHeight - buttonHeight * 1f
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

	fun setMemoContent(content: String) {
		inputView.setText(content)
	}

	fun updateConfirmButtonEvent(hold: (Button) -> Unit) {
		hold(confirmButton)
	}

	fun isValidMemoByChain(isEOSChain: Boolean): Boolean {
		return if (isEOSChain) EOSUtils.isValidMemoSize(inputView.text.toString())
		else true
	}
}

