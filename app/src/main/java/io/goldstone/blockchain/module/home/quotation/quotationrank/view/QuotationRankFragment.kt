package io.goldstone.blockchain.module.home.quotation.quotationrank.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BottomLoadingView
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.quotation.quotationrank.contract.QuotationRankContract
import io.goldstone.blockchain.module.home.quotation.quotationrank.model.QuotationGlobalModel
import io.goldstone.blockchain.module.home.quotation.quotationrank.model.QuotationRankTable
import io.goldstone.blockchain.module.home.quotation.quotationrank.presenter.QuotationRankPresenter
import org.jetbrains.anko.backgroundColor


/**
 * @author KaySaith
 * @date  2019/01/02
 */
class QuotationRankFragment : GSRecyclerFragment<QuotationRankTable>(), QuotationRankContract.GSView {

	override val pageTitle: String = "Quotation Rank"
	
	private var headerView: QuotationRankHeaderView? = null
	
	private var bottomLoadingView: BottomLoadingView? = null
	
	override val presenter: QuotationRankContract.GSPresenter = QuotationRankPresenter(this)
	
	override fun showBottomLoading(isShow: Boolean) {
		if (isShow) bottomLoadingView?.show()
		else bottomLoadingView?.hide()
		isLoadingData = false
	}
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<QuotationRankTable>?
	) {
		recyclerView.adapter = QuotationRankAdapter(asyncData.orEmptyArray(),
			{
				headerView = this
			},
			{
				bottomLoadingView = this
			}) {
			
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		wrapper.backgroundColor = Spectrum.opacity2White
		asyncData = arrayListOf()
		presenter.start()
	}
	
	override fun showError(error: Throwable) {
		ErrorDisplayManager(error).show(context)
	}
	
	override fun showHeaderData(model: QuotationGlobalModel) {
		headerView?.model = model
	}
	
	override fun flipPage() {
		super.flipPage()
		presenter.loadMore()
	}
	
	override fun showListData(isClear: Boolean, data: List<QuotationRankTable>) {
		if (isClear) asyncData!!.clear()
		asyncData!!.addAll(data)
		recyclerView.adapter?.notifyDataSetChanged()
		if (asyncData!!.size > 0) {
			removeEmptyView()
		} else {
			showEmptyView()
		}
		
	}
	
	
	
}