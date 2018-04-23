package io.goldstone.blockchain.module.common.passcode.view

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.Count
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView

/**
 * @date 23/04/2018 11:06 AM
 * @author KaySaith
 */

class NumberKeyboard(context: Context) : RelativeLayout(context) {

  var checkCode: Runnable? = null

  private val buttonSize = 60.uiPX()
  private val itemSpace = 35.uiPX()
  private val lineSpace = 20.uiPX()
  private val rowCount = 3
  private val totalCount = 11
  private var currentCode: String by observing("") {
    if (currentCode.length <= Count.pinCode) checkCode?.run()
  }

  init {
    val keyboardWidth = (buttonSize + itemSpace) * rowCount - itemSpace
    val keyboardHeight =
      ((buttonSize + lineSpace) * (Math.ceil(totalCount / rowCount.toDouble())) - lineSpace).toInt()
    layoutParams = LinearLayout.LayoutParams(keyboardWidth, keyboardHeight)
    (0 until totalCount).forEach {
      textView {
        text = if (it == 9) "0" else if (it == 10) CommonText.cancel else (it + 1).toString()
        textSize = if (it == 10) 4.uiPX().toFloat() else 10.uiPX().toFloat()
        typeface = GoldStoneFont.heavy(context)
        textColor = Spectrum.white
        gravity = Gravity.CENTER
        layoutParams = LinearLayout.LayoutParams(buttonSize, buttonSize)
        x = (it % rowCount * (buttonSize + itemSpace)).toFloat() +
          if (it >= 9) buttonSize + itemSpace else 0
        y = (Math.floor(it / rowCount.toDouble()) * (buttonSize + lineSpace)).toFloat()
        addTouchRippleAnimation(
          Spectrum.opacity1White, Spectrum.yellow, RippleMode.Square, buttonSize.toFloat()
        )
        // 输入密码
        // 第 `10` 位是 `cancel` 键
        if (it < 10) {
          onClick {
            // 超过 `4` 位就不在记录新输入的密码
            if (currentCode.length < Count.pinCode) {
              currentCode += text
            }
          }
        }
        // 删除已经输入的部分
        if (it == 10) {
          onClick {
            currentCode.apply { if (isNotEmpty()) currentCode = substring(0, lastIndex) }
          }
        }
      }
    }
  }

  fun getEnteredCode() = currentCode
  fun resetCode() {
    currentCode = ""
  }

}