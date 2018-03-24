package io.goldstone.blockchain.module.home.wallet.wallet.presenter

import com.blinnnk.extension.addFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.wallet.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.wallet.view.WalletDetailFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.CurrentWalletDetailFragment

/**
 * @date 23/03/2018 3:45 PM
 * @author KaySaith
 */

class WalletDetailPresenter(
  override val fragment: WalletDetailFragment
  ) : BaseRecyclerPresenter<WalletDetailFragment, WalletDetailCellModel>() {

  fun showTransactionsFragment() {
    fragment.activity?.addFragment<TransactionFragment>(ContainerID.main)
  }

  fun showWalletListFragment() {
    fragment.activity?.addFragment<CurrentWalletDetailFragment>(ContainerID.main)
  }

  fun showNotificationListFragment() {
    fragment.activity?.addFragment<NotificationFragment>(ContainerID.main)
  }

  fun showTokenManagementFragment() {
    fragment.activity?.addFragment<TokenManagementFragment>(ContainerID.main)
  }

}