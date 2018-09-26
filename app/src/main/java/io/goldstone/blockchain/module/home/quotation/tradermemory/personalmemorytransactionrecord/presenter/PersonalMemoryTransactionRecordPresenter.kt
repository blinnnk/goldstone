package io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.presenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.model.PersonalMemoryTransactionRecordTable
import io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.view.PersonalMemoryTransactionRecordAdapter
import io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.view.PersonalMemoryTransactionRecordFragment

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
class PersonalMemoryTransactionRecordPresenter(
	override val fragment: PersonalMemoryTransactionRecordFragment,
	private val isSalesRecord: Boolean
) : BaseRecyclerPresenter<PersonalMemoryTransactionRecordFragment, PersonalMemoryTransactionRecordTable>() {

	override fun updateData() {
		if (isSalesRecord) {

		} else {

		}
		GoldStoneAPI.getPersonalMemoryTransactionRecord(fragment.getAccountName(), {}) { it ->
			it?.let {
				if (it.txList.isNotEmpty()) {
					val arrayList = ArrayList<PersonalMemoryTransactionRecordTable>()
					for (index: Int in 0 until it.txList.size) {
						arrayList.add(PersonalMemoryTransactionRecordTable(it.txList[index]))
					}
					fragment.asyncData.isNull() isTrue {
						fragment.asyncData = arrayList
					} otherwise {
						diffAndUpdateSingleCellAdapterData<PersonalMemoryTransactionRecordAdapter>(arrayList)
					}
				} else {
					val arrayList = ArrayList<PersonalMemoryTransactionRecordTable>()
					arrayList.add(PersonalMemoryTransactionRecordTable(1, 1.0, 2, "3", 4))
					arrayList.add(PersonalMemoryTransactionRecordTable(1, 1.0, 2, "3", 4))
					arrayList.add(PersonalMemoryTransactionRecordTable(1, 1.0, 2, "3", 4))
					fragment.asyncData.isNull() isTrue {
						fragment.asyncData = arrayList
					} otherwise {
						diffAndUpdateSingleCellAdapterData<PersonalMemoryTransactionRecordAdapter>(arrayList)
					}
				}
			}
		}
	}
}