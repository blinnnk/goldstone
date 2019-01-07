package io.goldstone.blinnnk.module.common.tokenpayment.addressselection.view

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.EditText
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.orZero
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blinnnk.common.component.GradientType
import io.goldstone.blinnnk.common.component.GradientView
import io.goldstone.blinnnk.common.language.EmptyText
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.*

/**
 * @date 28/03/2018 9:44 AM
 * @author KaySaith
 */
class AddressSelectionHeaderView(context: Context) : RelativeLayout(context) {

	private val addressInput = EditText(context)
	private val gradientView = GradientView(context)

	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, 80.uiPX())
		gradientView
			.apply { setStyle(GradientType.Tree, 150.uiPX()) }
			.into(this)

		addressInput
			.apply {
				layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
				hint = EmptyText.transferToAddress
				textSize = fontSize(15)
				textColor = Spectrum.white
				layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
				leftPadding = 20.uiPX()
				rightPadding = 20.uiPX()
				backgroundTintMode = PorterDuff.Mode.CLEAR
				gravity = Gravity.CENTER
				typeface = GoldStoneFont.medium(context)
			}
			.into(this)
	}

	fun setFocusStatus() {
		addressInput.hintTextColor = Spectrum.opacity3White
		addressInput.requestFocus()
		(context as? Activity)?.let { SoftKeyboard.show(it, addressInput) }
	}

	fun getInputStatus(hold: (hasInput: Boolean, address: String?) -> Unit) {
		addressInput.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(char: Editable?) {
				hold(char?.length.orZero() > 0, char?.toString())
			}

			override fun beforeTextChanged(char: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(char: CharSequence?, start: Int, before: Int, count: Int) {}
		})
	}
}