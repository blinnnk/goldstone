package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baseInfocell.BaseValueCell
import io.goldstone.blockchain.common.component.RoundIcon
import io.goldstone.blockchain.common.component.UnlimitedAvatar
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.WalletText
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.model.WalletListModel

@SuppressLint("SetTextI18n")
/**
 * @date 24/03/2018 8:58 PM
 * @author KaySaith
 * @rewriteDate 31/07/2018 17:25 PM
 * @rewriter wcx
 * @description 修复获取头像锯齿bug
 */
class WalletListCell(context: Context) : BaseValueCell(context) {

  var model: WalletListModel by observing(WalletListModel()) {
    info.apply {
      title.text = CryptoUtils.scaleTo16(model.addressName)
      subtitle.text = if (model.subtitle.equals(WalletText.multiChainWallet, true)) {
        model.subtitle.scaleTo(24)
      } else {
        model.subtitle.scaleTo(18)
      }
    }

    count?.apply {
      title.text = model.count.formatCurrency()
      subtitle.text = WalletText.totalAssets + " (${Config.getCurrencyCode()})"
    }

    icon.apply {
      UnlimitedAvatar.width = layoutParams.width
      UnlimitedAvatar.height = layoutParams.height
      glideImage(UnlimitedAvatar(
        model.avatar,
        context
      ).generateImage())
    }

    model.isWatchOnly isTrue {
      if (signalIcon.isNull()) {
        signalIcon = RoundIcon(context).apply {
          src = R.drawable.watch_only_icon
          iconColor = Spectrum.darkBlue
          iconSize = 20.uiPX()
          y += 12.uiPX()
        }
        signalIcon?.into(this)
      }
    }

    model.isUsing isTrue {
      if (currentIcon.isNull()) {
        currentIcon = RoundIcon(context).apply {
          src = R.drawable.current_icon
          iconColor = Spectrum.green
          iconSize = 20.uiPX()
          y += 45.uiPX()
          x += 32.uiPX()
        }
        currentIcon?.into(this)
      }
    }
    // 圆角 `icon`
    icon.addCorner(icon.layoutParams.height / 2, GrayScale.midGray)
  }
  private var signalIcon: RoundIcon? = null
  private var currentIcon: RoundIcon? = null

  init {
    setGrayStyle()
    setValueStyle()
  }
}