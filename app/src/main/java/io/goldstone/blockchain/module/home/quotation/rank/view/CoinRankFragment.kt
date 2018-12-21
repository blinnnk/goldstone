package io.goldstone.blockchain.module.home.quotation.rank.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BottomLoadingView
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.quotation.rank.contract.CoinRankContract
import io.goldstone.blockchain.module.home.quotation.rank.model.CoinGlobalModel
import io.goldstone.blockchain.module.home.quotation.rank.model.CoinRankModel
import io.goldstone.blockchain.module.home.quotation.rank.presenter.CoinRankPresenter
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.bottomPadding

/**
 * @date: 2018-12-12.
 * @author: yangLiHai
 * @description:
 */
class CoinRankFragment: GSRecyclerFragment<CoinRankModel>(), CoinRankContract.GSView {
	
	override val pageTitle: String = ""
	
	private var coinRankHeader: CoinRankHeader? = null
	
	private var bottomLoadingView: BottomLoadingView? = null
	
	override val presenter: CoinRankContract.GSPresenter = CoinRankPresenter(this)
	
	override fun showBottomLoading(isShow: Boolean) {
		if (isShow) bottomLoadingView?.show()
		else bottomLoadingView?.hide()
		isLoadingData = false
	}
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<CoinRankModel>?
	) {
		recyclerView.adapter = CoinRankAdapter(asyncData.orEmptyArray(),
			{
				coinRankHeader = this
			},
			{
				bottomLoadingView = this
			}) {
			
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		wrapper.bottomPadding = 50.uiPX()
		wrapper.backgroundColor = Spectrum.opacity2White
		asyncData = arrayListOf()
		presenter.start()
	}
	
	override fun showError(error: Throwable) {
		ErrorDisplayManager(error).show(context)
	}
	
	override fun showHeaderData(model: CoinGlobalModel) {
		coinRankHeader?.model = model
	}
	
	override fun flipPage() {
		super.flipPage()
		presenter.loadMore()
	}
	
	override fun showListData(isClear: Boolean, data: List<CoinRankModel>) {
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






