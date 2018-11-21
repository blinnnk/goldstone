package io.goldstone.blockchain.module.home.quotation.quotationmanagement.view

import android.os.Bundle
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.event.PairUpdateEvent
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.presenter.QuotationManagementPresenter
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @date 21/04/2018 3:58 PM
 * @author KaySaith
 */
class QuotationManagementFragment :
	BaseRecyclerFragment<QuotationManagementPresenter, QuotationSelectionTable>() {

	override val pageTitle: String = QuotationText.management
	override val presenter = QuotationManagementPresenter(this)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		EventBus.getDefault().register(this)
	}

	override fun onDestroy() {
		super.onDestroy()
		presenter.updateQuotationDataChanged()
		presenter.notifyQuotationDataChanged()
		EventBus.getDefault().unregister(this)
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	fun updatePairListEvent(pairUpdateEvent: PairUpdateEvent) {
		if (pairUpdateEvent.needUpdate) presenter.updateData()
	}

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<QuotationSelectionTable>?
	) {
		recyclerView.adapter = QuotationManagementAdapter(asyncData.orEmptyArray()) { data, isChecked ->
			// 更新内存里面的数据防止复用的时候出错
			asyncData?.find { selection ->
				selection.pair.equals(data.pair, true)
			}?.isSelecting = isChecked
		}
	}

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		getParentFragment<QuotationOverlayFragment> {
			if (hidden) showSearchButton(false) {}
			else {
				showSearchButton(true) {
					presenter.showQuotationSearchFragment()
				}
				showCloseButton(true) {
					presenter.removeSelfFromActivity()
				}
			}
		}
	}
}