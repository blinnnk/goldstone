package io.goldstone.blinnnk.common.sandbox.view.wallet

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blinnnk.common.sandbox.WalletBackUpModel
import io.goldstone.blinnnk.common.utils.click

/**
 * @date: 2019-01-07.
 * @author: yangLiHai
 * @description:
 */
class RecoverWalletAdapter(
	override val dataSet: ArrayList<WalletBackUpModel>,
	private val deleteAction: (position: Int) -> Unit,
	private val recoverAction: (position: Int) -> Unit
) : HoneyBaseAdapter<WalletBackUpModel, WalletBackUpCell>() {
	
	override fun generateCell(context: Context) = WalletBackUpCell(context)
	
	override fun WalletBackUpCell.bindCell(
		data: WalletBackUpModel,
		position: Int
	) {
		model = data
		deleteButton.click { deleteAction(position) }
		recoverButton.click { recoverAction(position) }
	}
}