package io.goldstone.blockchain.module.home.quotation.quotation.presenter

import com.blinnnk.extension.*
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.GoldStoneWebSocket
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.daysAgoInMills
import io.goldstone.blockchain.crypto.getObjectMD5HexString
import io.goldstone.blockchain.module.home.home.view.MainActivity
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
var selectionMD5: String? = null
var memoryData: ArrayList<QuotationModel>? = null

class QuotationPresenter(
	override val fragment: QuotationFragment
) : BaseRecyclerPresenter<QuotationFragment, QuotationModel>() {
	
	private var updateChartTimes: Int? = null
	private var hasInitSocket = false
	
	override fun updateData() {
		if (fragment.asyncData.isNull()) fragment.asyncData = arrayListOf()
		// 如果内存有数据直接更新内存的数据
		memoryData?.let {
			diffAndUpdateAdapterData<QuotationAdapter>(it)
		}
		
		QuotationSelectionTable.getMySelections { selections ->
			// 比对内存中的源数据 `MD5` 和新的数据是否一样, 如果一样跳出
			if (selectionMD5 == selections.getObjectMD5HexString()) {
				return@getMySelections
			}
			selectionMD5 = selections.getObjectMD5HexString()
			/** 记录可能需要更新的 `Line Chart` 最大个数 */
			if (updateChartTimes.isNull()) updateChartTimes = selections.size
			
			selections.map { selection ->
				var linechart = arrayListOf<ChartPoint>()
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
			}.toArrayList().let {
				// 把数据存在内存里面方便下次打开使用
				memoryData = it
				// 更新 `UI`
				diffAndUpdateAdapterData<QuotationAdapter>(it)
				// 设定 `Socket` 并执行
				currentSocket.isNull() isTrue {
					// 初始化 `Socket`
					setSocket {
						hasInitSocket = true
						currentSocket?.runSocket()
					}
				} otherwise {
					// 更新 `Sockert`
					fragment.asyncData?.map { it.pair }?.toArrayList()?.toJsonArray {
						currentSocket?.sendMessage("{\"t\":\"sub_tick\", \"pair_list\":$it}")
					}
				}
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
			}
		}
		if (currentSocket.isNull() && hasInitSocket) {
			setSocket {
				currentSocket?.runSocket()
			}
		}
	}
	
	private fun ArrayList<ChartPoint>.checkTimeStampIfNeedUpdateBy(pair: String) {
		if (isEmpty()) return
		sortedByDescending {
			it.label.toLong()
		}.let {
			/** 服务端传入的最近的事件会做减1处理, 从服务器获取的事件是昨天的事件. */
			if (it.first().label.toLong() + 1L < 0.daysAgoInMills()) {
				QuotationSearchPresenter.getLineChartDataByPair(pair) { newChart ->
					QuotationSelectionTable.updateLineChartDataBy(pair, newChart) {
						/** 防止服务器数据出错或不足, 可能导致的死循环 */
						if (updateChartTimes!! > 0) {
							updateData()
							updateChartTimes = updateChartTimes!! - 1
						}
					}
				}
			}
		}
	}
	
	private var currentSocket: GoldStoneWebSocket? = null
	fun setSocket(
		holdData: CurrencyPriceInfoModel.() -> Unit = {},
		callback: (GoldStoneWebSocket?) -> Unit = {}
	) {
		fragment.asyncData?.isEmpty()?.isTrue { return }
		getPriceInfoBySocket(
			fragment.asyncData?.map { it.pair }?.toArrayList(),
			{
				currentSocket = it
				callback(it)
			}) {
			holdData(it)
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
		coroutinesTask(
			{
				asyncData?.find { it.pair == data.pair }?.apply {
					price = data.price
					percent = data.percent
				}
			}) {
			it?.let {
				recyclerView.adapter.notifyDataSetChanged()
			}
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
	
	private fun convertDataToChartData(data: String): ArrayList<ChartPoint> {
		val jsonarray = JSONArray(data)
		(0 until jsonarray.length()).map {
			val timeStamp = jsonarray.getJSONObject(it).safeGet("time").toLong()
			ChartPoint(
				timeStamp.toString(),
				jsonarray.getJSONObject(it).safeGet("price").toFloat()
			)
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
		
		fun getQuotationFragment(
			activity: MainActivity?,
			callback: QuotationFragment.() -> Unit
		) {
			activity?.apply {
				supportFragmentManager.findFragmentByTag(FragmentTag.home)?.apply {
					findChildFragmentByTag<QuotationFragment>(FragmentTag.quotation)?.let {
						callback(it)
					}
				}
			}
		}
	}
}