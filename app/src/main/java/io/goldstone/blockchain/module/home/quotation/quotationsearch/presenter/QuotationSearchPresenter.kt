package io.goldstone.blockchain.module.home.quotation.quotationsearch.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orEmpty
import com.blinnnk.extension.toArrayList
import com.google.gson.JsonArray
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
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
			overlayView.header.searchInputListener(
				{
					// 在 `Input` focus 的时候就进行网络判断, 移除在输入的时候监听的不严谨提示.
					if (it) hasNetWork = NetworkUtil.hasNetworkWithAlert(context)
				}
			) {
				if (NetworkUtil.hasNetwork(context)) fragment.searchTokenBy(it)
			}
		}
	}

	@WorkerThread
	fun updateMyQuotation(
		model: QuotationSelectionTable,
		isSelect: Boolean,
		@WorkerThread callback: (error: GoldStoneError) -> Unit
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
		} else {
			GoldStoneDataBase.database.quotationSelectionDao().deleteByPairs(model.pair)
			callback(RequestError.None)
		}
	}

	private fun QuotationSearchFragment.searchTokenBy(symbol: String) {
		showLoadingView(LoadingText.searchingQuotation)
		// 拉取搜索列表
		GoldStoneAPI.getMarketSearchList(symbol) { searchList, error ->
			if (!searchList.isNull() && error.isNone()) QuotationSelectionTable.getAll { selectedList ->
				// 如果本地没有已经选中的直接返回搜索的数据展示在界面
				if (selectedList.isEmpty()) {
					completeQuotationTable(searchList!!)
				} else {
					// 否则用搜索的记过查找是否有已经选中在本地的, 更改按钮的选中状态
					searchList!!.map { data ->
						data.apply { isSelecting = selectedList.any { it.pair == data.pair } }
					}.apply {
						completeQuotationTable(this)
					}
				}
			} else GoldStoneAPI.context.runOnUiThread {
				context.alert(error.message)
				removeLoadingView()
			}
		}
	}

	private fun QuotationSearchFragment.completeQuotationTable(searchList: List<QuotationSelectionTable>) {
		val data = searchList.map { QuotationSelectionTable(it, "") }
		GoldStoneAPI.context.runOnUiThread {
			removeLoadingView()
			diffAndUpdateSingleCellAdapterData<QuotationSearchAdapter>(data.toArrayList())
		}
	}

	companion object {
		fun getLineChartDataByPair(
			pair: String,
			@WorkerThread hold: (lineChar: String?, error: RequestError) -> Unit
		) {
			val parameter = JsonArray().apply { add(pair) }
			GoldStoneAPI.getCurrencyLineChartData(parameter) { lineData, error ->
				if (!lineData.isNull() && error.isNone()) {
					hold(lineData!!.firstOrNull()?.pointList?.toString().orEmpty(), error)
				} else hold(null, error)
			}
		}
	}
}