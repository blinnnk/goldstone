package io.goldstone.blockchain.module.home.wallet.tokenselectionlist

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.bitcoinj.wallet.Wallet
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 2018/6/7 3:29 AM
 * @author KaySaith
 */
class TokenSelectionAdapter(
	override val dataSet: ArrayList<WalletDetailCellModel>,
	private val clickEvent: (data: WalletDetailCellModel) -> Unit
) : HoneyBaseAdapter<WalletDetailCellModel, TokenSelectionCell>() {

	override fun generateCell(context: Context) = TokenSelectionCell(context)

	override fun TokenSelectionCell.bindCell(data: WalletDetailCellModel, position: Int) {
		model = data
		onClick {
			clickEvent(data)
			preventDuplicateClicks()
		}
	}
}