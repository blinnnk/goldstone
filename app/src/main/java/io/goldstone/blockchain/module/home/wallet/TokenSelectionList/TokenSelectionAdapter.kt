package io.goldstone.blockchain.module.home.wallet.tokenselectionlist

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable

/**
 * @date 2018/6/7 3:29 AM
 * @author KaySaith
 */
class TokenSelectionAdapter(
	override val dataSet: ArrayList<DefaultTokenTable>,
	private val hold: TokenSelectionCell.() -> Unit
) : HoneyBaseAdapter<DefaultTokenTable,
	TokenSelectionCell>() {
	
	override fun generateCell(context: Context) = TokenSelectionCell(context)
	
	override fun TokenSelectionCell.bindCell(data: DefaultTokenTable, position: Int) {
		model = data
		hold(this)
	}
}