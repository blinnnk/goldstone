package io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view

import android.view.ViewGroup
import com.blinnnk.extension.into
import com.blinnnk.extension.setCenterInHorizontal
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.presenter.TokenDetailOverlayPresenter

/**
 * @date 27/03/2018 3:41 PM
 * @author KaySaith
 */

class TokenDetailOverlayFragment : BaseOverlayFragment<TokenDetailOverlayPresenter>() {

  private val valueHeader by lazy { TwoLineTitles(context!!) }

  override val presenter = TokenDetailOverlayPresenter(this)

  override fun ViewGroup.initView() {
    customHeader = {
      valueHeader
        .apply {
          title.text = "MY ETH"
          subtitle.text = "2.38 ETH ≈(1126.32 USD)"
          setBlackTitles()
          isCenter = true
        }
        .into(this)
      valueHeader.apply {
        setCenterInVertical()
        setCenterInHorizontal()
      }
    }
    addFragmentAndSetArgument<TokenDetailFragment>(ContainerID.content) {
      // Send Arguments
    }

  }

}