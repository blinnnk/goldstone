package io.goldstone.blockchain.module.home.wallet.walletdetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.CircleButton
import io.goldstone.blockchain.common.component.SliderHeader
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.WalletText
import io.goldstone.blockchain.crypto.formatCurrency
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable

/**
 * @date 24/03/2018 12:50 AM
 * @author KaySaith
 */
@SuppressLint("SetTextI18n")
class WalletSlideHeader(context: Context) : SliderHeader(context) {

  val historyButton by lazy { CircleButton(context) }
  val notifyButton by lazy { CircleButton(context) }
  private val balance by lazy { TwoLineTitles(context) }

  init {

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
          typeface = GoldStoneFont.heavy(context)
          textSize = 6.uiPX().toFloat()
        }
        subtitle.apply {
          text = WalletText.totalAssets + " " + GoldStoneApp.currencyCode
          textSize = 4.uiPX().toFloat()
          typeface = GoldStoneFont.medium(context)
          y -= 6.uiPX()
        }

        isCenter = true
        visibility = View.GONE
        y -= 7.uiPX()
      }
      .into(this)
  }

  override fun onHeaderShowedStyle() {
    super.onHeaderShowedStyle()
    historyButton.setUnTransparent()
    notifyButton.setUnTransparent()

    balance.apply {
      setCenterInParent()
      visibility = View.VISIBLE
    }

    setBalanceValue(WalletTable.current.balance?.formatCurrency().orEmpty())
  }

  override fun onHeaderHidesStyle() {
    super.onHeaderHidesStyle()
    historyButton.setDefaultStyle()
    notifyButton.setDefaultStyle()
    balance.visibility = View.GONE
  }

  private fun setBalanceValue(value: String) {
    balance.title.text = value
  }

}