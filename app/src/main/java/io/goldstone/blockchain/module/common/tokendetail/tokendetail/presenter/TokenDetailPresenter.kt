package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.v4.app.Fragment
import com.blinnnk.extension.hideChildFragment
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.view.AddressSelectionFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel

/**
 * @date 27/03/2018 3:21 PM
 * @author KaySaith
 */

class TokenDetailPresenter(
  override val fragment: TokenDetailFragment
  ) : BaseRecyclerPresenter<TokenDetailFragment, TransactionListModel>() {

  fun showAddressSelectionFragment() {
    WalletTable.isWatchOnlyWalletShowAlertOrElse(fragment.context!!) {
      shoTargetFragment<AddressSelectionFragment>(TokenDetailText.address)
    }

  }

  fun showTransactionDetailFragment() {
    shoTargetFragment<TransactionDetailFragment>(TransactionText.detail)
  }

  fun showDepositFragment() {
    WalletTable.isWatchOnlyWalletShowAlertOrElse(fragment.context!!) {
      //
    }
  }

  private inline fun<reified T: Fragment> shoTargetFragment(title: String) {
    fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
      hideChildFragment(fragment)
      addFragmentAndSetArgument<T>(ContainerID.content) {
        // Send Arguments
      }
      overlayView.header.apply {
        showBackButton(true) {
          presenter.setValueHeader()
          presenter.popFragmentFrom<T>()
          setHeightMatchParent()
        }
        showCloseButton(false)
      }
      presenter.resetHeader()
      headerTitle = title
    }
  }

}