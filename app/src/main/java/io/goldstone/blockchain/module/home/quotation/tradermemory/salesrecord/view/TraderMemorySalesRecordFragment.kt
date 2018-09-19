package io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecord.view

import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecord.model.TraderMemorySalesRecordModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecord.presenter.TraderMemorySalesRecordPresenter

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
class TraderMemorySalesRecordFragment :
	BaseRecyclerFragment<TraderMemorySalesRecordPresenter, TraderMemorySalesRecordModel>() {
	override val presenter: TraderMemorySalesRecordPresenter = TraderMemorySalesRecordPresenter(this)

	override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<TraderMemorySalesRecordModel>?) {

	}

}