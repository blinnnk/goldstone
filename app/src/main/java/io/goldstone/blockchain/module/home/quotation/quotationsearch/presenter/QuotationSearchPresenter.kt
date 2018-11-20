package io.goldstone.blockchain.module.home.quotation.quotationsearch.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.forEachOrEnd
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.orEmpty
import com.blinnnk.util.load
import com.blinnnk.util.then
import com.google.gson.JsonArray
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.quotationsearch.contract.QuotationSearchContract
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @date 21/04/2018 4:32 PM
 * @author KaySaith
 */
class QuotationSearchPresenter(
	private val detailView: QuotationSearchContract.GSView
) : QuotationSearchContract.GSPresenter {

	private var selectedIds = listOf<Int>()

	override fun start() {
		getSelectedExchange { exchanges ->
			selectedIds = exchanges.map { it.marketId }
			detailView.showFilterDescription(exchanges)
		}
	}

	override fun updateSelectedExchangeID(ids: List<Int>) {
		selectedIds = ids
	}

	@WorkerThread
	override fun updateLocalQuotation(
		model: QuotationSelectionTable,
		isSelect: Boolean,
		callback: (error: GoldStoneError) -> Unit
	) {
		// 如果选中, 拉取选中的 `token` 的 `lineChart` 信息
		val parameter = JsonArray().apply { add(model.pair) }
		if (isSelect) GoldStoneAPI.getCurrencyLineChartData(parameter) { chartData, error ->
			if (chartData.isNotNull() && error.isNone()) {
				QuotationSelectionTable.insertSelection(
					QuotationSelectionTable(
						model,
						chartData.firstOrNull()?.pointList?.toString().orEmpty(),
						isSelect
					)
				)
				callback(error)
			} else callback(error)
		} else GlobalScope.launch(Dispatchers.Default) {
			QuotationSelectionTable.dao.deleteByPairs(model.pair)
			callback(RequestError.None)
		}
	}

	override fun getSelectedExchange(hold: (List<ExchangeTable>) -> Unit) {
		load { ExchangeTable.dao.getSelected() } then { hold(it) }
	}

	override fun searchToken(symbol: String) {
		with(detailView) {
			showLoading(true)
			GoldStoneAPI.getMarketSearchList(symbol, selectedIds.joinToString(",")) { searchList, error ->
				if (!searchList.isNullOrEmpty() && error.isNone()) {
					val targetData =
						QuotationSelectionTable.dao.getTargetMarketTables(selectedIds)
					// 如果本地没有已经选中的直接返回搜索的数据展示在界面
					// 否则用搜索的记过查找是否有已经选中在本地的, 更改按钮的选中状态
					if (targetData.isEmpty()) {
						showLoading(false)
						updateUI(searchList)
					} else searchList.forEachOrEnd { item, isEnd ->
						item.isSelecting = targetData.any { it.pair == item.pair }
						if (isEnd) {
							showLoading(false)
							updateUI(searchList)
						}
					}
				} else {
					if (error.hasError()) showError(error)
					showLoading(false)
				}
			}
		}
	}
}