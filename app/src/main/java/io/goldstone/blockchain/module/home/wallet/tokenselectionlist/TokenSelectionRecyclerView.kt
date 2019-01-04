package io.goldstone.blockchain.module.home.wallet.tokenselectionlist

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent

/**
 * @date 2018/6/7 3:29 AM
 * @author KaySaith
 */
class TokenSelectionRecyclerView(context: Context) : BaseRecyclerView(context) {

	init {
		backgroundColor = Color.RED
		layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
	}

	companion object {

		fun showTransferAddressFragment(context: Context?, token: WalletDetailCellModel) {
			val bundle = Bundle().apply {
				putSerializable(ArgumentKey.tokenDetail, token)
				putBoolean(ArgumentKey.fromQuickTransfer, true)
			}
			TokenDetailOverlayFragment.show(context, bundle)
		}

		fun showDepositFragment(context: Context?, token: WalletDetailCellModel) {
			val bundle = Bundle().apply {
				putSerializable(ArgumentKey.tokenDetail, token)
				putBoolean(ArgumentKey.fromQuickDeposit, true)
			}
			TokenDetailOverlayFragment.show(context, bundle)
		}
	}
}