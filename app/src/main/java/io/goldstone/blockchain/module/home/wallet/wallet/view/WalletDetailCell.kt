package io.goldstone.blockchain.module.home.wallet.wallet.view

import android.content.Context
import android.widget.LinearLayout
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.component.SquareIcon
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.utils.into
import io.goldstone.blockchain.common.utils.setAlignParentRight
import io.goldstone.blockchain.common.utils.setCenterInVertical
import io.goldstone.blockchain.common.utils.setMargins
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.SymbolText
import io.goldstone.blockchain.module.home.wallet.wallet.model.WalletDetailCellModel

/**
 * @date 23/03/2018 6:19 PM
 * @author KaySaith
 */

class WalletDetailCell(context: Context) : BaseCell(context) {

  var model: WalletDetailCellModel by observing(WalletDetailCellModel()) {
    icon.src = model.src
    tokenInfo.title.text = model.tokenSymbol
    tokenInfo.subtitle.text = model.tokenName
    valueInfo.title.text = model.count.toString()
    valueInfo.subtitle.text = model.moneyText
  }

  private val icon by lazy { SquareIcon(context) }
  private val tokenInfo by lazy { TwoLineTitles(context) }
  private val valueInfo by lazy { TwoLineTitles(context) }

  init {
    setWillNotDraw(false)

    layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 60.uiPX()).apply {
      leftMargin = PaddingSize.device
    }

    icon
      .apply {
        src = R.drawable.etc_icon
        setMargins<LinearLayout.LayoutParams> { topMargin = 16.uiPX() }
      }
      .into(this)

    tokenInfo
      .apply {
        title.text = "ETH"
        subtitle.text = "Global, Ethereum"
      }
      .into(this)

    valueInfo
      .apply {
        isFloatRight = true
        title.text = "12.68"
        subtitle.text = "≈ 6583.78 ${ SymbolText.usd }"
      }
      .into(this)

    tokenInfo.apply {
      setCenterInVertical()
      x += 40.uiPX()
    }

    valueInfo.apply {
      setAlignParentRight()
      setCenterInVertical()
      x -= 30.uiPX()
    }

  }

}