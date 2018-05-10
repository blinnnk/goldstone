package io.goldstone.blockchain.module.home.quotation.quotation.presenter

import com.blinnnk.extension.*
import com.db.chart.model.Point
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.GoldStoneWebSocket
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.QuotationText
import io.goldstone.blockchain.crypto.daysAgoInMills
import io.goldstone.blockchain.crypto.getObjectMD5HexString
import io.goldstone.blockchain.module.home.quotation.quotation.model.CurrencyPriceInfoModel
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationAdapter
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationFragment
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.presenter.QuotationSearchPresenter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray
import org.json.JSONObject

/**
 * @date 26/03/2018 8:56 PM
 * @author KaySaith
 */

var selectionMD5: String? = null
var memoryData: ArrayList<QuotationModel>? = null

class QuotationPresenter(
	override val fragment: QuotationFragment
) : BaseRecyclerPresenter<QuotationFragment, QuotationModel>() {

	private var updateChartTimes: Int? = null

	override fun updateData() {
		fragment.asyncData.isNotNull {
			setSocket {
				currentSocket?.runSocket()
			}
		}
		updateQuotationData()
	}

	private fun updateQuotationData() {
		// 如果内存有数据直接更新内存的数据
		memoryData?.let { diffAndUpdateData(it) }

		QuotationSelectionTable.getMySelections { selections ->
			// 比对内存中的源数据 `MD5` 和新的数据是否一样, 如果一样跳出
			if (selectionMD5 == selections.getObjectMD5HexString()) {
				return@getMySelections
			}
			selectionMD5 = selections.getObjectMD5HexString()

			/** 记录可能需要更新的 `Line Chart` 最大个数 */
			if (updateChartTimes.isNull()) updateChartTimes = selections.size

			selections.map { selection ->
				val linechart = convertDataToChartData(selection.lineChartDay)
				linechart.checkTimeStampIfNeedUpdateBy(selection.pair)
				QuotationModel(selection, "--", "0", linechart)
			}.sortedByDescending {
				it.orderID
			}.toArrayList().let {
				// 把数据存在内存里面方便下次打开使用
				memoryData = it
				// 更新 `UI`
				diffAndUpdateData(it)
				// 设定 `Socket` 并执行
				currentSocket.isNull() isTrue {
					setSocket {
						currentSocket?.runSocket()
					}
				}
			}
		}
	}

	private fun diffAndUpdateData(data: ArrayList<QuotationModel>) {
		fragment.asyncData.isNull() isTrue {
			fragment.asyncData = data
		} otherwise {
			diffAndUpdateAdapterData<QuotationAdapter>(data)
			fragment.setEmptyViewBy(data)
		}
	}

	private fun ArrayList<Point>.checkTimeStampIfNeedUpdateBy(pair: String) {
		sortedByDescending {
			it.label.toLong()
		}.let {
			// 服务端传入的最近的事件会做减1处理, 从服务器获取的事件是昨天的事件.
			// 本地的当天 `lineChart` 的值是通过长连接实时更新的.
			if (it.first().label.toLong() + 1L < 0.daysAgoInMills()) {
				QuotationSearchPresenter.getLineChartDataByPair(pair) { newChart ->
					QuotationSelectionTable.updateLineChartDataBy(pair, newChart) {
						/** 防止服务器数据出错, 可能导致的死循环 */
						if (updateChartTimes!! > 0) {
							updateQuotationData()
							updateChartTimes = updateChartTimes!! - 1
						}
					}
				}
			}
		}
	}

	private var currentSocket: GoldStoneWebSocket? = null
	private fun setSocket(callback: () -> Unit) {
		fragment.asyncData?.isEmpty()?.isTrue { return }
		getPriceInfoBySocket(fragment.asyncData?.map { it.pair }?.toArrayList(), {
			currentSocket = it
			callback()
		}) {
			fragment.updateAdapterDataset(it)
		}
	}

	override fun onFragmentHiddenChanged(isHidden: Boolean) {
		if (isHidden) {
			currentSocket?.isSocketConnected()?.isTrue {
				currentSocket?.closeSocket()
			}
		} else {
			currentSocket?.isSocketConnected()?.isFalse {
				currentSocket?.runSocket()
			}
		}
	}

	private fun QuotationFragment.updateAdapterDataset(data: CurrencyPriceInfoModel) {
		doAsync {
			var index: Int? = null
			asyncData?.forEachOrEnd { item, isEnd ->
				if (item.pair == data.pair) {
					item.price = data.price
					item.percent = data.percent
					index = asyncData?.indexOf(item).orZero() + 1
				}
				if (isEnd) {
					context?.runOnUiThread {
						index?.let { recyclerView.adapter.notifyItemChanged(it) }
					}
				}
			}
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

	private fun convertDataToChartData(data: String): ArrayList<Point> {
		val jsonarray = JSONArray(data)
		(0 until jsonarray.length()).map {
			val timeStamp = jsonarray.getJSONObject(it)["time"].toString().toLong()
			Point(timeStamp.toString(), jsonarray.getJSONObject(it)["price"].toString().toFloat())
		}.reversed().let {
			return it.toArrayList()
		}
	}

	companion object {
		fun getPriceInfoBySocket(
			pairList: ArrayList<String>?,
			holdSocket: (GoldStoneWebSocket) -> Unit,
			hold: (CurrencyPriceInfoModel) -> Unit
		) {
			/**
			 * 准备长连接, 发送参数. 并且在返回结果的地方异步更新界面上的 `UI`.
			 */
			object : GoldStoneWebSocket() {
				override fun onOpened() {
					pairList?.toJsonArray {
						sendMessage("{\"t\":\"sub_tick\", \"pair_list\":$it}")
					}
				}

				override fun getServerBack(content: JSONObject) {
					hold(CurrencyPriceInfoModel(content))
				}
			}.apply(holdSocket)
		}
	}
}