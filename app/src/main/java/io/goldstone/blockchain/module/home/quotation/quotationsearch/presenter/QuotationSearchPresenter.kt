package io.goldstone.blockchain.module.home.quotation.quotationsearch.presenter

import com.blinnnk.extension.*
import com.google.gson.JsonArray
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.showAfterColonContent
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.view.QuotationSearchAdapter
import io.goldstone.blockchain.module.home.quotation.quotationsearch.view.QuotationSearchFragment
import org.jetbrains.anko.runOnUiThread

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
	
	companion object {
		fun getLineChartDataByPair(pair: String, hold: (String) -> Unit) {
			val parameter = JsonArray().apply { add(pair) }
			GoldStoneAPI.getCurrencyLineChartData(parameter) {
				it.isNotEmpty() isTrue {
					hold(it[0].pointList.toString())
				} otherwise {
					LogUtil.error("Empty pair data from server")
				}
			}
		}
	}
}