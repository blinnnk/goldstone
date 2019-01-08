package io.goldstone.blinnnk.common.sandbox.view.wallet

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blinnnk.common.sandbox.WalletModel
import io.goldstone.blinnnk.common.utils.click

/**
 * @date: 2019-01-07.
 * @author: yangLiHai
 * @description:
 */
class RecoverWalletAdapter(
	override val dataSet: ArrayList<WalletModel>,
	private val deleteAction: (postion: Int) -> Unit,
	private val recoverAction: (position: Int) -> Unit
): HoneyBaseAdapter<WalletModel, WalletRecoverCell>() {
	
	override fun generateCell(context: Context) = WalletRecoverCell(context)
	
	override fun WalletRecoverCell.bindCell(
		data: WalletModel,
		position: Int
	) {
		model = data
		deleteButton.click { deleteAction(position) }
		recoverButton.click { recoverAction(position) }
	}
}