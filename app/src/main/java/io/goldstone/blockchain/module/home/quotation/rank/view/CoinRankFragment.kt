package io.goldstone.blockchain.module.home.quotation.rank.view

import android.os.Bundle
import android.view.*
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.module.home.quotation.rank.contract.CoinRankContract
import io.goldstone.blockchain.module.home.quotation.rank.model.CoinGlobalModel
import io.goldstone.blockchain.module.home.quotation.rank.model.CoinRankModel
import io.goldstone.blockchain.module.home.quotation.rank.presenter.CoinRankPresenter

/**
 * @date: 2018-12-12.
 * @author: yangLiHai
 * @description:
 */
class CoinRankFragment: GSRecyclerFragment<CoinRankModel>(), CoinRankContract.GSView {
	
	override val pageTitle: String = ""
	
	private var coinRankHeader: CoinRankHeader? = null
	
	override val presenter: CoinRankContract.GSPresenter = CoinRankPresenter(this)
	
	override fun showLoadingView(status: Boolean) {
		super.showLoadingView(status)
	}
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<CoinRankModel>?
	) {
		recyclerView.adapter = CoinRankAdapter(asyncData.orEmptyArray(),
			{
				coinRankHeader = this
			}) {
			
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		asyncData = arrayListOf()
		presenter.start()
	}
	
	override fun showError(error: Throwable) {
	
	}
	
	override fun showHeaderData(model: CoinGlobalModel) {
		coinRankHeader?.apply {
		
		}
	}
	
	override fun showListData(data: List<CoinRankModel>) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
	
	
	
}






