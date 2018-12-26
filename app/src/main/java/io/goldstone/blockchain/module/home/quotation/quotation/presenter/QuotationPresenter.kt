package io.goldstone.blockchain.module.home.quotation.quotation.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import com.blinnnk.util.load
import com.blinnnk.util.then
import com.google.gson.JsonArray
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.sandbox.SandBoxManager
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.GoldStoneWebSocket
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.common.value.ValueTag
import io.goldstone.blockchain.crypto.utils.daysAgoInMills
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.quotation.model.ChartPoint
import io.goldstone.blockchain.module.home.quotation.quotation.model.CurrencyPriceInfoModel
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationAdapter
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationFragment
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

/**
 * @date 26/03/2018 8:56 PM
 * @author KaySaith
 */

class QuotationPresenter(
	override val fragment: QuotationFragment
) : BaseRecyclerPresenter<QuotationFragment, QuotationModel>() {

	private var hasInitSocket = false
	private var hasCheckedPairDate = false
	private val invalidDatePairs = JsonArray()

	override fun updateData() {
		if (fragment.asyncData.isNull()) fragment.asyncData = arrayListOf()
		GlobalScope.launch(Dispatchers.Default) {
			val selections = QuotationSelectionTable.dao.getAll()
			updateQuotationList(selections)
		}
	}

	@WorkerThread
	fun updateQuotationList(selections: List<QuotationSelectionTable>) {
		selections.asSequence().map { selection ->
			var linechart = listOf<ChartPoint>()
			if (selection.lineChartDay.isNotEmpty())
				linechart = convertDataToChartData(selection.lineChartDay)
			// 如果有网络的情况下检查 `LineData` 是否有效
			if (
				NetworkUtil.hasNetwork() &&
				linechart.isNotEmpty() &&
				!hasCheckedPairDate
			) {
				linechart.filterInvalidDatePair(selection.pair)
			}
			QuotationModel(
				selection,
				ValueTag.emptyPrice,
				"0",
				linechart
			)
		}.sortedByDescending {
			it.orderID
		}.toList().let { quotations ->
			if (!hasCheckedPairDate) {
				updateInvalidDatePair(invalidDatePairs) {
					launchUI {
						updateData()
					}
				}
				hasCheckedPairDate = true
			}
			
			launchUI {
				// 更新 `UI`
				diffAndUpdateAdapterData<QuotationAdapter>(quotations.toArrayList())
				// 设定 `Socket` 并执行
				if (currentSocket.isNull()) setSocket {
					hasInitSocket = true
					currentSocket?.runSocket()
				} else subscribeSocket()
			}
		}
	}

	override fun onFragmentResume() {
		resetSocket()
	}

	fun resetSocket() {
		currentSocket?.let {
			if (!it.isSocketConnected()) {
				it.runSocket()
				subscribeSocket()
			}
		}
		if (currentSocket.isNull() && hasInitSocket) setSocket {
			currentSocket?.runSocket()
		}
	}

	private fun subscribeSocket() {
		// 更新 `Socket`
		val jsonArray = fragment.asyncData?.map { it.pair }?.toJsonArray()
		currentSocket?.sendMessage("{\"t\":\"sub_tick\", \"pair_list\":$jsonArray}")
	}


	private fun List<ChartPoint>.filterInvalidDatePair(pair: String) {
		/** 服务端传入的最近的时间会做 `减1` 处理, 从服务器获取的事件是昨天的事件. */
		val maxDate = maxBy { it.label.toLong() }?.label?.toLongOrNull() ?: 0L
		if (maxDate < 0.daysAgoInMills()) invalidDatePairs.add(pair)
	}

	private fun updateInvalidDatePair(pairList: JsonArray, @WorkerThread callback: () -> Unit) {
		GoldStoneAPI.getCurrencyLineChartData(pairList) { newChart, error ->
			if (!newChart.isNullOrEmpty() && error.isNone()) {
				// 更新数据库的数据
				newChart.forEachIndexed { index, model ->
					QuotationSelectionTable.dao.updateDayLineChartByPair(
						pairList.get(index).asString,
						model.pointList.toString()
					)
				}
				callback()
			}
		}
	}

	private var currentSocket: GoldStoneWebSocket? = null
	private fun setSocket(callback: (GoldStoneWebSocket?) -> Unit) {
		if (fragment.asyncData?.size ?: 0 == 0) return
		getPriceInfoBySocket(
			fragment.asyncData?.map { it.pair },
			{
				currentSocket = it
				callback(it)
			}
		) { model, isDisconnected ->
			fragment.updateAdapterDataSet(model, isDisconnected)
		}
	}

	override fun onFragmentHiddenChanged(isHidden: Boolean) {
		if (isHidden) currentSocket?.isSocketConnected()?.isTrue {
			currentSocket?.closeSocket()
		} else currentSocket?.isSocketConnected()?.isFalse {
			currentSocket?.runSocket()
		}
	}

	private fun QuotationFragment.updateAdapterDataSet(data: CurrencyPriceInfoModel, isDisconnected: Boolean) {
		load {
			asyncData?.find {
				it.pair.equals(data.pair, true)
			}?.apply {
				this.price = data.price
				this.percent = data.percent
				this.isDisconnected = isDisconnected
			}
		} then {
			if (it.isNotNull()) recyclerView.adapter?.notifyDataSetChanged()
		}
	}

	fun showQuotationManagement() {
		fragment.activity?.addFragmentAndSetArguments<QuotationOverlayFragment>(ContainerID.main) {
			putString(ArgumentKey.quotationOverlayTitle, QuotationText.management)
		}
	}

	fun showMarketTokenDetailFragment(model: QuotationModel) {
		fragment.activity?.addFragmentAndSetArguments<QuotationOverlayFragment>(ContainerID.main) {
			putSerializable(ArgumentKey.quotationOverlayInfo, model)
		}
	}

	private fun convertDataToChartData(data: String): List<ChartPoint> {
		val jsonArray = JSONArray(data)
		val maxIndexOfData =
			if (jsonArray.length() > DataValue.quotationDataCount) DataValue.quotationDataCount
			else jsonArray.length()
		return (0 until maxIndexOfData).map {
			ChartPoint(jsonArray.getJSONObject(it))
		}.sortedBy {
			it.label.toLongOrNull() ?: 0L
		}
	}

	companion object {
		fun getPriceInfoBySocket(
			pairList: List<String>?,
			holdSocket: (GoldStoneWebSocket) -> Unit,
			hold: (model: CurrencyPriceInfoModel, isDisconnected: Boolean) -> Unit
		) {
			/**
			 * 准备长连接, 发送参数. 并且在返回结果的地方异步更新界面上的 `UI`.
			 */
			object : GoldStoneWebSocket("{\"t\": \"unsub_tick\"}") {
				override fun onOpened() {
					sendMessage("{\"t\":\"sub_tick\", \"pair_list\":${pairList?.toJsonArray()}}")
				}

				override fun getServerBack(content: JSONObject, isDisconnected: Boolean) {
					hold(CurrencyPriceInfoModel(content), isDisconnected)
				}
			}.apply(holdSocket)
		}
	}
}
