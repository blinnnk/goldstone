package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.presenter

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.google.gson.Gson
import com.google.gson.JsonArray
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.language.AlarmClockText
import io.goldstone.blockchain.common.utils.PriceAlarmNotificationUtils
import io.goldstone.blockchain.common.utils.PriceAlarmUtils
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmTable
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PricePairModel
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 16/08/2018 5:02 PM
 * @author wcx
 */

abstract class PriceAlarmStatusObserver(private val context: Context) {
	private val handler = Handler(Looper.getMainLooper())
	private val retryTime = 900000L

	private fun checkStatusByPrice() {
		if (!XinGePushReceiver.backgroundFlag) {
			PriceAlarmTable.getAllPriceAlarm {
				val priceAlarmTableArrayList = it
				val parameters = removeDuplicationData(priceAlarmTableArrayList)
				GoldStoneAPI.getPricePairs(
					parameters,
					{}
				) {
					val pricePairArrayList = resolveJsonData(it)
					val compareNetDataAndLocalData = compareNetDataAndLocalData(
						pricePairArrayList,
						priceAlarmTableArrayList
					)
					showPriceAlarmDialog(
						context,
						compareNetDataAndLocalData
					)
				}
				handler.postDelayed(
					reDo,
					retryTime
				)
			}
		}
	}

	// 去除重复数据
	private fun removeDuplicationData(priceAlarmTableArrayList: ArrayList<PriceAlarmTable>): JsonArray {
		val localSize = priceAlarmTableArrayList.size
		val pairArrayList = ArrayList<String>()
		val parameters = JsonArray()
		for (index: Int in 0 until localSize) {
			pairArrayList.add(priceAlarmTableArrayList[index].pair)
		}
		pairArrayList.distinct()
		val pairSize = pairArrayList.size
		for (index: Int in 0 until pairSize) {
			parameters.add(pairArrayList[index])
		}
		return parameters
	}

	// 手动解析json数据
	private fun resolveJsonData(it: String): ArrayList<PricePairModel> {
		val gson = Gson()
		var map = gson.fromJson(it, Map::class.java)
		val value: String = (map["data"]).toString()
		map = gson.fromJson(value, Map::class.java)
		val keys = map.iterator()
		val pricePairArrayList = ArrayList<PricePairModel>()
		while (keys.hasNext()) {
			val next = keys.next()
			pricePairArrayList.add(PricePairModel(next.key.toString(), next.value.toString()))
		}
		return pricePairArrayList
	}

	// 对比网络市场价格和本地设置价格获得响铃列表
	private fun compareNetDataAndLocalData(
		newData: ArrayList<PricePairModel>,
		localData: ArrayList<PriceAlarmTable>
	): ArrayList<PriceAlarmTable> {
		return localData.map { local ->
			val existNewData = newData.find {
				it.pair.equals(
					local.pair,
					true
				)
			}
			local.status && !existNewData.isNull()
			Pair(
				local,
				existNewData
			)
		}.filter { localAndNewData ->
			val local = localAndNewData.first
			val new = localAndNewData.second
			(local.priceType == ArgumentKey.greaterThanForPriceType && new?.price?.toDouble().orZero() > local.price.toDouble()) ||
				(local.priceType == ArgumentKey.lessThanForPriceType && new?.price?.toDouble().orZero() < local.price.toDouble())
		}.map {
			it.first.apply { marketPrice = it.second?.price ?: "" }
		} as ArrayList<PriceAlarmTable>
	}

	// 关闭单次闹铃
	private fun turnOffOnceAlarm(priceAlarmTable: PriceAlarmTable) {
		if (priceAlarmTable.alarmType != ArgumentKey.repeatingForAlarm) {
			priceAlarmTable.status = false
			PriceAlarmTable.updatePriceAlarm(priceAlarmTable) {}
		}
	}

	fun removeObserver() {
		handler.removeCallbacks(reDo)
	}

	fun start() {
		checkStatusByPrice()
	}

	private val reDo: Runnable = Runnable {
		checkStatusByPrice()
	}

	private fun showPriceAlarmDialog(
		context: Context,
		priceAlarmTableArrayList: ArrayList<PriceAlarmTable>
	) {
		if (priceAlarmTableArrayList.isEmpty()) return
		GoldStoneAPI.context.runOnUiThread {
			val priceAlarmTable = priceAlarmTableArrayList[0]
			turnOffOnceAlarm(priceAlarmTable)
			priceAlarmTableArrayList.remove(priceAlarmTable)
			PriceAlarmUtils.sendAlarmReceiver(
				0,
				context,
				1
			)
			val priceAlarmContent = AlarmClockText.priceAlarmNotificationContent(priceAlarmTable)
			PriceAlarmNotificationUtils.sendPriceAlarmClockNotification(
				context,
				AlarmClockText.priceWarning,
				priceAlarmContent,
				priceAlarmTable.price
			)
			GoldStoneDialog.remove(context as MainActivity)
			GoldStoneDialog.show(context) {
				showButtons {
					viewAlarmButtonClickEvent(
						priceAlarmTableArrayList,
						priceAlarmTable
					)
				}
				setGoldStoneDialogAttributes(
					this,
					priceAlarmTable,
					priceAlarmTableArrayList
				)
			}
		}
	}

	private fun showMarketTokenDetailFragment(priceAlarmTable: PriceAlarmTable) {
		priceAlarmTable.let {
			(context as Activity).addFragmentAndSetArguments<QuotationOverlayFragment>(ContainerID.main) {
				putBoolean(
					ArgumentKey.priceAlarmTitle,
					true
				)
				putSerializable(
					ArgumentKey.quotationOverlayInfo,
					QuotationModel(priceAlarmTable)
				)

			}
		}
	}

	private fun gotItButtonClickEvent(priceAlarmTableArrayList: ArrayList<PriceAlarmTable>) {
		PriceAlarmUtils.stopAlarmReceiver(
			context,
			1
		)
		PriceAlarmReceiver.stopAlarmClock()
		showPriceAlarmDialog(
			context,
			priceAlarmTableArrayList
		)
	}

	private fun viewAlarmButtonClickEvent(
		priceAlarmTableArrayList: ArrayList<PriceAlarmTable>,
		priceAlarmTable: PriceAlarmTable
	) {

		PriceAlarmUtils.stopAlarmReceiver(
			context,
			1
		)
		PriceAlarmReceiver.stopAlarmClock()
		showPriceAlarmDialog(
			context,
			priceAlarmTableArrayList
		)
		showMarketTokenDetailFragment(priceAlarmTable)
	}

	private fun setGoldStoneDialogAttributes(
		goldStoneDialog: GoldStoneDialog,
		priceAlarmTable: PriceAlarmTable,
		priceAlarmTableArrayList: ArrayList<PriceAlarmTable>
	) {
		val priceAlarmContent = AlarmClockText.priceAlarmContent(priceAlarmTable)
		goldStoneDialog.apply {

			getCancelButton().apply {
				text = AlarmClockText.gotIt
				onClick {
					gotItButtonClickEvent(priceAlarmTableArrayList)
				}
			}

			getConfirmButton().apply {
				text = AlarmClockText.viewAlarm
				onClick {
					viewAlarmButtonClickEvent(
						priceAlarmTableArrayList,
						priceAlarmTable
					)
				}
			}

			setImage(R.drawable.price_alarm_banner)

			setContent(
				"${priceAlarmTable.marketName} ${priceAlarmTable.pairDisplay}${AlarmClockText.achieve}${priceAlarmTable.marketPrice}",
				priceAlarmContent
			)
		}
	}

}
