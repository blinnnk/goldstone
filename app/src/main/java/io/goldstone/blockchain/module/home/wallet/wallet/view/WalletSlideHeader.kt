package io.goldstone.blockchain.module.home.wallet.wallet.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.RelativeLayout
import com.blinnnk.animation.updateColorAnimation
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.CircleButton
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.utils.GoldStoneFont
import com.blinnnk.extension.into
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.SymbolText
import io.goldstone.blockchain.common.value.WalletText
import org.jetbrains.anko.matchParent

/**
 * @date 24/03/2018 12:50 AM
 * @author KaySaith
 */
@SuppressLint("SetTextI18n")
class WalletSlideHeader(context: Context) : RelativeLayout(context) {

  val historyButton by lazy { CircleButton(context) }
  val notifyButton by lazy { CircleButton(context) }
  private val balance by lazy { TwoLineTitles(context) }

  init {
    layoutParams = RelativeLayout.LayoutParams(matchParent, 90.uiPX())

    historyButton
      .apply {
        title = "history"
        src = R.drawable.history_icon
        x += PaddingSize.device
        y += 15.uiPX()
      }
      .into(this)

    historyButton.apply {
      setCenterInVertical()
    }

    notifyButton
      .apply {
        title = "notify"
        src = R.drawable.notifications_icon
        x -= PaddingSize.device
        y += 15.uiPX()
      }
      .into(this)

    notifyButton.apply {
      setCenterInVertical()
      setAlignParentRight()
    }

    balance
      .apply {

        title.apply {
          text = "192456.82"
          typeface = GoldStoneFont.black(context)
          textSize = 7.uiPX().toFloat()
        }

        subtitle.apply {
          text = WalletText.totalAssets + SymbolText.usd
          textSize = 4.uiPX().toFloat()
          typeface = GoldStoneFont.medium(context)
          y -= 3.uiPX()
        }

        isCenter = true
        visibility = View.GONE
        y += 10.uiPX()
      }
      .into(this)
  }

  fun onHeaderShowedStyle() {
    updateColorAnimation(Color.TRANSPARENT, Spectrum.green)
    historyButton.setUnTransparent()
    notifyButton.setUnTransparent()

    balance.apply {
      setCenterInParent()
      visibility = View.VISIBLE
    }
  }

  fun onHeaderHidesStyle() {
    updateColorAnimation(Spectrum.green, Color.TRANSPARENT)
    historyButton.setDefaultStyle()
    notifyButton.setDefaultStyle()
    balance.visibility = View.GONE
  }

}