package io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.presenter

import android.annotation.SuppressLint
import android.view.View
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel

/**ø
 * @date 27/03/2018 3:41 PM
 * @author KaySaith
 */

class TokenDetailOverlayPresenter(
  override val fragment: TokenDetailOverlayFragment
  ) : BaseOverlayPresenter<TokenDetailOverlayFragment>() {

  fun showTokenDetailFragment(symbol: String?) {
    fragment.apply {
      addFragmentAndSetArgument<TokenDetailFragment>(ContainerID.content) {
        putString(ArgumentKey.tokenDetail, symbol)
      }
    }
  }

  @SuppressLint("SetTextI18n")
  fun setValueHeader(token: WalletDetailCellModel?) {
    fragment.apply {
      overlayView.header.title.isHidden()
      valueHeader.isNull() isTrue {
        customHeader = {
          valueHeader = TwoLineTitles(context)
          valueHeader
            ?.apply {
              title.text = "MY ${token?.symbol}"
              subtitle.text = "${token?.count} ${token?.symbol} ≈ ${token?.currency} (${GoldStoneApp.currencyCode})"
              setBlackTitles()
              isCenter = true
            }
            ?.into(this)
          valueHeader?.apply {
            setCenterInHorizontal()
            y += 15.uiPX()
          }
        }
      } otherwise {
        valueHeader?.visibility = View.VISIBLE
      }
    }
  }

}