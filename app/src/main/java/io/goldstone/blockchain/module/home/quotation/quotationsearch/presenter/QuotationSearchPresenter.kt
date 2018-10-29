package io.goldstone.blockchain.module.home.quotation.quotationsearch.presenter

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.widget.CheckBox
import com.blinnnk.extension.*
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orEmpty
import com.blinnnk.extension.toArrayList
import com.google.gson.JsonArray
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.component.overlay.ContentScrollOverlayView
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.language.*
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.entrance.starting.presenter.StartingPresenter
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 21/04/2018 4:32 PM
 * @author KaySaith
 */
class QuotationSearchPresenter(
	override val fragment: QuotationSearchFragment
) : BaseRecyclerPresenter<QuotationSearchFragment, QuotationSelectionTable>() {
	
	private var selectedIds = ""
	// `ExchangeID` 是交易所列表对应的角标, `Boolean` 是用户选择对应角标的选择状态
	private var selectedStatusChangedList: MutableList<Pair<Int, Boolean>> = arrayListOf()
	private var exchangeListInMemory: ArrayList<ExchangeTable> = arrayListOf()
	
	override fun updateData() {
		fragment.asyncData = arrayListOf()
	}
	
	private var hasNetWork = true
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.getParentFragment<QuotationOverlayFragment> {
			overlayView.header.getFilterSearchInput().showFilterImage(true)
			overlayView.header.searchInputListener({
				// 在 `Input` focus 的时候就进行网络判断, 移除在输入的时候监听的不严谨提示.
				if (it) hasNetWork = NetworkUtil.hasNetworkWithAlert(context)
			}) {
				hasNetWork isTrue { searchTokenBy(it) }
			}
			overlayView.header.setSearchFilterClickEvent {
				showExchangeDashboard()
			}
		}
		getSearchFilters()
	}
	
	private fun showExchangeDashboard() {
		fragment.activity?.apply {
			SoftKeyboard.hide(this)
		}
		getExchangeList {
			selectedStatusChangedList.clear()
			exchangeListInMemory.clear()
			exchangeListInMemory.addAll(it)
			fragment.showExchangeDashboard(exchangeListInMemory)
		}
	}
	
	private fun getSearchFilters() {
		ExchangeTable.getMarketsBySelectedStatus(true) {
			getSelectedIdsAndExchangeName(it)
		}
	}
	
	private fun getSelectedIdsAndExchangeName(data: List<ExchangeTable>) {
		selectedIds = ""
		val selectedNames = arrayListOf<String>()
		data.forEach { exchangeTable ->
			if (exchangeTable.isSelected) {
				selectedIds += "${exchangeTable.marketId},"
				selectedNames.add(exchangeTable.exchangeName)
			}
		}
		if (selectedIds.isNotEmpty()) {
			selectedIds = selectedIds.substringBeforeLast(',')
		}
		fragment.getParentFragment<QuotationOverlayFragment> {
			overlayView.header.resetFilterStatus(selectedIds.isNotEmpty())
		}
		
		showExchangeFilterDescriptionBy(selectedNames)
		
	}
	
	private fun showExchangeFilterDescriptionBy(filterNames: List<String>) {
		fragment.apply {
			var filterText = ""
			run onlyTwoFilters@{
				filterNames.forEachIndexed { index, item ->
					filterText += "$item,"
					if (index >= 1 || index == filterNames.lastIndex) {
						filterText = filterText.substringBeforeLast(",")
						return@onlyTwoFilters
					}
				}
			}
			
			if (filterText.isEmpty()) {
				fragment.removeExchangeFilterDescriptionView()
			} else {
				fragment.showExchangeFilterDescriptionView(
					QuotationText.searchFilterTextDescription(
						if (filterNames.size > 2) QuotationText.searchExchangesNames(filterText)
						else filterText
					)
				)
			}
		}
	}
	
	fun updateMyQuotation(
		model: QuotationSelectionTable,
		isSelect: Boolean,
		callback: (error: GoldStoneError) -> Unit
	) {
		// 如果选中, 拉取选中的 `token` 的 `lineChart` 信息
		if (isSelect) getLineChartDataByPair(model.pair) { chartData, error ->
			if (!chartData.isNull() && error.isNone()) {
				QuotationSelectionTable.insertSelection(model.apply {
					lineChartDay = chartData!!
					isSelecting = isSelect
				})
				callback(error)
			} else callback(error)
		} else load {
			GoldStoneDataBase.database.quotationSelectionDao().deleteByPairs(model.pair)
		} then {
			callback(RequestError.None)
		}
	}
	
	private fun searchTokenBy(symbol: String) {
		fragment.showLoadingView(LoadingText.searchingQuotation)
		// 拉取搜索列表
		GoldStoneAPI.getMarketSearchList(symbol, selectedIds) { searchList, error ->
			if (!searchList.isNull() && error.isNone()) {
				if (searchList!!.isEmpty()) {
					fragment.context?.runOnUiThread { fragment.removeLoadingView() }
					return@getMarketSearchList
				}
				val localTargetMarketData =
					GoldStoneDataBase.database.quotationSelectionDao().getTargetMarketTables(
						selectedIds.split(",").map { it.toIntOrNull().orZero() }
					)
				// 如果本地没有已经选中的直接返回搜索的数据展示在界面
				localTargetMarketData.isEmpty() isTrue {
					fragment.completeQuotationTable(searchList)
				} otherwise {
					// 否则用搜索的记过查找是否有已经选中在本地的, 更改按钮的选中状态
					searchList.forEachOrEnd { item, isEnd ->
						item.isSelecting = localTargetMarketData.any { it.pair == item.pair }
						if (isEnd) {
							fragment.completeQuotationTable(searchList)
						}
					}
				}
			} else {
				// Show error information to user
				fragment.context.alert(error.toString().showAfterColonContent())
			}
			
		}
	}
	
	private fun QuotationSearchFragment.completeQuotationTable(searchList: List<QuotationSelectionTable>) {
		context?.runOnUiThread {
			removeLoadingView()
			diffAndUpdateSingleCellAdapterData<QuotationSearchAdapter>(searchList.map {
				QuotationSelectionTable(it, "")
			}.toArrayList())
		}
	}
	
	private fun QuotationSearchFragment.showExchangeDashboard(data: ArrayList<ExchangeTable>) {
		getMainActivity()?.getMainContainer()?.apply {
			if (findViewById<ContentScrollOverlayView>(ElementID.contentScrollview).isNull()) {
				val overlay = ContentScrollOverlayView(context)
				overlay.into(this)
				overlay.apply {
					setTitle(TransactionText.tokenSelection)
					addContent {
						val exchangeRecyclerView = ExchangeRecyclerView(context)
						addView(exchangeRecyclerView, 0)
						val exchangeAdapter = ExchangeAdapter(data) { marketSetCell ->
							marketSetCell.checkBox.setOnCheckedChangeListener { _, isChecked ->
								marketSetCell.model?.apply {
									isSelected = isChecked
									updateSelectedStatus(marketId, isSelected)
									// 用户每次点击列表中的 CheckBox 都检测一下是否需要把 BottomBar
									// 的 CheckBox 更新为全选状态
									overlay.findViewById<CheckBox>(ElementID.checkBox).isChecked = data.filterNot{ it.isSelected }.isEmpty()
								}
							}
						}
						exchangeRecyclerView.adapter = exchangeAdapter
						showBottomBar(
							ExchangeFilterDashboardBottomBar(context).apply {
								// 点击确认事件
								confirmButtonClickEvent = Runnable {
									selectedStatusChangedList.forEach {
										ExchangeTable.updateSelectedStatusById(it.first, it.second)
									}
									getSelectedIdsAndExchangeName(data)
									selectedStatusChangedList.clear()
									overlay.remove()
									updateResultAfterConditionChanged()
								}
								// 全选事件
								checkAllEvent = Runnable {
									val checkBoxAll = overlay.findViewById<CheckBox>(ElementID.checkBox)
//									checkBoxAll.isChecked = !checkBoxAll.isChecked
									data.forEach {
										it.isSelected = checkBoxAll.isChecked
										updateSelectedStatus(it.marketId, it.isSelected)
									}
									exchangeAdapter.notifyDataSetChanged()
								}
							}
						)
					}
					// 初始化的时候判断是否是全部选中状态
					overlay.findViewById<CheckBox>(ElementID.checkBox).isChecked = data.filterNot{ it.isSelected }.isEmpty()
				}
				
			}
			// 重置回退栈首先关闭悬浮层
			recoveryBackEvent()
		}
	}
	
	private fun updateResultAfterConditionChanged() {
		fragment.getParentFragment<QuotationOverlayFragment> {
			val textForSearch = overlayView.header.getFilterSearchInput().editText.text.toString()
			if (hasNetWork && !textForSearch.isBlank()) {
				searchTokenBy(textForSearch)
			}
		}
	}
	
	private fun updateSelectedStatus(id: Int, isSelected: Boolean) {
		val targetData =
			selectedStatusChangedList.find { it.first == id }
		if (targetData.isNull()) {
			selectedStatusChangedList.add(Pair(id, isSelected))
		} else {
			selectedStatusChangedList.remove(targetData)
			selectedStatusChangedList.add(Pair(id, isSelected))
		}
	}
	
	
	companion object {
		fun getLineChartDataByPair(
			pair: String, @WorkerThread hold: (lineChar: String?, error: RequestError) -> Unit
		) {
			val parameter = JsonArray().apply { add(pair) }
			GoldStoneAPI.getCurrencyLineChartData(parameter) { lineData, error ->
				if (!lineData.isNull() && error.isNone()) {
					hold(lineData!!.firstOrNull()?.pointList?.toString().orEmpty(), RequestError.None)
				} else hold(null, error)
			}
		}
		
		fun getExchangeList(
			@UiThread hold: (exchangeTableList: ArrayList<ExchangeTable>) -> Unit
		) {
			doAsync {
				val localData = GoldStoneDataBase.database.exchangeTableDao().getAll()
				if (localData.isEmpty()) StartingPresenter.getAndUpdateExchangeTables { exchangeTables, error ->
					//数据库没有数据，从网络获取
					if (!exchangeTables.isNull() && error.isNone()) GoldStoneAPI.context.runOnUiThread {
						hold(exchangeTables!!)
					} else GoldStoneAPI.context.runOnUiThread {
						hold(arrayListOf())
					}
				} else GoldStoneAPI.context.runOnUiThread {
					//数据库有数据
					hold(localData.toArrayList())
				}
				
			}
			
		}
		
	}
}