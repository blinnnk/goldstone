package io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view

import android.view.ViewGroup
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.presenter.TokenDetailOverlayPresenter
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 27/03/2018 3:41 PM
 * @author KaySaith
 */

class TokenDetailOverlayFragment : BaseOverlayFragment<TokenDetailOverlayPresenter>() {

  var valueHeader: TwoLineTitles? = null

  val token by lazy { arguments?.get(ArgumentKey.tokenDetail) as? WalletDetailCellModel }

  var confirmButton: RoundButton? = null

  override val presenter = TokenDetailOverlayPresenter(this)

  override fun ViewGroup.initView() {
    presenter.setValueHeader(token)
    presenter.showTokenDetailFragment(token?.symbol)
  }

  fun showConfirmButton(status: Boolean = true) {
    confirmButton.isNotNull {
      if (!status) {
        overlayView.header.removeView(confirmButton)
        confirmButton = null
      }
    } otherwise {
      if (status) confirmButton.isNull {
        confirmButton = RoundButton(context!!)
        confirmButton?.apply {
          setSmallButton(GrayScale.lightGray, GrayScale.midGray)
          text = CommonText.confirm.toUpperCase()
          x -= PaddingSize.device
        }?.into(overlayView.header)
        confirmButton?.apply {
          setAlignParentRight()
          setCenterInVertical()
        }
      }
    }
  }

  // 点击确认按钮后需要执行的操作
  var confirmButtonClickEvent: Runnable? = null

  fun setConfirmStatus(canClick: Boolean = false) {
    confirmButton?.apply {
      canClick.isTrue {
        updateColor(Spectrum.green, Spectrum.white)
        onClick { confirmButtonClickEvent?.run()  }
      } otherwise {
        updateColor(GrayScale.lightGray, GrayScale.midGray)
        onClick { view?.context?.alert("You should set an address") }
      }
    }
  }

}