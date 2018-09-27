package io.goldstone.blockchain.module.home.quotation.quotation.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.common.value.ValueTag
import io.goldstone.blockchain.crypto.utils.daysAgoInMills
import io.goldstone.blockchain.module.home.quotation.quotation.model.ChartPoint
import io.goldstone.blockchain.module.home.quotation.quotation.model.CurrencyPriceInfoModel
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationAdapter
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationFragment
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.presenter.QuotationSearchPresenter
import org.json.JSONArray
import org.json.JSONObject

/**
 * @date 26/03/2018 8:56 PM
 * @author KaySaith
 */

class QuotationPresenter(
	override val fragment: QuotationFragment
) : BaseRecyclerPresenter<QuotationFragment, QuotationModel>() {

	private var updateChartTimes: Int? = null
	private var hasInitSocket = false

	override fun updateData() {
		if (fragment.asyncData.isNull()) fragment.asyncData = arrayListOf()
		QuotationSelectionTable.getMySelections { selections ->
			/** 记录可能需要更新的 `Line Chart` 最大个数 */
			if (updateChartTimes.isNull()) updateChartTimes = selections.size
			selections.asSequence().map { selection ->
				var linechart = listOf<ChartPoint>()
				if (!selection.lineChartDay.isBlank()) {
					linechart = convertDataToChartData(selection.lineChartDay)
				}
				linechart.checkTimeStampIfNeedUpdateBy(selection.pair)
				QuotationModel(
					selection,
					ValueTag.emptyPrice,
					"0",
					linechart
				)
			}.sortedByDescending {
				it.orderID
			}.toList().toArrayList().let { it ->
				// 更新 `UI`
				diffAndUpdateAdapterData<QuotationAdapter>(it)
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

	private fun List<ChartPoint>.checkTimeStampIfNeedUpdateBy(pair: String) {
		if (isEmpty()) return
		/** 服务端传入的最近的时间会做减1处理, 从服务器获取的事件是昨天的事件. */
		val maxDate = maxBy { it.label.toLong() }?.label?.toLongOrNull().orElse(0L)
		if (maxDate + 1L < 0.daysAgoInMills()) {
			QuotationSearchPresenter.getLineChartDataByPair(pair) { newChart, error ->
				if (!newChart.isNull() && error.isNone()) {
					QuotationSelectionTable.updateLineChartDataBy(pair, newChart!!) {
						/** 防止服务器数据出错或不足, 可能导致的死循环 */
						if (updateChartTimes.orZero() > 0) {
							updateData()
							updateChartTimes = updateChartTimes.orZero() - 1
						}
					}
				} else fragment.context.alert(error.message)
			}
		}
	}

	private var currentSocket: GoldStoneWebSocket? = null
	private fun setSocket(
		callback: (GoldStoneWebSocket?) -> Unit
	) {
		fragment.asyncData?.isEmpty()?.isTrue { return }
		getPriceInfoBySocket(
			fragment.asyncData?.map { it.pair },
			{
				currentSocket = it
				callback(it)
			}) { model, isDisconnected ->
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

	private fun QuotationFragment.updateAdapterDataSet(
		data: CurrencyPriceInfoModel,
		isDisconnected: Boolean
	) {
		load {
			asyncData?.find {
				it.pair.equals(data.pair, true)
			}?.apply {
				this.price = data.price
				this.percent = data.percent
				this.isDisconnected = isDisconnected
			}
		} then {
			if (!it.isNull()) recyclerView.adapter?.notifyDataSetChanged()
		}
	}

	fun showQuotationManagement() {
		fragment.activity?.addFragmentAndSetArguments<QuotationOverlayFragment>(ContainerID.main) {
			putString(
				ArgumentKey.quotationOverlayTitle,
				QuotationText.management
			)
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
			it.label.toLong()
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
			object : GoldStoneWebSocket() {
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
