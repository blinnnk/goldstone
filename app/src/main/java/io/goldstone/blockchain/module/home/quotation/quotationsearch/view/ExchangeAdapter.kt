package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date: 2018/8/29.
 * @author: yanglihai
 * @description:
 */
class ExchangeAdapter(
	override val dataSet: ArrayList<ExchangeTable>,
	private val clickEvent: (data: ExchangeTable, isChecked: Boolean) -> Unit
) : HoneyBaseAdapter<ExchangeTable, ExchangeCell>() {

	override fun generateCell(context: Context): ExchangeCell {
		return ExchangeCell(context)
	}

	override fun ExchangeCell.bindCell(data: ExchangeTable, position: Int) {
		model = data
		checkBox.onClick {
			clickEvent(data, checkBox.isChecked)
			checkBox.preventDuplicateClicks()
		}
	}
}