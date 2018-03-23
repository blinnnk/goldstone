package io.goldstone.blockchain.module.home.wallet.wallet.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.RoundBorderButton
import io.goldstone.blockchain.common.component.RoundButtonWithIcon
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.SymbolText
import io.goldstone.blockchain.common.value.WalletText
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

/**
 * @date 23/03/2018 4:21 PM
 * @author KaySaith
 */

class WalletDetailHeaderView(context: Context) : RelativeLayout(context) {

  private val balanceTitle by lazy { TextView(context) }
  private val manageButton by lazy { RoundButtonWithIcon(context) }
  private val addTokenButton by lazy { RoundBorderButton(context) }
  private val currentAccount by lazy { CurrentAccountView(context) }

  private val sectionHeaderHeight = 50.uiPX()

  init {

    setWillNotDraw(false)

    layoutParams = RelativeLayout.LayoutParams(matchParent, 365.uiPX())

    currentAccount.into(this)
    currentAccount.apply {
      setCenterInHorizontal()
      y += 30.uiPX()
    }

    verticalLayout {
      balanceTitle
        .apply {
          textSize = 12.uiPX().toFloat()
          typeface = GoldStoneFont.black(context)
          textColor = Spectrum.white
          text = "192456.82"
        }
        .into(this)

      textView(WalletText.totalAssets + SymbolText.usd) {
        textSize = 4.uiPX().toFloat()
        typeface = GoldStoneFont.light(context)
        textColor = Spectrum.opacity5White
        gravity = Gravity.CENTER_HORIZONTAL
      }.lparams(matchParent, matchParent)

    }.apply {
      setCenterInParent()
    }

    manageButton
      .apply {
        text = (WalletText.manage + " (5)").toUpperCase()
        y -= sectionHeaderHeight + 25.uiPX()
      }
      .into(this)
    manageButton.setCenterInHorizontal()
    manageButton.setAlignParentBottom()

    textView {
      text = WalletText.section
      typeface = GoldStoneFont.heavy(context)
      textColor = Spectrum.white
      textSize = 5.uiPX().toFloat()
      y -= 20.uiPX()
    }.apply {
      setAlignParentBottom()
      x += PaddingSize.device
      y += 10.uiPX()
    }

    addTokenButton
      .apply {
        text = WalletText.addToken
        layoutParams = LinearLayout.LayoutParams(125.uiPX(), 24.uiPX())
        x -= PaddingSize.device
        y -= 10.uiPX()
      }
      .into(this)

    addTokenButton.apply {
      setAlignParentRight()
      setAlignParentBottom()
    }

  }

  private val paint = Paint().apply {
    isAntiAlias = true
    style = Paint.Style.FILL
    color = Spectrum.opacity2White
  }

  @SuppressLint("DrawAllocation")
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    canvas?.drawLine(
      PaddingSize.device.toFloat(),
      height - sectionHeaderHeight.toFloat(),
      width - PaddingSize.device.toFloat(),
      height - sectionHeaderHeight.toFloat(),
      paint
    )
  }

}