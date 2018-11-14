package io.goldstone.blockchain.module.home.rammarket.module.ramtransactionsearch.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import io.goldstone.blockchain.module.home.rammarket.module.ramtransactionsearch.view.*
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketOverlayFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 21/04/2018 4:32 PM
 * @author yangLiHai
 */
class RAMTransactionSearchPresenter(
	override val fragment: RAMTransactionSearchFragment
) : BaseRecyclerPresenter<RAMTransactionSearchFragment, TradingInfoModel>() {
	
	private var account: String? = null
	private var endID = 0
	
	override fun updateData() {
		fragment.asyncData = arrayListOf()
	}
	
	override fun onFragmentCreate() {
		super.onFragmentCreate()
		account = fragment.arguments?.getString("account")
	}

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.getParentFragment<RAMMarketOverlayFragment> {
			searchInputListener {
				if (NetworkUtil.hasNetwork(context))   loadFirstPage(it)
			}
		}
		if (account != null && account!!.isNotEmpty()) {
			fragment.getParentFragment<RAMMarketOverlayFragment> {
				getOverlayHeader().setSearchText(account!!)
			}
		}
		
	}
	
	private fun searchByName(account: String, @UiThread callback: () -> Unit) {
		doAsync {
			GoldStoneAPI.getEOSRAMTransactionsByAccount(account, endID) { data, error ->
				if (data != null && error.isNone()) {
					data.forEach {
						it.account = account
					}
					GoldStoneAPI.context.runOnUiThread {
						if (fragment.asyncData == null) {
							fragment.setRecyclerViewAdapter(fragment.recyclerView, data.toArrayList())
						} else {
							if (endID == 0) fragment.asyncData!!.clear()
							fragment.asyncData!!.addAll(data.toArrayList())
							fragment.getAdapter<TransactionsOfNameAdapter>()?.notifyDataSetChanged()
						}
						if (fragment.asyncData != null && fragment.asyncData!!.size>0) {
							fragment.removeEmptyView()
						}
						data.isNotEmpty() isTrue {
							endID = data[data.lastIndex].id
						}
						callback()
					}
				} else {
					GoldStoneAPI.context.runOnUiThread {
						fragment.context?.alert(error.message)
						callback()
					}
				}
			}
		}
	}
	
	private fun loadFirstPage(account: String) {
		endID = 0
		fragment.showLoadingView()
		searchByName(account) {
			fragment.removeLoadingView()
		}
	}
	
	override fun loadMore() {
		super.loadMore()
		account?.apply {
			searchByName(this) {
//				showBottomLoading(false)
			}
		}
	}
	
}