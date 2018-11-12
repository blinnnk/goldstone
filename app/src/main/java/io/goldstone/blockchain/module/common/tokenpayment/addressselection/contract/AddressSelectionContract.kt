package io.goldstone.blockchain.module.common.tokenpayment.addressselection.contract

import io.goldstone.blockchain.crypto.multichain.QRCode
import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel


/**
 * @author KaySaith
 * @date  2018/11/12
 */
interface AddressSelectionContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {
		var asyncData: ArrayList<ContactTable>?
		fun showAddresses(data: ArrayList<ContactTable>)
		fun goToPaymentDetailFragment(address: String, count: Double, token: WalletDetailCellModel)
		fun updateInputStatus()
		fun goToPaymentDetailWithExistedCheckedDialog(
			addresses: List<String>,
			toAddress: String,
			count: Double,
			token: WalletDetailCellModel
		)

		fun showError(error: Throwable)
	}

	interface GSPresenter : GoldStonePresenter {
		fun showPaymentDetailByQRCode(qrCode: QRCode)
		fun showPaymentDetail(toAddress: String, count: Double)
	}
}