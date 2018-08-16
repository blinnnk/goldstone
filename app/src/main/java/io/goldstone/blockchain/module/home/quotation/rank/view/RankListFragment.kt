package io.goldstone.blockchain.module.home.quotation.rank.view

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.quotation.rank.presenter.RankPresenter
import io.goldstone.blockchain.module.home.quotation.rank.model.RankTable

/**
 * @date: 2018/8/14.
 * @author: yanglihai
 * @description:
 */
class RankListFragment : BaseRecyclerFragment<RankPresenter, RankTable>() {
	override val presenter: RankPresenter = RankPresenter(
		this)
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<RankTable>?
	) {
		recyclerView.adapter = RankListAdapter(
			asyncData.orEmptyArray())
	}
	
	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(view, savedInstanceState)
		
		recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrollStateChanged(
				recyclerView: RecyclerView,
				newState: Int
			) {
				super.onScrollStateChanged(recyclerView, newState)
				
				if (newState == RecyclerView.SCROLL_STATE_IDLE){
					if ((recyclerView.layoutManager as? LinearLayoutManager)!!.findLastVisibleItemPosition() >= asyncData?.lastIndex!!-5){
						presenter.getRankData()
					}
				}
			}
		})
	}
	
	override fun setBackEvent(mainActivity: MainActivity?) {
		mainActivity?.getHomeFragment()?.presenter?.showWalletDetailFragment()
	}
	
	
}