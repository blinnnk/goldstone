package io.goldstone.blockchain.module.home.wallet.walletdetail.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.currentwalletdetail.view.CurrentWalletDetailFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailAdapter
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
    WalletTable.getCurrentWalletInfo {
      updateAllTokensInWalletBy(it!!)
    }
  }

  fun updateAllTokensInWalletBy(walletInfo: WalletTable) {
    // Check the count of local wallets
    WalletTable.apply { getAll { walletCount = size } }
    // Check the info of wallet currency list
    WalletDetailCellModel.getModels(walletInfo.address) { newDataSet ->
      fragment.apply {
        context?.runOnUiThread {
          asyncData.isNull().isTrue {
            asyncData = newDataSet
          } otherwise {
            getAdapter<WalletDetailAdapter>()?.apply {
              // Comparison the data, if they are different then update adapter
              diffDataSetChanged(dataSet, newDataSet) {
                it.isTrue {
                  dataSet.clear()
                  dataSet.addAll(newDataSet)
                  notifyDataSetChanged()
                }
              }
            }
          }
          val totalBalance = asyncData?.sumByDouble {
            CryptoUtils.formatDouble(it.currency)
          }
          // Once the calculation is finished then update `WalletTable`
          WalletTable.myBalance = totalBalance
          recyclerView.getItemViewAtAdapterPosition<WalletDetailHeaderView>(0) {
            model = WalletDetailHeaderModel(
              null,
              walletInfo.name,
              CryptoUtils.scaleAddress(walletInfo.address),
              totalBalance.toString(),
              WalletTable.walletCount.orZero()
            )
          }
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
    fragment.activity?.addFragmentAndSetArguments<WalletSettingsFragment>(ContainerID.main) {
      putString(ArgumentKey.walletSettingsTitle, WalletSettingsText.walletSettings)
    }
  }

  fun showMyTokenDetailFragment() {
    fragment.activity?.addFragment<TokenDetailOverlayFragment>(ContainerID.main)
  }

  private fun diffDataSetChanged(
    oldData: ArrayList<WalletDetailCellModel> ,
    newData: ArrayList<WalletDetailCellModel>,
    hold: (Boolean) -> Unit
  ){
    if (oldData.size != newData.size) hold(true)
    else {
      oldData.forEach { data ->
        hold( newData.firstOrNull { it.symbol == data.symbol }.isNull())
      }
    }
  }
}
