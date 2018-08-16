package io.goldstone.blockchain.module.home.quotation.rank.presenter

import android.widget.Toast
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.rank.model.RankTable
import io.goldstone.blockchain.module.home.quotation.rank.view.RankListAdapter
import io.goldstone.blockchain.module.home.quotation.rank.view.RankListFragment

/**
 * @date: 2018/8/14.
 * @author: yanglihai
 * @description:
 */
class RankPresenter(override val fragment: RankListFragment)
	: BaseRecyclerPresenter<RankListFragment, RankTable>() {
	
	var rank: String = ""
	var size: Int = 20
	
	fun getRankData() {
		GoldStoneAPI.getRankData(
			rank ,
			size ,
			{
				if (rank.isEmpty() ) {
					RankTable.queryRankData {
						rank = it[it.lastIndex].rank
						fragment.asyncData?.addAll(it)
						fragment.recyclerView.adapter!!.notifyDataSetChanged()
					}
				}else {
					Toast.makeText(GoldStoneAPI.context, it.toString(), Toast.LENGTH_LONG).show()
				}
			} ,
			{
				
				var rankNewData: List<RankTable> = it
				if (rank.isEmpty()) {
					fragment.asyncData?.clear()
					it.isEmpty() isTrue {
						RankTable.queryRankData {
							rankNewData = it
						}
					}otherwise  {
						insertFirstPageData(it)
					}
				}
				rank = rankNewData[rankNewData.lastIndex].rank
				fragment.asyncData!!.addAll(rankNewData)
				fragment.recyclerView.adapter?.notifyDataSetChanged()
			}
		)
	}
	
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.asyncData = arrayListOf()
	}
	
	
	override fun onFragmentResume() {
		rank = ""
		getRankHeaderData()
		getRankData()
	}
	
	override fun updateData() {
		getRankData()
	}
	
	fun getRankHeaderData() {
		GoldStoneAPI.getRandHeader( {
				LogUtil.error("RankPresenter", it)
		}, {
			(fragment.recyclerView.adapter as? RankListAdapter)!!.updateRankHeaderViewData(it)
		})
	}
	
	private fun insertFirstPageData(rankList: List<RankTable>?) {
		rankList?.apply {
			isNotEmpty() isTrue {
				RankTable.clearRankTable {
					RankTable.insertRankList(rankList) {
					
					}
				}
				
			}
		}
		
	}
	
}