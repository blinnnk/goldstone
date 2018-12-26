package io.goldstone.blockchain.module.home.rammarket.module.ramtransactionsearch.view

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BottomLoadingView
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.utils.*
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
	
	override val pageTitle: String = EOSRAMExchangeText.ramExchange
	override lateinit var presenter: RAMTransactionSearchPresenter
	private var bottomLoading: BottomLoadingView? = null
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val account = arguments?.getString("account")
		asyncData = arrayListOf()
		presenter = RAMTransactionSearchPresenter(this, account)
		getParentFragment<RAMMarketOverlayFragment> {
			searchInputListener {
				if (it.length == 12 && NetworkUtil.hasNetwork()) {
					this@RAMTransactionSearchFragment.presenter.loadFirstPage(it)
				}
			}
		}
		getParentFragment<RAMMarketOverlayFragment> {
			if (account != null && account.isNotEmpty()) {
				getContainer().header.setSearchText(account)
			}
			getContainer().header.setSearchEditor(EditorInfo.IME_ACTION_SEARCH) { accountName ->
				this@RAMTransactionSearchFragment.presenter.loadFirstPage(accountName)
			}
		}
		activity?.window?.apply {
			setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
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
			showSearchInput(false, { }) { }
		}
	}
}





