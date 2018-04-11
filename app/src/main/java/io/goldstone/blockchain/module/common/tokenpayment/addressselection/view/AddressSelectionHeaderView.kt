package io.goldstone.blockchain.module.common.tokenpayment.addressselection.view

import android.content.Context
import android.graphics.PorterDuff
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.EditText
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.orZero
import com.blinnnk.honey.setCursorColor
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.GradientType
import io.goldstone.blockchain.common.component.GradientView
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.Spectrum
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
      .apply { setStyle(GradientType.DarkGreenYellow, 80.uiPX()) }
      .into(this)

    addressInput
      .apply {
        layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
        hint = "Enter an wallet address or select a contacts below"
        textSize = 5.uiPX().toFloat()
        textColor = Spectrum.white
        hintTextColor = Spectrum.opacity5White
        layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
        leftPadding = 20.uiPX()
        rightPadding = 20.uiPX()
        setCursorColor(Spectrum.blue)
        backgroundTintMode = PorterDuff.Mode.CLEAR
        gravity = Gravity.CENTER
        typeface = GoldStoneFont.medium(context)
      }
      .into(this)
  }

  fun setFocusStatus() {
    addressInput.hintTextColor = Spectrum.opacity1White
    addressInput.requestFocus()
  }

  fun getInputStatus(hold: (hasInput: Boolean, address: String?) -> Unit) {
    addressInput.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(char: Editable?) {
        hold(char?.length.orZero() > 0, char?.toString())
      }
      override fun beforeTextChanged(char: CharSequence?, start: Int, count: Int, after: Int) { }
      override fun onTextChanged(char: CharSequence?, start: Int, before: Int, count: Int) { }
    })
  }

}