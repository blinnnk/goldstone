package io.goldstone.blockchain.module.home.rammarket.module.ramtransactionsearch.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.*
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailHeaderView
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import io.goldstone.blockchain.module.home.rammarket.module.ramtransactionsearch.contract.RAMTransactionSearchContract
import io.goldstone.blockchain.module.home.rammarket.module.ramtransactionsearch.presenter.RAMTransactionSearchPresenter
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketOverlayFragment
import java.util.*

/**
 * @date 21/04/2018 4:31 PM
 * @author yangLiHai
 */

class RAMTransactionSearchFragment:
	GSRecyclerFragment<TradingInfoModel>(), RAMTransactionSearchContract.GSView {
	
	override fun showError(error: Throwable) {
		ErrorDisplayManager(error).show(context)
	}
	
	override val pageTitle: String = "Quotation Search"
	override lateinit var presenter: RAMTransactionSearchPresenter
	private var bottomLoading: BottomLoadingView? = null
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val account = arguments?.getString("account")
		asyncData = arrayListOf()
		presenter = RAMTransactionSearchPresenter(this, account)
		getParentFragment<RAMMarketOverlayFragment> {
			searchInputListener {
				if (NetworkUtil.hasNetwork(context)) {
					this@RAMTransactionSearchFragment.presenter.account = it
					this@RAMTransactionSearchFragment.presenter.loadFirstPage()
				}
			}
		}
		if (account != null && account.isNotEmpty()) {
			getParentFragment<RAMMarketOverlayFragment> {
				getOverlayHeader().setSearchText(account)
			}
		}
	}
	override fun showLoading(status: Boolean) {
			super.showLoadingView(status)
	}
	override fun showBottomLoading(status: Boolean) {
		if (status) bottomLoading?.show()
		else bottomLoading?.hide()
		isLoadingData = false
	}
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<TradingInfoModel>?
	) {
		recyclerView.adapter = TransactionsOfNameAdapter(asyncData.orEmptyArray(), {}) {
			bottomLoading = this
		}
	}
	
	override fun notifyUI(isClear: Boolean, newData: ArrayList<TradingInfoModel>) {
		if (isClear) asyncData?.clear()
		asyncData!!.addAll(newData)
		recyclerView.adapter?.notifyDataSetChanged()
		if (asyncData!!.size>0) {
			removeEmptyView()
		}
	}
	override fun flipPage() {
		super.flipPage()
		presenter.loadMore()
	}

	override fun recoveryBackEvent() {
		getMainActivity()?.apply {
			setBackEvent(this)
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		getParentFragment<RAMMarketOverlayFragment> {
			headerTitle = QuotationText.management
			presenter.popFragmentFrom<RAMTransactionSearchFragment>()
			getOverlayHeader().showSearchInput(false) {}
		}
	}
}





