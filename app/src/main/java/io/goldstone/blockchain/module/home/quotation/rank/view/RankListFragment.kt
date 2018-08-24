package io.goldstone.blockchain.module.home.quotation.rank.view

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.RelativeLayout
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.quotation.rank.model.RankTable
import io.goldstone.blockchain.module.home.quotation.rank.presenter.RankPresenter
import org.jetbrains.anko.matchParent

/**
 * @date: 2018/8/14.
 * @author: yanglihai
 * @description: rank列表fragment
 */
class RankListFragment : BaseRecyclerFragment<RankPresenter, RankTable>() {
	override val presenter: RankPresenter = RankPresenter(this)
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<RankTable>?
	) {
		recyclerView.adapter = RankListAdapter(asyncData.orEmptyArray())
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
				
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					asyncData?.apply {
						if ((recyclerView.layoutManager as? LinearLayoutManager)!!.findLastVisibleItemPosition() >= lastIndex - 2) {
							if (size > 0) {
								presenter.getRankData()
							}
						}
					}
				}
			}
			
			override fun onScrolled(
				recyclerView: RecyclerView,
				dx: Int,
				dy: Int
			) {
				super.onScrolled(recyclerView, dx, dy)
				
				val itemView = recyclerView.layoutManager!!.findViewByPosition(1)
				itemView?.apply {
					if (itemView.top < 50.uiPX()) {
						if (topView.parent.isNull()) {
							wrapper.addView(topView, topViewLayoutParams)
						}
					} else {
						if (!topView.parent.isNull()) {
							wrapper.removeView(topView)
						}
					}
				}
				
			}
			
		})
		
	}
	
	private val topView by lazy {
		SuperHeaderBar(context!!)
	}
	
	private val topViewLayoutParams by lazy {
		RelativeLayout.LayoutParams(matchParent, 50.uiPX())
	}
	
	override fun setBackEvent(mainActivity: MainActivity?) {
		mainActivity?.getHomeFragment()?.presenter?.showWalletDetailFragment()
	}
	
	
}