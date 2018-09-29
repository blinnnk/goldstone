package io.goldstone.blockchain.module.home.quotation.tradermemory.ramrank.presenter

import android.os.Bundle
import com.blinnnk.extension.isNull
import com.blinnnk.util.getStringFromSharedPreferences
import com.blinnnk.util.saveDataToSharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.RAMTradeRefreshEvent
import io.goldstone.blockchain.module.home.quotation.tradermemory.RefreshReceiver
import io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.view.PersonalMemoryTransactionRecordFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramrank.model.EOSRAMRankModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramrank.view.*
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date: 2018/9/25.
 * @author: yanglihai
 * @description:
 */
class RankPresenter(override val fragment: RankFragment)
	: BasePresenter<RankFragment>(), RefreshReceiver {
	
	private var rankList = arrayListOf<EOSRAMRankModel>()
	
	private val rankKey = "eosRAMRAnk"
	
	override fun onFragmentCreate() {
		super.onFragmentCreate()
		RAMTradeRefreshEvent.register(this)
		
		fragment.context?.apply {
			try {
				val jsonData = getStringFromSharedPreferences(rankKey)
				val type = object : TypeToken<ArrayList<EOSRAMRankModel>>() {}.type
				rankList.addAll(Gson().fromJson(jsonData, type))
			}catch (error: Exception) {
				rankList.clear()
			}
		}
		
	}
	
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		getRank()
	}
	
	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		RAMTradeRefreshEvent.unRegister(this)
		if (rankList.isNotEmpty()) {
			fragment.context?.apply {
				saveDataToSharedPreferences(rankKey, Gson().toJson(rankList))
			}
		}
	}
	
	private fun getRank() {
		GoldStoneAPI.getEOSRAMRank { data, error ->
			if (error.isNone()) {
				data?.let {
					rankList.clear()
					rankList.addAll(it)
				}
			}
			GoldStoneAPI.context.runOnUiThread {
				fragment.ramRankView.updateRankUI()
			}
		}
	}
	
	private fun RAMRankView.updateRankUI() {
		if (rankRecyclerView.adapter.isNull()) {
			rankRecyclerView.adapter = RAMRankAdapter(rankList) { cell, _ ->
				cell.onClick {
					showPersonalTransaction(cell.model.account)
				}
			}
		} else {
			rankRecyclerView.adapter?.notifyDataSetChanged()
		}
		
		
	}
	
	fun showPersonalTransaction(account: String) {
		(fragment.parentFragment?.parentFragment as? BaseOverlayFragment<*>)?.apply {
			presenter.showTargetFragment<PersonalMemoryTransactionRecordFragment>(bundle = Bundle().apply {
				putString(
					"account",
					account
				) })
		}
		
	}
	
	override fun onReceive(any: Any) {
		if (any.toString() == "") {
			if (NetworkUtil.hasNetwork(fragment.context)){
				getRank()
			}
		}
	}
	
}