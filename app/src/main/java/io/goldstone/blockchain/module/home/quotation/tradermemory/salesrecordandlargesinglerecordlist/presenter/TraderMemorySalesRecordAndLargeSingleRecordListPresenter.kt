package io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecordandlargesinglerecordlist.presenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecordandlargesinglerecordlist.model.TraderMemorySalesRecordAndLargeSingleRecordListModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecordandlargesinglerecordlist.view.TraderMemorySalesRecordAndLargeSingleRecordListAdapter
import io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecordandlargesinglerecordlist.view.TraderMemorySalesRecordAndLargeSingleRecordListFragment

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
class TraderMemorySalesRecordAndLargeSingleRecordListPresenter(
	override val fragment: TraderMemorySalesRecordAndLargeSingleRecordListFragment,
	private val isSalesRecord: Boolean
) : BaseRecyclerPresenter<TraderMemorySalesRecordAndLargeSingleRecordListFragment, TraderMemorySalesRecordAndLargeSingleRecordListModel>() {

	override fun updateData() {
		if (isSalesRecord) {

		} else {

		}
		fragment.asyncData.isNull() isTrue {
			fragment.asyncData = arrayListOf(
				TraderMemorySalesRecordAndLargeSingleRecordListModel(),
				TraderMemorySalesRecordAndLargeSingleRecordListModel(),
				TraderMemorySalesRecordAndLargeSingleRecordListModel()
			)
		} otherwise {
			diffAndUpdateSingleCellAdapterData<TraderMemorySalesRecordAndLargeSingleRecordListAdapter>(arrayListOf(
				TraderMemorySalesRecordAndLargeSingleRecordListModel(),
				TraderMemorySalesRecordAndLargeSingleRecordListModel(),
				TraderMemorySalesRecordAndLargeSingleRecordListModel()
			))
		}
	}
}