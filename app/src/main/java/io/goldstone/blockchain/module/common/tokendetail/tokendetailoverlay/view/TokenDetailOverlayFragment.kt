package io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view

import android.view.ViewGroup
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.presenter.TokenDetailOverlayPresenter

/**
 * @date 27/03/2018 3:41 PM
 * @author KaySaith
 */

class TokenDetailOverlayFragment : BaseOverlayFragment<TokenDetailOverlayPresenter>() {

  override val presenter = TokenDetailOverlayPresenter(this)

  override fun ViewGroup.initView() {
    headerTitle = "My ETH"
    addFragmentAndSetArgument<TokenDetailFragment>(ContainerID.content) {
      // Send Arguments
    }

  }

}