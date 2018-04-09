package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.WalletSettingsText
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.textColor

/**
 * @date 25/03/2018 9:44 PM
 * @author KaySaith
 */

class WalletSettingsHeader(context: Context) : LinearLayout(context) {

  val walletInfo = TwoLineTitles(context)
  val avatarImage = ImageView(context)

  private val copyButton = TextView(context)

  init {
    orientation = VERTICAL
    layoutParams = LinearLayout.LayoutParams(ScreenSize.Width - 100.uiPX(), matchParent).apply {
      leftMargin = 50.uiPX()
      topMargin = 25.uiPX()
    }

    gravity = Gravity.CENTER_HORIZONTAL

    avatarImage
      .apply {
        layoutParams = LinearLayout.LayoutParams(70.uiPX(), 70.uiPX())
        addCorner(35.uiPX(), GrayScale.lightGray)
      }
      .into(this)

    walletInfo
      .apply {
        setBlackTitles()
        isCenter = true
        subtitle.gravity = Gravity.CENTER_HORIZONTAL
      }
      .into(this)

    copyButton
      .apply {
        text = WalletSettingsText.copy
        textSize = 4.uiPX().toFloat()
        textColor = Spectrum.blue
        typeface = GoldStoneFont.book(context)
        gravity = Gravity.CENTER_HORIZONTAL
        addTouchRippleAnimation(Color.TRANSPARENT, Spectrum.green, RippleMode.Round)
        onClick {
          context.clickToCopy(walletInfo.getSubtitleValue())
        }
      }
      .into(this)

    walletInfo.setMargins<LinearLayout.LayoutParams> {
      topMargin = 10.uiPX()
    }
    copyButton.setMargins<LinearLayout.LayoutParams> { topMargin = 5.uiPX() }

  }

}