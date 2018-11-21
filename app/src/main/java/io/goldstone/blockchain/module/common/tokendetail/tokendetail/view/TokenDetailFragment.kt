package io.goldstone.blockchain.module.common.tokendetail.tokendetail.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BottomLoadingView
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.common.component.overlay.Dashboard
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.contract.TokenDetailContract
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.event.FilterButtonDisplayEvent
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.event.TokenDetailEvent
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter.TokenDetailPresenter
import io.goldstone.blockchain.module.common.tokendetail.tokendetailcenter.view.TokenDetailCenterFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.quotation.quotation.model.ChartPoint
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
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
	private val filterConditions = listOf(
		TokenDetailText.totalReceived,
		TokenDetailText.totalSent,
		TokenDetailText.fee,
		TokenDetailText.failed
	)

	private var currentFilterConditions = listOf(
		TokenDetailText.totalReceived,
		TokenDetailText.totalSent,
		TokenDetailText.fee,
		TokenDetailText.failed
	)

	override fun removeEmptyView() = launchUI {
		super.removeEmptyView()
	}

	override fun showLoading(status: Boolean) = launchUI {
		super.showLoadingView(status)
	}

	override fun showBottomLoading(status: Boolean) = launchUI {
		if (status) bottomLoading?.show() else bottomLoading?.hide()
		isLoadingData = false
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
		asyncData = arrayListOf()
		overlayFragment?.showFilterButton(true) {
			showFilterDashboard()
		}
		token?.let {
			presenter = TokenDetailPresenter(it, this)
			presenter.start()
		}

		footer.into(wrapper)
		footer.apply {
			alignParentBottom()
			sendButton.onClick {
				overlayFragment?.presenter?.showAddressSelectionFragment()
			}
			receivedButton.onClick {
				overlayFragment?.presenter?.showDepositFragment()
			}
		}
	}

	private var attentionDashboard: Dashboard? = null
	override fun showFilterLoadMoreAttention(dataSize: Int) {
		if (attentionDashboard.isNull()) {
			attentionDashboard = Dashboard(context!!) {
				showAlert(
					"No Actions Found \n(DataSize: $dataSize Transactions)",
					"The number of pages currently loaded has not found relevant data, try to change the classification or slide up to load more."
				) {
					presenter.loadMore()
					attentionDashboard = null
				}
			}
		}
	}

	override fun filterData(data: List<TransactionListModel>?): List<TransactionListModel> {
		return when {
			currentFilterConditions.isEmpty() -> listOf()
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

	// Event Bus 的注册, 销毁 和 订阅时间的函数
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		EventBus.getDefault().register(this)
	}

	override fun onDestroy() {
		super.onDestroy()
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
				currentFilterConditions = conditions
				asyncData?.let {
					updateAdapterDataSet<TokenDetailAdapter>(filterData(Collections.synchronizedList(it)).toArrayList())
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