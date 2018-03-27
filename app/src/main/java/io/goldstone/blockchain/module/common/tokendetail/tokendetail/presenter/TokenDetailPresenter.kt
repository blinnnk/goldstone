package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import com.blinnnk.animation.updateHeightAnimation
import com.blinnnk.extension.getRealScreenHeight
import com.blinnnk.extension.hideChildFragment
import com.blinnnk.extension.orZero
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel

/**
 * @date 27/03/2018 3:21 PM
 * @author KaySaith
 */

class TokenDetailPresenter(
  override val fragment: TokenDetailFragment
  ) : BaseRecyclerPresenter<TokenDetailFragment, TransactionListModel>() {

  fun showTransactionDetailFragment() {
    fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
      hideChildFragment(fragment)
      addFragmentAndSetArgument<TransactionDetailFragment>(ContainerID.content) {
        // Send Arguments
      }
      overlayView.apply {
        header.showBackButton(true) {
          presenter.popFragmentFrom<TransactionDetailFragment>()
          contentLayout.updateHeightAnimation(context?.getRealScreenHeight().orZero())
        }
      }
    }
  }

}