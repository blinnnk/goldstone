package io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.presenter

import android.annotation.SuppressLint
import android.view.View
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment

/**ø
 * @date 27/03/2018 3:41 PM
 * @author KaySaith
 */

class TokenDetailOverlayPresenter(
  override val fragment: TokenDetailOverlayFragment
  ) : BaseOverlayPresenter<TokenDetailOverlayFragment>() {

  fun showTokenDetailFragment() {
    fragment.apply {
      addFragmentAndSetArgument<TokenDetailFragment>(ContainerID.content) {
        // Send Arguments
      }
    }
  }

  fun resetHeader() {
    fragment.apply {
      valueHeader?.isHidden()
      recoveryOverlayHeader()
    }
  }

  @SuppressLint("SetTextI18n")
  fun setValueHeader() {
    fragment.apply {
      overlayView.header.title.isHidden()
      valueHeader.isNull().isTrue {
        customHeader = {
          valueHeader = TwoLineTitles(context)
          valueHeader
            ?.apply {
              title.text = "MY ETH"
              subtitle.text = "2.38 ETH ≈(1126.32 USD)"
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