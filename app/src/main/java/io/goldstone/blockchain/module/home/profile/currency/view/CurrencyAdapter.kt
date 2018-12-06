package io.goldstone.blockchain.module.home.profile.currency.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.kernel.commontable.SupportCurrencyTable
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 26/03/2018 2:26 PM
 * @author KaySaith
 */

class CurrencyAdapter(
	override val dataSet: ArrayList<SupportCurrencyTable>,
	private val callback: SupportCurrencyTable.() -> Unit
) : HoneyBaseAdapter<SupportCurrencyTable, CurrencyCell>() {

	override fun generateCell(context: Context) = CurrencyCell(context)

	override fun CurrencyCell.bindCell(data: SupportCurrencyTable, position: Int) {
		model = data
		onClick {
			callback(data)
			setSwitchStatusBy(true)
			preventDuplicateClicks()
		}
	}

}