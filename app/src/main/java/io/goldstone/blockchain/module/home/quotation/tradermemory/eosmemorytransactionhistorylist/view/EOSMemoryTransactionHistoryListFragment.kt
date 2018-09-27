package io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.view

import android.annotation.SuppressLint
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.model.EOSMemoryTransactionHistoryListTable
import io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.presenter.EOSMemoryTransactionHistoryListPresenter

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
@SuppressLint("ValidFragment")
class EOSMemoryTransactionHistoryListFragment(private val isSalesRecord: Boolean) :
	BaseRecyclerFragment<EOSMemoryTransactionHistoryListPresenter, EOSMemoryTransactionHistoryListTable>() {
	override val pageTitle: String
		get() = ""
	override val presenter: EOSMemoryTransactionHistoryListPresenter = EOSMemoryTransactionHistoryListPresenter(
		this,
		isSalesRecord
	)

	override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<EOSMemoryTransactionHistoryListTable>?) {
		recyclerView.adapter = EOSMemoryTransactionHistoryListAdapter(
			asyncData.orEmptyArray(),
			isSalesRecord
		) { it ->
			it.setOnClickListener { _ ->
				presenter.showPersonalMemoryTransactionRecord(it.model.account)
			}
		}
	}

}