package io.goldstone.blockchain.module.home.wallet.walletdetail.presenter

import com.blinnnk.extension.addFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import io.goldstone.blockchain.module.home.wallet.currentwalletdetail.view.CurrentWalletDetailFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment

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

  fun showWalletSettingsFragment() {
    fragment.activity?.addFragment<WalletSettingsFragment>(ContainerID.main)
  }

  fun showMyTokenDetailFragment() {
    fragment.activity?.addFragment<TokenDetailOverlayFragment>(ContainerID.main)
  }

}