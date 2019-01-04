package io.goldstone.blockchain.module.home.wallet.walletdetail.contract

import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailHeaderModel


/**
 * @author KaySaith
 * @date  2018/11/11
 */
interface WalletDetailContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {
		var asyncData: ArrayList<WalletDetailCellModel>?
		fun showLoading(status: Boolean)
		fun setUnreadCount(count: Int)
		fun showSelectionDashboard(tokens: ArrayList<WalletDetailCellModel>, isAddress: Boolean)
		fun setHeaderData(model: WalletDetailHeaderModel)
		fun showMnemonicBackUpFragment()
		fun showNotificationListFragment()
		fun showAddressSelectionFragment(data: WalletDetailCellModel)
		fun showDepositFragment(data: WalletDetailCellModel)
		fun updateAdapterData(data: ArrayList<WalletDetailCellModel>)
		fun showMnemonicBackUpDialog()
		fun showChainError()
	}

	interface GSPresenter : GoldStonePresenter {
		fun showTransferDashboard(isAddress: Boolean)
		fun updateUnreadCount()
	}
}