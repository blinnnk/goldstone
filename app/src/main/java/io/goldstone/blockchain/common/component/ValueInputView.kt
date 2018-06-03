package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.app.Activity
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
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import com.blinnnk.honey.setCursorColor
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.AlertText
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.crypto.formatCurrency
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.verticalLayout

/**
 * @date 2018/5/15 10:22 PM
 * @author KaySaith
 */
@SuppressLint("SetTextI18n")
open class ValueInputView(context: Context) : RelativeLayout(context) {
	
	protected val gradientView by lazy { GradientView(context) }
	protected val description by lazy { TextView(context) }
	protected val valueInput by lazy { EditText(context) }
	private val priceInfo by lazy { TextView(context) }
	protected val gradientViewHeight = 170.uiPX()
	
	init {
		this.addView(gradientView.apply {
			layoutParams = LinearLayout.LayoutParams(matchParent, gradientViewHeight)
			setStyle(GradientType.DarkGreenYellow, gradientViewHeight)
		})
		
		verticalLayout {
			layoutParams = RelativeLayout.LayoutParams(matchParent, gradientViewHeight)
			gravity = Gravity.CENTER
			description.apply {
				layoutParams = LinearLayout.LayoutParams(matchParent, 20.uiPX()).apply {
					topMargin = 15.uiPX()
				}
				textColor = Spectrum.opacity5White
				textSize = fontSize(15)
				typeface = GoldStoneFont.medium(context)
				gravity = Gravity.CENTER
			}.into(this)
			
			valueInput.apply {
				layoutParams = LinearLayout.LayoutParams(matchParent, 100.uiPX())
				hint = "0.0"
				hintTextColor = Spectrum.opacity5White
				textColor = Spectrum.white
				textSize = fontSize(48)
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
				text = "≈ 0.0 (${GoldStoneApp.getCurrencyCode()})"
				textColor = Spectrum.opacity5White
				textSize = fontSize(12)
				typeface = GoldStoneFont.medium(context)
				gravity = Gravity.CENTER
			}.into(this)
		}
	}
	
	fun updateCurrencyValue(value: Double) {
		if (valueInput.text?.toString()?.toDoubleOrNull().isNull() && valueInput.text.isNotEmpty()) {
			context?.alert(AlertText.transferUnvalidInputFromat)
			valueInput.text.clear()
			return
		}
		val count = if (valueInput.text.isEmpty()) 0.0 else valueInput.text.toString().toDouble()
		priceInfo.text =
			"≈ ${(value * count).orElse(0.0).formatCurrency()} (${GoldStoneApp.getCurrencyCode()})"
	}
	
	fun setInputValue(count: Double) {
		valueInput.setText(count.toString())
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
			
			override fun beforeTextChanged(
				s: CharSequence?,
				start: Int,
				count: Int,
				after: Int
			) {
			}
			
			override fun onTextChanged(
				s: CharSequence?,
				start: Int,
				before: Int,
				count: Int
			) {
			}
		})
	}
	
	fun setHeaderSymbol(symbol: String) {
		description.text = "Send $symbol Count"
	}
	
	fun getValue(): String {
		return valueInput.text.toString()
	}
	
	fun setFoucs() {
		valueInput.requestFocus()
		(context as? Activity)?.apply { SoftKeyboard.show(this, valueInput) }
	}
}
