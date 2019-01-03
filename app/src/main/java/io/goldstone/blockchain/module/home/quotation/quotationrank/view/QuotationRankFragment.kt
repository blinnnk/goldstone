package io.goldstone.blockchain.module.home.quotation.quotationrank.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.module.home.quotation.quotationrank.contract.QuotationRankContract
import io.goldstone.blockchain.module.home.quotation.quotationrank.model.QuotationRankModel
import io.goldstone.blockchain.module.home.quotation.quotationrank.presenter.QuotationRankPresenter


/**
 * @author KaySaith
 * @date  2019/01/02
 */
class QuotationRankFragment : GSRecyclerFragment<QuotationRankModel>(), QuotationRankContract.GSView {

	override val pageTitle: String = "Quotation Rank"
	override lateinit var presenter: QuotationRankContract.GSPresenter

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		asyncData = arrayListOf()
		presenter = QuotationRankPresenter(this)
		presenter.start()
	}

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<QuotationRankModel>?
	) {
		recyclerView.adapter = QuotationRankAdapter(asyncData.orEmptyArray()) {
			showQuotationDetailFragment()
		}
	}

	override fun updateAdapterDataSet(data: List<QuotationRankModel>) = launchUI {
		updateAdapterData<QuotationRankAdapter>(data.toArrayList())
	}

	override fun showMarketInfo() {
		// TODO
	}

	override fun flipPage() {
		super.flipPage()
		presenter.loadMore()
	}

	override fun showError(error: Throwable) {
		ErrorDisplayManager(error).show(context)
	}

	private fun showQuotationDetailFragment() {
		// TODO
	}

}