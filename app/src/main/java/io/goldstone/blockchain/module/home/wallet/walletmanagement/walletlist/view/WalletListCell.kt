package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.view

import android.annotation.SuppressLint
import android.content.Context
import com.blinnnk.extension.into
import com.blinnnk.extension.isTrue
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baseInfocell.BaseValueCell
import io.goldstone.blockchain.common.component.RoundIcon
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.WalletText
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.crypto.formatCurrency
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.model.WalletListModel

@SuppressLint("SetTextI18n")
/**
 * @date 24/03/2018 8:58 PM
 * @author KaySaith
 */

class WalletListCell(context: Context) : BaseValueCell(context) {

  var model: WalletListModel by observing(WalletListModel()) {

    info.apply {
      title.text = model.addressName
      subtitle.text = CryptoUtils.scaleTo16(model.address)
    }

    count.apply {
      title.text = model.count.formatCurrency()
      subtitle.text = WalletText.totalAssets + " (${GoldStoneApp.currencyCode})"
    }

    icon.apply { glideImage(model.avatar) }

    model.isWatchOnly isTrue { signalIcon.into(this) }
    model.isUsing isTrue { currentIcon.into(this) }

    setValueStyle()

  }

  private val signalIcon by lazy {
    RoundIcon(context)
      .apply {
        src = R.drawable.watch_only_icon
        iconColor = Spectrum.darkBlue
        iconSize = 20.uiPX()
        y += 12.uiPX()
      }
  }

  private val currentIcon by lazy {
    RoundIcon(context)
      .apply {
        src = R.drawable.current_icon
        iconColor = Spectrum.green
        iconSize = 20.uiPX()
        y += 45.uiPX()
        x += 32.uiPX()
      }
  }

  init {
    setGrayStyle()
  }


}