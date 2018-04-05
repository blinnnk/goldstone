package io.goldstone.blockchain.module.home.wallet.walletdetail.view

import android.annotation.SuppressLint
import android.content.Context
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.component.SquareIcon
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.CommonCellSize
import io.goldstone.blockchain.common.value.SymbolText
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel

@SuppressLint("SetTextI18n")
/**
 * @date 23/03/2018 6:19 PM
 * @author KaySaith
 */

class WalletDetailCell(context: Context) : BaseCell(context) {

  var model: WalletDetailCellModel? by observing(null) {
    model?.apply {
      icon.image.glideImage(iconUrl)
      tokenInfo.title.text = symbol
      tokenInfo.subtitle.text = name
      valueInfo.title.text = balance.toString()
      valueInfo.subtitle.text = "≈ " + currency + SymbolText.usd
    }
  }

  private val icon by lazy { SquareIcon(context) }
  private val tokenInfo by lazy { TwoLineTitles(context) }
  private val valueInfo by lazy { TwoLineTitles(context) }

  init {

    icon.into(this)

    tokenInfo.into(this)

    valueInfo
      .apply {
        isFloatRight = true
      }
      .into(this)

    tokenInfo.apply {
      setCenterInVertical()
      x += CommonCellSize.iconPadding
    }

    icon.setCenterInVertical()

    valueInfo.apply {
      setAlignParentRight()
      setCenterInVertical()
      x -= 30.uiPX()
    }

  }

}