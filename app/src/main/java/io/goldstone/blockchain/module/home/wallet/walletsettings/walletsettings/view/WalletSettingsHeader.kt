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
import io.goldstone.blockchain.common.utils.glideImage
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

  private val avatarImage = ImageView(context)
  private val walletInfo = TwoLineTitles(context)
  private val copyButton = TextView(context)

  init {
    orientation = VERTICAL
    layoutParams = LinearLayout.LayoutParams(ScreenSize.Width - 80.uiPX(), matchParent).apply {
      leftMargin = 40.uiPX()
      topMargin = 25.uiPX()
    }

    gravity = Gravity.CENTER_HORIZONTAL

    avatarImage
      .apply {
        glideImage("http://img1.touxiang.cn/uploads/20121212/12-055756_227.png")
        layoutParams = LinearLayout.LayoutParams(70.uiPX(), 70.uiPX())
        addCorner(35.uiPX(), GrayScale.lightGray)
      }
      .into(this)

    walletInfo
      .apply {
        setBlackTitles()
        title.text = "KaySaith"
        subtitle.text = "0x98s9d789s9x8d7567567s756d57s7898as8a7658x87d678s876d678s87c68s"
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
          context.clickToCopy(walletInfo.subtitle.text.toString())
        }
      }
      .into(this)

    walletInfo.setMargins<LinearLayout.LayoutParams> {
      topMargin = 10.uiPX()
    }
    copyButton.setMargins<LinearLayout.LayoutParams> { topMargin = 5.uiPX() }

  }

}