package io.goldstone.blockchain.module.home.wallet.tokenselectionlist

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel

/**
 * @date 2018/6/7 3:29 AM
 * @author KaySaith
 */
class TokenSelectionAdapter(
	override val dataSet: ArrayList<WalletDetailCellModel>,
	private val hold: TokenSelectionCell.() -> Unit
) : HoneyBaseAdapter<WalletDetailCellModel, TokenSelectionCell>() {

	override fun generateCell(context: Context) = TokenSelectionCell(context)

	override fun TokenSelectionCell.bindCell(data: WalletDetailCellModel, position: Int) {
		model = data
		hold(this)
	}
}