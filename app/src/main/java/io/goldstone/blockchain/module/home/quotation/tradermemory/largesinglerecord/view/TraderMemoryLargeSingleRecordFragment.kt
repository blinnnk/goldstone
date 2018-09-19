package io.goldstone.blockchain.module.home.quotation.tradermemory.largesinglerecord.view

import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.module.home.quotation.tradermemory.largesinglerecord.model.TraderMemoryLargeSingleRecordModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.largesinglerecord.presenter.TraderMemoryLargeSingleRecordPresenter
import io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecord.model.TraderMemorySalesRecordModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecord.presenter.TraderMemorySalesRecordPresenter

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
class TraderMemoryLargeSingleRecordFragment :
	BaseRecyclerFragment<TraderMemoryLargeSingleRecordPresenter, TraderMemoryLargeSingleRecordModel>() {
	override val presenter: TraderMemoryLargeSingleRecordPresenter = TraderMemoryLargeSingleRecordPresenter(this)

	override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<TraderMemoryLargeSingleRecordModel>?) {

	}

}