package io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.model.PersonalMemoryTransactionRecordTable
import io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.view.PersonalMemoryTransactionRecordAdapter
import io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.view.PersonalMemoryTransactionRecordFragment
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
class PersonalMemoryTransactionRecordPresenter(
	override val fragment: PersonalMemoryTransactionRecordFragment,
	private val isSalesRecord: Boolean
) : BaseRecyclerPresenter<PersonalMemoryTransactionRecordFragment, PersonalMemoryTransactionRecordTable>() {

	private var id = ""
	override fun updateData() {
		if (isSalesRecord) {

		} else {

		}
		updateNetworkData(
			fragment.getAccountName(),
			""
		)
	}

	override fun loadMore() {
		super.loadMore()
		updateNetworkData(
			fragment.getAccountName(),
			id
		)
	}

	private fun updateNetworkData(
		accountName: String,
		id: String
	) {
		GoldStoneAPI.getPersonalMemoryTransactionRecord(
			accountName,
			id,
			{}
		) { personalMemoryTransactionRecordModel ->
			personalMemoryTransactionRecordModel?.let { it ->
				if (it.txList.isNotEmpty()) {
					val arrayList = ArrayList<PersonalMemoryTransactionRecordTable>()
					for (index: Int in 0 until it.txList.size) {
						arrayList.add(PersonalMemoryTransactionRecordTable(it.txList[index]))
					}

					fragment.asyncData.isNull() isTrue {
						fragment.asyncData = arrayList
					} otherwise {
						fragment.asyncData?.let {
							it.addAll(arrayList)
							fragment.getAdapter<PersonalMemoryTransactionRecordAdapter>()?.dataSet = fragment.asyncData.orEmptyArray()
							fragment.recyclerView.adapter?.notifyItemRangeChanged(
								fragment.asyncData?.count().orZero() - arrayList.size, fragment.asyncData?.count().orZero()
							)
							showBottomLoading(false)
						}
					}
					this.id = it.txList[it.txList.size - 1].id.toString()
				} else {
					val arrayList = ArrayList<PersonalMemoryTransactionRecordTable>()
					arrayList.add(PersonalMemoryTransactionRecordTable(1, 1.0, 2, "3", 4))
					arrayList.add(PersonalMemoryTransactionRecordTable(1, 1.0, 2, "3", 4))
					arrayList.add(PersonalMemoryTransactionRecordTable(1, 1.0, 2, "3", 4))
					fragment.asyncData.isNull() isTrue {
						fragment.asyncData = arrayList
					} otherwise {
						diffAndUpdateAdapterData<PersonalMemoryTransactionRecordAdapter>(arrayList)
					}
				}
			}
		}
	}
}