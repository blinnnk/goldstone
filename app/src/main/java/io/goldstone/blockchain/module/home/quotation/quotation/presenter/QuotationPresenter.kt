package io.goldstone.blockchain.module.home.quotation.quotation.presenter

import android.text.format.DateUtils
import com.blinnnk.extension.*
import com.db.chart.model.Point
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.GoldStoneWebSocket
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.QuotationText
import io.goldstone.blockchain.module.home.quotation.quotation.model.CurrencyPriceInfoModel
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationAdapter
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationFragment
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray
import org.json.JSONObject

/**
 * @date 26/03/2018 8:56 PM
 * @author KaySaith
 */

class QuotationPresenter(
	override val fragment: QuotationFragment
) : BaseRecyclerPresenter<QuotationFragment, QuotationModel>() {

	override fun updateData() {
		QuotationSelectionTable.getMySelections { selections ->
			selections.map {
				QuotationModel(it, "--", "+2.56", convertDataToChartData(it.lineChart))
			}.sortedByDescending {
				it.orderID
			}.toArrayList().let {
				fragment.asyncData.isNull() isTrue {
					fragment.asyncData = it
				} otherwise {
					if (fragment.asyncData.orEmptyArray().isEmpty()) fragment.removeEmptyView()
					diffAndUpdateAdapterData<QuotationAdapter>(it)
				}
			}
		}
	}

	/**
	 * 准备长连接, 发送参数. 并且在返回结果的地方异步更新界面上的 `UI`.
	 */
	private val currentSocket: GoldStoneWebSocket = object : GoldStoneWebSocket() {
		override fun onOpened() {
			// 如果没有订阅的数据直接退出逻辑
			if (fragment.asyncData?.isEmpty() == true) return
			fragment.asyncData?.map { it.pair }?.toArrayList()?.toJsonArray {
				sendMessage("{\"t\":\"sub_tick\", \"pair_list\":$it}")
			}
		}

		override fun getServerBack(content: JSONObject) {
			fragment.updateAdapterDataset(CurrencyPriceInfoModel(content))
		}
	}

	/** 在界面的 `Adapter` 准备完毕后, 并且有数据的情况下建立长连接 */
	override fun afterUpdateAdapterDataset(recyclerView: BaseRecyclerView) {
		super.afterUpdateAdapterDataset(recyclerView)
		currentSocket.runSocket()
	}

	override fun onFragmentHiddenChanged(isHidden: Boolean) {
		if (isHidden) {
			currentSocket.isSocketConnected() isTrue {
				currentSocket.closeSocket()
			}
		} else {
			currentSocket.isSocketConnected() isFalse {
				currentSocket.runSocket()
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

	fun showMarketTokenDetailFragment(symbol: String) {
		fragment.activity?.addFragmentAndSetArguments<QuotationOverlayFragment>(ContainerID.main) {
			putString(ArgumentKey.quotationOverlayTitle, symbol)
		}
	}

	private fun convertDataToChartData(data: String): ArrayList<Point> {
		val jsonarray = JSONArray(data)
		(0 until jsonarray.length()).map {
			val timeStamp = jsonarray.getJSONObject(it)["time"].toString().toLong()
			val date = DateUtils.formatDateTime(fragment.context, timeStamp, DateUtils.FORMAT_NO_YEAR)
			Point(date, jsonarray.getJSONObject(it)["price"].toString().toFloat())
		}.reversed().let {
			return it.toArrayList()
		}
	}
}