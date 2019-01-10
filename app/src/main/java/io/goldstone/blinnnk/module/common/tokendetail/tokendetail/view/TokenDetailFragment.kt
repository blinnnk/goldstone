package io.goldstone.blinnnk.module.common.tokendetail.tokendetail.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.*
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BottomLoadingView
import io.goldstone.blinnnk.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blinnnk.common.component.overlay.Dashboard
import io.goldstone.blinnnk.common.component.overlay.GoldStoneDialog
import io.goldstone.blinnnk.common.language.TokenDetailText
import io.goldstone.blinnnk.common.language.TransactionText
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.isEmptyThen
import io.goldstone.blinnnk.common.utils.safeShowError
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.common.value.UMengEvent
import io.goldstone.blinnnk.crypto.multichain.isEOSSeries
import io.goldstone.blinnnk.module.common.tokendetail.tokendetail.contract.TokenDetailContract
import io.goldstone.blinnnk.module.common.tokendetail.tokendetail.event.FilterButtonDisplayEvent
import io.goldstone.blinnnk.module.common.tokendetail.tokendetail.event.TokenDetailEvent
import io.goldstone.blinnnk.module.common.tokendetail.tokendetail.presenter.TokenDetailPresenter
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailcenter.view.TokenDetailCenterFragment
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import io.goldstone.blinnnk.module.home.quotation.quotation.model.ChartPoint
import io.goldstone.blinnnk.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blinnnk.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

/**
 * @date 27/03/2018 3:20 PM
 * @author KaySaith
 */
class TokenDetailFragment : GSRecyclerFragment<TransactionListModel>(), TokenDetailContract.GSView {
	// 首页的 `cell` 点击进入详情界面传入的 `Symbol`
	val token by lazy {
		(parentFragment as? TokenDetailCenterFragment)?.token
	}

	override val pageTitle: String get() = token?.symbol?.symbol.orEmpty()
	override lateinit var presenter: TokenDetailContract.GSPresenter
	private val footer by lazy { TokenDetailFooter(context!!) }
	private var headerView: TokenDetailHeaderView? = null
	private var bottomLoading: BottomLoadingView? = null
	private val overlayFragment by lazy {
		getGrandFather<TokenDetailOverlayFragment>()
	}
	private var filterConditions = listOf(
		TokenDetailText.totalReceived,
		TokenDetailText.totalSent,
		TokenDetailText.fee,
		TokenDetailText.failed
	)

	private var currentFilterConditions = listOf<String>()

	override fun removeEmptyView() = launchUI {
		super.removeEmptyView()
	}

	override fun showLoading(status: Boolean) = launchUI {
		super.showLoadingView(status)
	}

	override fun showBottomLoading(status: Boolean) = launchUI {
		isLoadingData = status
		if (status) bottomLoading?.show() else bottomLoading?.hide()
	}

	override fun setChartData(data: ArrayList<ChartPoint>) = launchUI {
		headerView?.setCharData(data)
	}

	override fun notifyDataRangeChanged(start: Int, count: Int) = launchUI {
		recyclerView.adapter?.notifyItemRangeChanged(start, count)
	}

	override fun getDetailAdapter(): TokenDetailAdapter? {
		return recyclerView.adapter as? TokenDetailAdapter
	}

	override fun flipPage() {
		super.flipPage()
		presenter.loadMore()
	}

	override fun showNetworkAlert() {
		context?.let {
			GoldStoneDialog(it).showNetworkStatus {}
		}
	}

	override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<TransactionListModel>?) {
		recyclerView.adapter = TokenDetailAdapter(
			asyncData.orEmptyArray(),
			{ showTransactionDetailFragment(it) },
			{
				headerView = this
			}) {
			bottomLoading = this
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		overlayFragment?.showFilterButton(true) {
			showFilterDashboard()
		}
		token?.let {
			// 初始化相关数据
			asyncData = arrayListOf()
			if (it.contract.isEOSSeries()) filterConditions = filterConditions.dropLast(2)
			currentFilterConditions = filterConditions
			// 初始化 `Presenter`
			presenter = TokenDetailPresenter(it, this)
			presenter.start()
		}

		footer.into(wrapper)
		footer.apply {
			alignParentBottom()
			sendButton.onClick {
				overlayFragment?.presenter?.showAddressSelectionFragment()
				UMengEvent.add(context, UMengEvent.Click.Common.send, UMengEvent.Page.tokenTransactionList)
			}
			receivedButton.onClick {
				overlayFragment?.presenter?.showDepositFragment()
				UMengEvent.add(context, UMengEvent.Click.Common.deposit, UMengEvent.Page.tokenTransactionList)
			}
		}
	}

	private var attentionDashboard: Dashboard? = null
	override fun showFilterLoadMoreAttention(dataSize: Int) {
		if (attentionDashboard.isNull()) {
			attentionDashboard = Dashboard(context!!) {
				showAlert(
					"${TransactionText.noActionsFound} \n${TransactionText.filterDataResource}",
					TransactionText.filterFoundNoItem,
					TransactionText.loadMore
				) {
					presenter.loadMore()
					attentionDashboard = null
				}
				cancelOnTouchOutside()
			}
		}
	}

	override fun filterData(data: List<TransactionListModel>?): List<TransactionListModel> {
		return when {
			currentFilterConditions.isEmpty() -> data ?: listOf()
			data.isNullOrEmpty() -> listOf()
			else -> try {
				// 翻页特别块的时候这里会出现数组线程不安全, 通过 `Try Catch` 容错
				data.filter {
					val booleans = listOf(
						it.isReceived && !it.isFee,
						!it.isReceived && !it.isFee,
						it.isFee,
						it.hasError
					)
					val indexes = currentFilterConditions.map { item ->
						filterConditions.indexOf(item)
					}
					val finalConditions = booleans.filterIndexed { index, _ ->
						indexes.contains(index)
					}
					finalConditions.reduceRight { b, acc -> b || acc }
				}
			} catch (error: Exception) {
				listOf<TransactionListModel>()
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

	override fun onStart() {
		super.onStart()
		EventBus.getDefault().register(this)
	}

	override fun onStop() {
		super.onStop()
		EventBus.getDefault().unregister(this)
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	fun updateTokenDetailEvent(updateEvent: TokenDetailEvent) {
		if (updateEvent.hasConfirmed) presenter.refreshData()
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	fun updateFilterDisplayEvent(updateEvent: FilterButtonDisplayEvent) {
		overlayFragment?.apply {
			if (updateEvent.status) showFilterButton(true) {
				showFilterDashboard()
			} else showFilterButton(false) {}
		}
	}

	private fun showFilterDashboard() {
		Dashboard(context!!) {
			showMultiChoice(
				TokenDetailText.filterConditions,
				filterConditions,
				currentFilterConditions.map { filterConditions.indexOf(it) }.toIntArray()
			) { conditions ->
				currentFilterConditions = conditions isEmptyThen currentFilterConditions isEmptyThen filterConditions
				asyncData?.let {
					val showData = filterData(it)
					if (showData.isEmpty()) showFilterLoadMoreAttention(asyncData?.size.orZero())
					updateAdapterDataSet<TokenDetailAdapter>(showData.toArrayList())
				}
			}
		}
	}

	private fun showTransactionDetailFragment(model: TransactionListModel) {
		val argument = Bundle().apply {
			putSerializable(ArgumentKey.transactionFromList, model)
		}
		overlayFragment?.presenter?.showTargetFragment<TransactionDetailFragment>(argument)
	}
}