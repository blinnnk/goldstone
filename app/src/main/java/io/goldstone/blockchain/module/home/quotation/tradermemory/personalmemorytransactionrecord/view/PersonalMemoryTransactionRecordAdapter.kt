package io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.model.PersonalMemoryTransactionRecordTable

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */

class PersonalMemoryTransactionRecordAdapter(
	override val dataSet: ArrayList<PersonalMemoryTransactionRecordTable>,
	private val isSalesRecord: Boolean,
	private val hold: (PersonalMemoryTransactionRecordCell) -> Unit
) : HoneyBaseAdapter<PersonalMemoryTransactionRecordTable, PersonalMemoryTransactionRecordCell>() {

	override fun generateCell(context: Context) = PersonalMemoryTransactionRecordCell(context, isSalesRecord)

	override fun PersonalMemoryTransactionRecordCell.bindCell(data: PersonalMemoryTransactionRecordTable, position: Int) {
		model = data
		hold(this)
	}

	override fun getItemCount(): Int {
		return dataSet.size
	}

}