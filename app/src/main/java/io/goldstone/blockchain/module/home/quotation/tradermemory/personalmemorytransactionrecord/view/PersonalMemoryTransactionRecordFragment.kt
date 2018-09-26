package io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.view

import android.annotation.SuppressLint
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.model.PersonalMemoryTransactionRecordTable
import io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.presenter.PersonalMemoryTransactionRecordPresenter

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
@SuppressLint("ValidFragment")
class PersonalMemoryTransactionRecordFragment(private val isSalesRecord: Boolean) :
	BaseRecyclerFragment<PersonalMemoryTransactionRecordPresenter, PersonalMemoryTransactionRecordTable>() {
	override val presenter: PersonalMemoryTransactionRecordPresenter = PersonalMemoryTransactionRecordPresenter(
		this,
		isSalesRecord
	)

	override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<PersonalMemoryTransactionRecordTable>?) {
		recyclerView.adapter = PersonalMemoryTransactionRecordAdapter(
			asyncData.orEmptyArray(),
			isSalesRecord
		) {
		}
	}

}