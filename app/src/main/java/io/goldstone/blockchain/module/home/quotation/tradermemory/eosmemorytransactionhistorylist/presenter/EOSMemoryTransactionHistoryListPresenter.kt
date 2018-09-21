package io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.presenter

import android.util.Log
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.model.EOSMemoryTransactionHistoryListTable
import io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.view.EOSMemoryTransactionHistoryListAdapter
import io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.view.EOSMemoryTransactionHistoryListFragment

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
class EOSMemoryTransactionHistoryListPresenter(
	override val fragment: EOSMemoryTransactionHistoryListFragment,
	private val isSalesRecord: Boolean
) : BaseRecyclerPresenter<EOSMemoryTransactionHistoryListFragment, EOSMemoryTransactionHistoryListTable>() {

	override fun updateData() {
		if (isSalesRecord) {

		} else {

		}
		GoldStoneAPI.getEOSMemoryTransactionHistory("wangchenxing", {}) { it ->
			it?.let {
				if (it.txList.isNotEmpty()) {
					val arrayList = ArrayList<EOSMemoryTransactionHistoryListTable>()
					for (index: Int in 0 until it.txList.size) {
						arrayList.add(EOSMemoryTransactionHistoryListTable(it.txList[index]))
					}
					fragment.asyncData.isNull() isTrue {
						fragment.asyncData = arrayList
					} otherwise {
						diffAndUpdateSingleCellAdapterData<EOSMemoryTransactionHistoryListAdapter>(arrayList)
					}
				} else {
					val arrayList = ArrayList<EOSMemoryTransactionHistoryListTable>()
					arrayList.add(EOSMemoryTransactionHistoryListTable(1, 2, "3", 4))
					arrayList.add(EOSMemoryTransactionHistoryListTable(1, 2, "3", 4))
					arrayList.add(EOSMemoryTransactionHistoryListTable(1, 2, "3", 4))
					fragment.asyncData.isNull() isTrue {
						fragment.asyncData = arrayList
					} otherwise {
						diffAndUpdateSingleCellAdapterData<EOSMemoryTransactionHistoryListAdapter>(arrayList)
					}
				}
			}
		}
	}
}