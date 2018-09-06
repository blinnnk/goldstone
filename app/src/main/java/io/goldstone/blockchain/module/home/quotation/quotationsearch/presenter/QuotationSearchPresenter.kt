package io.goldstone.blockchain.module.home.quotation.quotationsearch.presenter

import android.widget.*
import com.blinnnk.extension.*
import com.blinnnk.util.SoftKeyboard
import com.google.gson.JsonArray
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.component.overlay.ContentScrollOverlayView
import io.goldstone.blockchain.common.language.*
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.entrance.starting.presenter.StartingPresenter
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.view.*
import org.jetbrains.anko.*

/**
 * @date 21/04/2018 4:32 PM
 * @author KaySaith
 */
class QuotationSearchPresenter(
	override val fragment: QuotationSearchFragment
) : BaseRecyclerPresenter<QuotationSearchFragment, QuotationSelectionTable>() {
	
	private var selectedIds = ""
	private var selectedStatusChangedList: ArrayList<Pair<Int, Boolean>> = arrayListOf()
	private var overlayViewData: ArrayList<ExchangeTable> = arrayListOf()
	
	
	
	override fun updateData() {
		fragment.asyncData = arrayListOf()
	}
	
	private var hasNetWork = true
	
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.getParentFragment<QuotationOverlayFragment> {
			overlayView.header.getFilterSearchInput().showFilterImage(true)
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
		getMarketList {
			selectedStatusChangedList.clear()
			overlayViewData.clear()
			overlayViewData.addAll(it)
			fragment.showSelectionListOverlayView(overlayViewData)
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
				selectedIds += "${exchangeTable.exchangeId},"
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
			run onlyTwoFilters@ {
				filterNames.forEachIndexed { index, item ->
					filterText += "$item,"
					if (index >= 1 || index == filterNames.lastIndex) {
						filterText  = filterText.substringBeforeLast(",") + (if (index >= 1) "..." else "")
						return@onlyTwoFilters
					}
				}
			}
			
			if (filterText.isEmpty()) {
				fragment.removeExchangeFilterDescriptionView()
			} else {
				fragment.showExchangeFilterDescriptionView(QuotationText.searchFilterTextDescription(filterText))
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
		GoldStoneAPI.getMarketSearchList(symbol, selectedIds, {
			// Show error information to user
			fragment.context.alert(it.toString().showAfterColonContent())
		}) { searchList ->
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
	
	private fun QuotationSearchFragment.showSelectionListOverlayView(data: ArrayList<ExchangeTable>) {
		getMainActivity()?.getMainContainer()?.apply {
			if (findViewById<ContentScrollOverlayView>(ElementID.contentScrollview).isNull()) {
				val overlay = ContentScrollOverlayView(context)
				overlay.into(this)
				overlay.apply {
					setTitle(TransactionText.tokenSelection)
					addContent {
						var singleCheckClick = false
						val exchangeRecyclerView = ExchangeRecyclerView(context)
						addView(exchangeRecyclerView, 0)
						val exchangeAdater = ExchangeAdapter(data) { markeSetCell ->
							markeSetCell.checkBox.setOnCheckedChangeListener { _, isChecked ->
								singleCheckClick = true
								markeSetCell.model?.apply {
									isSelected = isChecked
									updateSelectedChanged(exchangeId, isSelected)
									updateSelectAllStatus(overlay.findViewById(ElementID.checkBox))
									singleCheckClick = false
								}
							}
						}
						exchangeRecyclerView.adapter = exchangeAdater
						val allCheckBox = CompoundButton.OnCheckedChangeListener {
								_, isChecked ->
							if (!singleCheckClick) {
								data.forEach {
									it.isSelected = isChecked
									updateSelectedChanged(it.exchangeId, it.isSelected)
								}
								exchangeAdater.notifyDataSetChanged()
							}
						}
						
						val exchangeFilterBottomBar = ExchangeFilterDashboardBottomBar(maxWidth, context).apply {
							setEvents(allCheckBox) {
								selectedStatusChangedList.forEach {
									ExchangeTable.updateSelectedStatusById(it.first, it.second)
								}
								getSelectedIdsAndExchangeName(data)
								selectedStatusChangedList.clear()
								overlay.remove()
								updateResultAfterConditionChanged()
							}
						}
						showConfirmButton (exchangeFilterBottomBar)
					}
					
					updateSelectAllStatus(overlay.findViewById(ElementID.checkBox))
				}
				
			}
			// 重置回退栈首先关闭悬浮层
			recoveryBackEvent()
		}
	}
	
	private fun updateResultAfterConditionChanged() {
		fragment.getParentFragment<QuotationOverlayFragment> {
			val textForSearch = overlayView.header.getFilterSearchInput().editText.text.toString()
			if (hasNetWork &&
				!textForSearch.isBlank()
			) {
				searchTokenBy(textForSearch)
			}
		}
	}
	
	private fun updateSelectAllStatus(checkBox: CheckBox) {
		overlayViewData.filterNot {
			it.isSelected
		}.apply {
			checkBox.isChecked = isEmpty()
		}
	}
	
	private fun updateSelectedChanged(id: Int, isSelected: Boolean) {
		selectedStatusChangedList.forEach {
			if (it.first == id) {
				selectedStatusChangedList.remove(it)
				selectedStatusChangedList.add(Pair(id, isSelected))
				return
			}
		}
		selectedStatusChangedList.add(Pair(id, isSelected))
	}
	
	
	companion object {
		fun getLineChartDataByPair(
			pair: String,
			hold: (String) -> Unit
		) {
			val parameter = JsonArray().apply { add(pair) }
			GoldStoneAPI.getCurrencyLineChartData(parameter, {
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
		
		fun getMarketList(callback: (ArrayList<ExchangeTable>) -> Unit) {
			ExchangeTable.getAll { it ->
				if (it.isEmpty()) {
					//数据库没有数据，从网络获取
					StartingPresenter.updateExchangesTables ( {
						LogUtil.error(it.toString())
					}) {
						getMarketList(callback)
					}
				} else {
					//数据库有数据
					GoldStoneAPI.context.runOnUiThread {
						callback(it.toArrayList())
					}
				}
			}
			
		}
	}
}