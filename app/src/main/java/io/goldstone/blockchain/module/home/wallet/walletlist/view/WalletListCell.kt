package io.goldstone.blockchain.module.home.wallet.walletlist.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.BaseInfoCell.BaseValueCell
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.SymbolText
import io.goldstone.blockchain.common.value.WalletText
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
      subtitle.text = model.address
    }

    count.apply {
      title.text = model.count.toString()
      subtitle.text = WalletText.totalAssets + SymbolText.usd
    }

    icon.apply {
      iconColor = GrayScale.lightGray
      scaleType = ImageView.ScaleType.CENTER_CROP
      glideImage(model.avatar)
    }

  }

  init {
    setGrayStyle()
  }

}