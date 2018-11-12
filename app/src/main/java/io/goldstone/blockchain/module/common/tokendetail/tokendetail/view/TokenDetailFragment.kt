package io.goldstone.blockchain.module.common.tokendetail.tokendetail.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BottomLoadingView
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.contract.TokenDetailContract
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter.TokenDetailPresenter
import io.goldstone.blockchain.module.common.tokendetail.tokendetailcenter.view.TokenDetailCenterFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.quotation.quotation.model.ChartPoint
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 27/03/2018 3:20 PM
 * @author KaySaith
 */
class TokenDetailFragment : GSRecyclerFragment<TransactionListModel>(), TokenDetailContract.GSView {
	// 首页的 `cell` 点击进入详情界面传入的 `Symbol`
	val token by lazy {
		(parentFragment as? TokenDetailCenterFragment)?.token
	}
	override var currentMenu = CommonText.all

	override val pageTitle: String get() = token?.symbol?.symbol.orEmpty()
	override lateinit var presenter: TokenDetailContract.GSPresenter

	private val footer by lazy { TokenDetailFooter(context!!) }
	private var headerView: TokenDetailHeaderView? = null
	private var bottomLoading: BottomLoadingView? = null

	override fun showLoading(status: Boolean) {
		if (status) showLoadingView() else removeLoadingView()
	}

	override fun showBottomLoading(status: Boolean) {
		if (status) bottomLoading?.show() else bottomLoading?.hide()
		isLoadingData = false
	}

	override fun setChartData(data: ArrayList<ChartPoint>) {
		headerView?.setCharData(data)
	}

	override fun notifyDataRangeChanged(start: Int, count: Int) {
		recyclerView.adapter?.notifyItemRangeChanged(start, count)
	}

	override fun updateDataChange(data: ArrayList<TransactionListModel>) {
		updateAdapterData<TokenDetailAdapter>(data)
	}

	override fun getDetailAdapter(): TokenDetailAdapter? {
		return recyclerView.adapter as? TokenDetailAdapter
	}

	override fun flipPage() {
		super.flipPage()
		presenter.loadMore()
	}

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<TransactionListModel>?
	) {
		recyclerView.adapter = TokenDetailAdapter(
			asyncData.orEmptyArray(),
			{ showTransactionDetailFragment(it) },
			{
				headerView = this
				menu.getButton { button ->
					button.onClick {
						currentMenu = button.text.toString()
						when (button.text) {
							CommonText.all -> presenter.showAllData()
							CommonText.deposit -> presenter.showOnlyReceiveData()
							CommonText.send -> presenter.showOnlySentData()
							CommonText.failed -> presenter.showOnlyFailedData()
						}
						menu.selected(button.id)
						button.preventDuplicateClicks()
					}
				}
			}) {
			bottomLoading = this
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		token?.let {
			presenter = TokenDetailPresenter(it, this)
			presenter.start()
		}

		footer.into(wrapper)
		footer.apply {
			setAlignParentBottom()
			sendButton.onClick {
				showAddressSelectionFragment()
			}
			receivedButton.onClick {
				showDepositFragment()
			}
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		super.setBackEvent(mainActivity)
		mainActivity?.backEvent = null
	}

	override fun showError(error: Throwable) {
		safeShowError(error)
	}

	// 异步加载数据后, 防止用户切换到别的 `TAB` 用来自动复位的方法
	override fun setAllMenu() {
		headerView?.menu?.selected(0)
	}

	private fun showAddressSelectionFragment() {
		getGrandFather<TokenDetailOverlayFragment>()
			?.presenter?.showAddressSelectionFragment()
	}

	private fun showDepositFragment() {
		getGrandFather<TokenDetailOverlayFragment>()?.presenter
			?.showDepositFragment()
	}

	private fun showTransactionDetailFragment(model: TransactionListModel) {
		val argument = Bundle().apply {
			putSerializable(ArgumentKey.transactionFromList, model)
		}
		getGrandFather<TokenDetailOverlayFragment>()?.apply {
			presenter.showTargetFragment<TransactionDetailFragment>(argument)
		}
	}
}