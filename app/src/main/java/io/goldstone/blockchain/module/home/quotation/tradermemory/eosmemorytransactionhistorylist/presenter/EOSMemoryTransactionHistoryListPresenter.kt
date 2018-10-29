package io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.presenter

import android.os.Bundle
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.model.EOSMemoryTransactionHistoryListTable
import io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.view.EOSMemoryTransactionHistoryListAdapter
import io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.view.EOSMemoryTransactionHistoryListFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.view.PersonalMemoryTransactionRecordFragment

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
class EOSMemoryTransactionHistoryListPresenter(
	override val fragment: EOSMemoryTransactionHistoryListFragment,
	private val isSalesRecord: Boolean
) : BaseRecyclerPresenter<EOSMemoryTransactionHistoryListFragment, EOSMemoryTransactionHistoryListTable>() {

	override fun updateData() {
		super.updateData()
		// 0. 最近交易记录 ; 1. 最近大单记录
		val mode = if (isSalesRecord) 0 else 1
		GoldStoneAPI.getEOSMemoryTransactionHistory(mode.toString(), {}) { it ->
			it?.let {
				val arrayList = ArrayList<EOSMemoryTransactionHistoryListTable>()
				for (index: Int in 0 until it.txList.size) {
					arrayList.add(EOSMemoryTransactionHistoryListTable(it.txList[index]))
				}
				fragment.asyncData.isNull() isTrue {
					fragment.asyncData = arrayList
				} otherwise {
					diffAndUpdateSingleCellAdapterData<EOSMemoryTransactionHistoryListAdapter>(arrayList)
				}
			}
		}
	}

	fun showPersonalMemoryTransactionRecord(account: String) {
		(fragment.parentFragment?.parentFragment?.parentFragment as? BaseOverlayFragment<*>)?.apply {
			presenter.showTargetFragment<PersonalMemoryTransactionRecordFragment>(bundle = Bundle().apply {
				putString(
					"内存交易",
					"个人交易"
				)
				putString(
					"account",
					account
				) })
		}
		
	}
}