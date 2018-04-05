package io.goldstone.blockchain.module.home.wallet.walletlist.view

import android.annotation.SuppressLint
import android.content.Context
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.baseInfocell.BaseValueCell
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.SymbolText
import io.goldstone.blockchain.common.value.WalletText
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.module.home.wallet.walletlist.model.WalletListModel

@SuppressLint("SetTextI18n")
/**
 * @date 24/03/2018 8:58 PM
 * @author KaySaith
 */

class WalletListCell(context: Context) : BaseValueCell(context) {

  var model: WalletListModel by observing(WalletListModel()) {

    info.apply {
      title.text = model.addressName
      subtitle.text = CryptoUtils.scaleTO16(model.address)
    }

    count.apply {
      title.text = model.count.toString()
      subtitle.text = WalletText.totalAssets + SymbolText.usd
    }

    icon.apply {
      glideImage(model.avatar)
    }

    setValueStyle()

  }

  init {
    setGrayStyle()
  }

}