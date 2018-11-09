package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.model.WalletListModel

/**
 * @date 24/03/2018 8:57 PM
 * @author KaySaith
 */
class WalletListAdapter(
	override val dataSet: ArrayList<WalletListModel>,
	private val callback: WalletListCardCell.() -> Unit
) : HoneyBaseAdapter<WalletListModel, WalletListCardCell>() {

	override fun generateCell(context: Context) = WalletListCardCell(context)

	override fun WalletListCardCell.bindCell(data: WalletListModel, position: Int) {
		model = data
		callback(this)
	}
}