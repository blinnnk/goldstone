package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import com.blinnnk.extension.addCorner
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.Grayscale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.padding
import org.jetbrains.anko.textColor

/**
 * @date 23/03/2018 2:20 AM
 * @author KaySaith
 */

class WalletEditText(context: Context) : EditText(context) {

  init {
    addCorner(CornerSize.default.toInt(), Grayscale.whiteGray)
    layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 120.uiPX()).apply {
      leftMargin = PaddingSize.device
      topMargin = 40.uiPX()
      padding = 20.uiPX()
    }
    hint = "mnemonics split with space"
    hintTextColor = Grayscale.midGray
    textSize = 5.uiPX().toFloat()
    textColor = Grayscale.black
    typeface = GoldStoneFont.heavy(context)
    gravity = Gravity.START
  }

}