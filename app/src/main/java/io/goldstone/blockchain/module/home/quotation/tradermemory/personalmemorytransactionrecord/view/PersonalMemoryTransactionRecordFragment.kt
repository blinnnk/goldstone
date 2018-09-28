package io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.view

import android.annotation.SuppressLint
import com.blinnnk.extension.*
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.quotation.tradermemory.TraderMemoryDetailOverlay.view.TraderMemoryOverlayFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.model.PersonalMemoryTransactionRecordTable
import io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.presenter.PersonalMemoryTransactionRecordPresenter
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.view.TraderMemoryDetailFragment

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
@SuppressLint("ValidFragment")
class PersonalMemoryTransactionRecordFragment() :
	BaseRecyclerFragment<PersonalMemoryTransactionRecordPresenter, PersonalMemoryTransactionRecordTable>() {
	override val pageTitle: String
		get() = "个人交易"
	private val account by lazy { arguments?.getString("account") }
	private val isSalesRecord by lazy { arguments?.getBoolean("isSalesRecord") }
	override val presenter: PersonalMemoryTransactionRecordPresenter = PersonalMemoryTransactionRecordPresenter(
		this,
		isSalesRecord.orFalse()
	)

	override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<PersonalMemoryTransactionRecordTable>?) {
		recyclerView.adapter = PersonalMemoryTransactionRecordAdapter(
			asyncData.orEmptyArray(),
			isSalesRecord.orFalse()
		) {
		}
	}

	fun getAccountName(): String {
		return account ?: ""
	}
	
	override fun setBackEvent(mainActivity: MainActivity?) {
//		super.setBackEvent(mainActivity)
		getParentFragment<BaseOverlayFragment<*>>()?.apply {
			presenter.popFragmentFrom<PersonalMemoryTransactionRecordFragment>()
		}
	}
}