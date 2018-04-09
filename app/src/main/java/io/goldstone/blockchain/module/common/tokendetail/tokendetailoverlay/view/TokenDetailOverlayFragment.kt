package io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view

import android.view.ViewGroup
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.presenter.TokenDetailOverlayPresenter

/**
 * @date 27/03/2018 3:41 PM
 * @author KaySaith
 */

class TokenDetailOverlayFragment : BaseOverlayFragment<TokenDetailOverlayPresenter>() {

  var valueHeader: TwoLineTitles? = null

  val symbol by lazy { arguments?.getString(ArgumentKey.tokenDetail) }

  override val presenter = TokenDetailOverlayPresenter(this)

  override fun ViewGroup.initView() {
    presenter.setValueHeader(symbol)
    presenter.showTokenDetailFragment(symbol)
  }

}