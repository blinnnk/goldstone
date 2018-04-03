package io.goldstone.blockchain.module.home.wallet.walletdetail.presenter

import com.blinnnk.extension.addFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.currentwalletdetail.view.CurrentWalletDetailFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailHeaderModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailHeaderView
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.runOnUiThread

/**
 * @date 23/03/2018 3:45 PM
 * @author KaySaith
 */

class WalletDetailPresenter(
  override val fragment: WalletDetailFragment
) : BaseRecyclerPresenter<WalletDetailFragment, WalletDetailCellModel>() {

  override fun updateData(asyncData: ArrayList<WalletDetailCellModel>?) {
    WalletTable.getCurrentWalletAddress {
      it?.let { getAllTokensInWalletBy(it) }
    }
  }

  private fun getAllTokensInWalletBy(walletInfo: WalletTable) {
    WalletDetailCellModel.getModels(walletInfo.address) {
      fragment.context?.runOnUiThread {
        fragment.asyncData = it
        val totalBalance = it.sumByDouble { it.balance }.toString()
        fragment.recyclerView.getItemViewAtAdapterPosition<WalletDetailHeaderView>(0) {
          model = WalletDetailHeaderModel(null, walletInfo.name, CryptoUtils.scaleAddress(walletInfo.address), totalBalance, 5)
        }
      }
    }
  }

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