package io.goldstone.blinnnk.module.home.wallet.tokenmanagement.tokenSearch.view

import android.content.Context
import android.widget.Switch
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blinnnk.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable

/**
 * @date 27/03/2018 11:25 AM
 * @author KaySaith
 */
class TokenSearchAdapter(
	override val dataSet: ArrayList<DefaultTokenTable>,
	private val hold: (DefaultTokenTable, Switch) -> Unit
) : HoneyBaseAdapter<DefaultTokenTable, TokenSearchCell>() {

	override fun generateCell(context: Context) = TokenSearchCell(context)

	override fun TokenSearchCell.bindCell(data: DefaultTokenTable, position: Int) {
		tokenSearchModel = data
		hold(data, switch)
	}
}