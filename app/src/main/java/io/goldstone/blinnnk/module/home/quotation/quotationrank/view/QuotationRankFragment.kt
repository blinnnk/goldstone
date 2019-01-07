package io.goldstone.blinnnk.module.home.quotation.quotationrank.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BottomLoadingView
import io.goldstone.blinnnk.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blinnnk.common.language.CoinRankText
import io.goldstone.blinnnk.common.utils.ErrorDisplayManager
import io.goldstone.blinnnk.module.home.quotation.quotationrank.contract.QuotationRankContract
import io.goldstone.blinnnk.module.home.quotation.quotationrank.model.QuotationGlobalModel
import io.goldstone.blinnnk.module.home.quotation.quotationrank.model.QuotationRankTable
import io.goldstone.blinnnk.module.home.quotation.quotationrank.presenter.QuotationRankPresenter


/**
 * @author KaySaith
 * @date  2019/01/02
 */
class QuotationRankFragment : GSRecyclerFragment<QuotationRankTable>(), QuotationRankContract.GSView {
	
	override val pageTitle: String = CoinRankText.marketRankPageTitle
	private var headerView: QuotationRankHeaderView? = null
	private var bottomLoadingView: BottomLoadingView? = null
	override lateinit var presenter: QuotationRankContract.GSPresenter
	
	override fun showBottomLoading(isShow: Boolean) {
		if (isShow) bottomLoadingView?.show()
		else bottomLoadingView?.hide()
		isLoadingData = false
	}
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<QuotationRankTable>?
	) {
		recyclerView.adapter = QuotationRankAdapter(
			asyncData.orEmptyArray(),
			holdHeader = {
				headerView = this
				presenter.start()
			},
			holdFooter = {
				bottomLoadingView = this
			},
			holdClickAction = {}
		)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter = QuotationRankPresenter(this)
		asyncData = arrayListOf()
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
	
	override fun updateData(newData: List<QuotationRankTable>) {
		asyncData?.apply {
			addAll(newData)
			val dataSize = size
			if (dataSize > 0) {
				recyclerView.adapter?.notifyItemRangeChanged(dataSize - newData.size + 1, dataSize + 1)
				removeEmptyView()
			}
		}
	}
}