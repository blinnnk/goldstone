package io.goldstone.blockchain.module.common.tokenpayment.deposit.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.orElse
import com.blinnnk.honey.setCursorColor
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.component.GradientType
import io.goldstone.blockchain.common.component.GradientView
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.crypto.formatCurrency
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.verticalLayout

/**
 * @date 2018/5/8 11:29 AM
 * @author KaySaith
 */

@SuppressLint("SetTextI18n")
class DepositInputView(context: Context) : RelativeLayout(context) {

	private val gradientView by lazy { GradientView(context) }
	private val description by lazy { TextView(context) }
	private val valueInput by lazy { EditText(context) }
	private val priceInfo by lazy { TextView(context) }
	private val gradientViewHeight = 170.uiPX()

	init {

		layoutParams = LinearLayout.LayoutParams(matchParent, 260.uiPX())

		gradientView.apply {
			layoutParams = LinearLayout.LayoutParams(matchParent, gradientViewHeight)
			setStyle(GradientType.Blue, gradientViewHeight)
		}.into(this)

		verticalLayout {
			layoutParams = RelativeLayout.LayoutParams(matchParent, gradientViewHeight)
			gravity = Gravity.CENTER
			description.apply {
				layoutParams = LinearLayout.LayoutParams(matchParent, 20.uiPX()).apply {
					topMargin = 15.uiPX()
				}
				textColor = Spectrum.opacity5White
				textSize = 5.uiPX().toFloat()
				typeface = GoldStoneFont.medium(context)
				gravity = Gravity.CENTER
			}.into(this)

			valueInput.apply {
				layoutParams = LinearLayout.LayoutParams(matchParent, 100.uiPX())
				hint = "0.0"
				hintTextColor = Spectrum.opacity5White
				textColor = Spectrum.white
				textSize = 16.uiPX().toFloat()
				typeface = GoldStoneFont.heavy(context)
				gravity = Gravity.CENTER
				inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
				setCursorColor(Spectrum.blue)
				backgroundTintMode = PorterDuff.Mode.CLEAR
				y -= 3.uiPX()
			}.into(this)

			priceInfo.apply {
				layoutParams = LinearLayout.LayoutParams(matchParent, 20.uiPX()).apply {
					topMargin = -(18.uiPX())
				}
				text = "≈ 0.0 (${GoldStoneApp.currencyCode})"
				textColor = Spectrum.opacity5White
				textSize = 4.uiPX().toFloat()
				typeface = GoldStoneFont.medium(context)
				gravity = Gravity.CENTER
			}.into(this)
		}
	}

	fun setInputFocus() {
		valueInput.hintTextColor = Spectrum.opacity1White
		valueInput.requestFocus()
	}

	fun updateCurrencyValue(value: Double?) {
		priceInfo.text = "≈ ${value.orElse(0.0).formatCurrency()} (${GoldStoneApp.currencyCode})"
	}

	fun inputTextListener(hold: (String) -> Unit) {
		valueInput.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(text: Editable?) {
				// 文字自适应宽度调整字号大小
				if (text?.length.orElse(0) > 8) {
					valueInput.textSize = (16 - Math.ceil(text!!.length / 3.0).toInt()).uiPX().toFloat()
				} else {
					valueInput.textSize = 16.uiPX().toFloat()
				}
				text.apply { hold(toString()) }
			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
		})
	}

	fun setHeaderSymbol(symbol: String) {
		description.text = "Recieve $symbol Count"
	}

}