package io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecordandlargesinglerecordlist.view

import android.annotation.SuppressLint
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecordandlargesinglerecordlist.model.TraderMemorySalesRecordAndLargeSingleRecordListModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecordandlargesinglerecordlist.presenter.TraderMemorySalesRecordAndLargeSingleRecordListPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
@SuppressLint("ValidFragment")
class TraderMemorySalesRecordAndLargeSingleRecordListFragment(private val isSalesRecord: Boolean) :
	BaseRecyclerFragment<TraderMemorySalesRecordAndLargeSingleRecordListPresenter, TraderMemorySalesRecordAndLargeSingleRecordListModel>() {
	override val presenter: TraderMemorySalesRecordAndLargeSingleRecordListPresenter = TraderMemorySalesRecordAndLargeSingleRecordListPresenter(
		this,
		isSalesRecord
	)

	override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<TraderMemorySalesRecordAndLargeSingleRecordListModel>?) {
		recyclerView.adapter = TraderMemorySalesRecordAndLargeSingleRecordListAdapter(
			asyncData.orEmptyArray(),
			isSalesRecord
		) {
		}
	}

}