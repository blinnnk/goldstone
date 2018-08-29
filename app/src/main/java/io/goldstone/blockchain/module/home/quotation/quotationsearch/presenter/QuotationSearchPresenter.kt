package io.goldstone.blockchain.module.home.quotation.quotationsearch.presenter

import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.google.gson.JsonArray
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.component.overlay.ContentScrollOverlayView
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.MarketSetTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.view.*
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenselectionlist.TokenSelectionRecyclerView
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.topPadding

/**
 * @date 21/04/2018 4:32 PM
 * @author KaySaith
 */
class QuotationSearchPresenter(
	override val fragment: QuotationSearchFragment
) : BaseRecyclerPresenter<QuotationSearchFragment, QuotationSelectionTable>() {

	override fun updateData() {
		fragment.asyncData = arrayListOf()
	}

	private var hasNetWork = true
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.getParentFragment<QuotationOverlayFragment> {
			overlayView.header.searchInputLinstener(
				{
					// 在 `Input` focus 的时候就进行网络判断, 移除在输入的时候监听的不严谨提示.
					if (it) {
						hasNetWork = NetworkUtil.hasNetworkWithAlert(context)
					}
				}
			) {
				hasNetWork isTrue { searchTokenBy(it) }
			}
			
			overlayView.header.setSearchFilterClick {
				val data = arrayListOf<MarketSetTable>()
				(1 until  100).forEachIndexed { _,_ ->
					data.add(MarketSetTable(0,
						"http://img3.duitang.com/uploads/item/201409/25/20140925100559_RviGZ.jpeg",
						"货币",
						1))
				}
				fragment.showSelectionListOverlayView(data)
			}
		}
	}

	fun setQuotationSelfSelection(
		model: QuotationSelectionTable,
		isSelect: Boolean = true,
		callback: () -> Unit
	) {
		isSelect isTrue {
			// 如果选中, 拉取选中的 `token` 的 `lineChart` 信息
			getLineChartDataByPair(model.pair) { chartData ->
				QuotationSelectionTable.insertSelection(model.apply {
					lineChartDay = chartData
					isSelecting = isSelect
				}) { callback() }
			}
		} otherwise {
			QuotationSelectionTable.removeSelectionBy(model.pair) { callback() }
		}
	}

	private fun searchTokenBy(symbol: String) {
		fragment.showLoadingView(LoadingText.searchingQuotation)
		// 拉取搜索列表
		GoldStoneAPI.getMarketSearchList(
			symbol,
			{
				// Show error information to user
				fragment.context?.alert(it.toString().showAfterColonContent())
			}
		) { searchList ->
			if (searchList.isEmpty()) {
				fragment.context?.runOnUiThread { fragment.removeLoadingView() }
				return@getMarketSearchList
			}
			// 获取本地自己选中的列表
			QuotationSelectionTable.getMySelections { selectedList ->
				// 如果本地没有已经选中的直接返回搜索的数据展示在界面
				selectedList.isEmpty() isTrue {
					fragment.completeQuotationTable(searchList)
				} otherwise {
					// 否则用搜索的记过查找是否有已经选中在本地的, 更改按钮的选中状态
					searchList.forEachOrEnd { item, isEnd ->
						item.isSelecting = selectedList.any { it.pair == item.pair }
						if (isEnd) {
							fragment.completeQuotationTable(searchList)
						}
					}
				}
			}
		}
	}

	private fun QuotationSearchFragment.completeQuotationTable(searchList: ArrayList<QuotationSelectionTable>) {
		context?.runOnUiThread {
			removeLoadingView()
			diffAndUpdateSingleCellAdapterData<QuotationSearchAdapter>(searchList.map {
				QuotationSelectionTable(it, "")
			}.toArrayList())
		}
	}
	private fun QuotationSearchFragment.showSelectionListOverlayView(data: ArrayList<MarketSetTable>) {
		getMainActivity()?.getMainContainer()?.apply {
			if (findViewById<ContentScrollOverlayView>(ElementID.contentScrollview).isNull()) {
				val overlay = ContentScrollOverlayView(context)
				overlay.into(this)
				overlay.apply {
					setTitle(TransactionText.tokenSelection)
					addContent {
						topPadding = 10.uiPX()
						val marketSetRecyclerView = MarketSetRecyclerView(context)
						marketSetRecyclerView.into(this)
						val marketSetAdapter = MarketSetAdapter(data) {
							click {}
						}
						marketSetRecyclerView.adapter = marketSetAdapter
					}
				}
			}
			// 重置回退栈首先关闭悬浮层
			recoveryBackEvent()
		}
	}
	

	companion object {
		fun getLineChartDataByPair(pair: String, hold: (String) -> Unit) {
			val parameter = JsonArray().apply { add(pair) }
			GoldStoneAPI.getCurrencyLineChartData(
				parameter,
				{
					LogUtil.error("getCurrencyLineChartData", it)
				}
			) {
				it.isNotEmpty() isTrue {
					hold(it.first().pointList.toString())
				} otherwise {
					LogUtil.error("Empty pair data from server")
				}
			}
		}
	}
}