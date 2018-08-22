package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.presenter

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.RelativeLayout
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.into
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.google.gson.Gson
import com.google.gson.JsonArray
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.language.AlarmClockText
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.PriceAlarmClockNotificationUtils
import io.goldstone.blockchain.common.utils.PriceAlarmClockUtils
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable
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
		if (XinGePushReceiver.backgroundFlag) {
			PriceAlarmClockTable.getAllPriceAlarm {
				val priceAlarmClockTableArrayList = it
				val parameters = eliminateDuplicationData(priceAlarmClockTableArrayList)
				GoldStoneAPI.getPricePairs(
					parameters,
					{}
				) {
					val pricePairArrayList = resolveJsonData(it)
					val compareNetDataAndLocalData = compareNetDataAndLocalData(
						pricePairArrayList,
						priceAlarmClockTableArrayList
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
	private fun eliminateDuplicationData(priceAlarmClockTableArrayList: ArrayList<PriceAlarmClockTable>): JsonArray {
		val localSize = priceAlarmClockTableArrayList.size
		val pairArrayList = ArrayList<String>()
		val parameters = JsonArray()
		for (index: Int in 0 until localSize) {
			pairArrayList.add(priceAlarmClockTableArrayList[index].pair)
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

	// 比对数据
	private fun compareData(
		pricePairArrayList: ArrayList<PricePairModel>,
		priceAlarmClockTableArrayList: ArrayList<PriceAlarmClockTable>
	) {
		val netDataSize = pricePairArrayList.size
		val localSize = priceAlarmClockTableArrayList.size
		loop@ for (netDataIndex: Int in 0 until netDataSize) {
			for (localIndex: Int in 0 until localSize) {
				val pricePairModel = pricePairArrayList[netDataIndex]
				val priceAlarmClockTable = priceAlarmClockTableArrayList[localIndex]
				if (priceAlarmClockTable.status) {
					if (pricePairModel.pair == priceAlarmClockTable.pair) {
						val priceType = priceAlarmClockTable.priceType
						if ((priceType == 0 && pricePairModel.price.toDouble() > priceAlarmClockTable.price.toDouble()) ||
							(priceType != 0 && pricePairModel.price.toDouble() < priceAlarmClockTable.price.toDouble())) {
							pricePairModel.pairDisplay = priceAlarmClockTable.pairDisplay
							pricePairModel.marketName = priceAlarmClockTable.marketName
							turnOffOnceAlarm(priceAlarmClockTable)
							priceAlarmClockTableArrayList.remove(priceAlarmClockTable)
//							showPriceAlarmDialog(
//								context,
//								pricePairModel,
//								priceAlarmClockTable,
//								pricePairArrayList,
//								priceAlarmClockTableArrayList
//							)
							break@loop
						}
					}
				} else {
					priceAlarmClockTableArrayList.remove(priceAlarmClockTable)
				}
			}
		}
	}

	// 对比网络市场价格和本地设置价格获得响铃列表
	private fun compareNetDataAndLocalData(
		newData: ArrayList<PricePairModel>,
		localData: ArrayList<PriceAlarmClockTable>
	): ArrayList<PriceAlarmClockTable> {
		return localData.map { local ->
			val existNewData = newData.find { it.pair.equals(local.pair, true) }
			local.status && !existNewData.isNull()
			Pair(local, existNewData)
		}.filter { localAndNewData ->
			val local = localAndNewData.first
			val new = localAndNewData.second
			(local.priceType == 0 && new?.price?.toDouble().orZero() > local.price.toDouble()) ||
				(local.priceType == 1 && new?.price?.toDouble().orZero() < local.price.toDouble())
		}.map {
			it.first.apply { marketPrice = it.second?.price ?: "" }
		} as ArrayList<PriceAlarmClockTable>
	}

	// 关闭单次闹铃
	private fun turnOffOnceAlarm(priceAlarmClockTable: PriceAlarmClockTable) {
		if (priceAlarmClockTable.alarmType != 0) {
			priceAlarmClockTable.status = false
			PriceAlarmClockTable.updatePriceAlarm(priceAlarmClockTable) {}
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
		priceAlarmClockTableArrayList: ArrayList<PriceAlarmClockTable>
	) {
		if (priceAlarmClockTableArrayList.size == 0) return
		GoldStoneAPI.context.runOnUiThread {
			val priceAlarmClockTable = priceAlarmClockTableArrayList[0]
			turnOffOnceAlarm(priceAlarmClockTable)
			priceAlarmClockTableArrayList.remove(priceAlarmClockTable)
			PriceAlarmClockUtils.sendAlarmReceiver(
				0,
				context,
				1
			)
			val priceAlarmContent = AlarmClockText.priceAlarmNotificationContent(priceAlarmClockTable)
			PriceAlarmClockNotificationUtils.sendPriceAlarmClockNotification(
				context,
				AlarmClockText.priceWarning,
				priceAlarmContent,
				priceAlarmClockTable.price
			)
			val goldStoneDialogFlag = (context as Activity).findViewById<GoldStoneDialog>(ElementID.dialog).isNull {}
			if (goldStoneDialogFlag) {
				GoldStoneDialog.show(context) {
					showButtons {
						viewAlarmButtonClickEvent(
							priceAlarmClockTableArrayList,
							priceAlarmClockTable
						)
					}
					setGoldStoneDialogAttributes(
						this,
						priceAlarmClockTable,
						priceAlarmClockTableArrayList
					)
				}
			} else {
				val goldStoneDialog = context.findViewById<GoldStoneDialog>(ElementID.dialog)
				removeDialog(goldStoneDialog)
				goldStoneDialog.into(context.findViewById<RelativeLayout>(ContainerID.main))
				setGoldStoneDialogAttributes(
					goldStoneDialog,
					priceAlarmClockTable,
					priceAlarmClockTableArrayList
				)
			}
		}
	}

	fun removeDialog(goldStoneDialog: GoldStoneDialog) {
		GoldStoneDialog.remove(context)
		val mainLayout = (context as Activity).findViewById<RelativeLayout>(ContainerID.main)
		val childCount = mainLayout.childCount - 1
		for (index: Int in 0..childCount) {
			val childAt = mainLayout.getChildAt(index)
			if (childAt == goldStoneDialog) {
				mainLayout.removeView(goldStoneDialog)
			}
		}
	}

	private fun showMarketTokenDetailFragment(priceAlarmClockTable: PriceAlarmClockTable) {
		priceAlarmClockTable.let {
			(context as Activity).addFragmentAndSetArguments<QuotationOverlayFragment>(ContainerID.main) {
				putString(
					ArgumentKey.priceAlarmClockTitle,
					AlarmClockText.viewAlarm
				)
				putSerializable(
					ArgumentKey.quotationOverlayInfo, QuotationModel(
						priceAlarmClockTable.symbol,
						priceAlarmClockTable.name.toString(),
						priceAlarmClockTable.price,
						"",
						ArrayList(),
						priceAlarmClockTable.marketName,
						0.0,
						priceAlarmClockTable.pairDisplay,
						priceAlarmClockTable.pair,
						priceAlarmClockTable.currencyName,
						"",
						false
					)
				)
			}
		}
	}

	private fun gotItButtonClickEvent(priceAlarmClockTableArrayList: ArrayList<PriceAlarmClockTable>) {
		PriceAlarmClockUtils.stopAlarmReceiver(
			context,
			1
		)
		PriceAlarmClockReceiver.stopAlarmClock()
		val goldStoneDialog = (context as Activity).findViewById<GoldStoneDialog>(ElementID.dialog)
		removeDialog(goldStoneDialog)
		showPriceAlarmDialog(
			context,
			priceAlarmClockTableArrayList
		)
	}

	private fun viewAlarmButtonClickEvent(
		priceAlarmClockTableArrayList: ArrayList<PriceAlarmClockTable>,
		priceAlarmClockTable: PriceAlarmClockTable
	) {

		PriceAlarmClockUtils.stopAlarmReceiver(
			context,
			1
		)
		PriceAlarmClockReceiver.stopAlarmClock()
		GoldStoneDialog.remove(context)
		(context as Activity).findViewById<RelativeLayout>(ContainerID.main).removeView(context.findViewById<GoldStoneDialog>(ElementID.dialog))
		showPriceAlarmDialog(
			context,
			priceAlarmClockTableArrayList
		)
		showMarketTokenDetailFragment(priceAlarmClockTable)
	}

	private fun setGoldStoneDialogAttributes(
		goldStoneDialog: GoldStoneDialog,
		priceAlarmClockTable: PriceAlarmClockTable,
		priceAlarmClockTableArrayList: ArrayList<PriceAlarmClockTable>
	) {
		val priceAlarmContent = AlarmClockText.priceAlarmContent(priceAlarmClockTable)
		goldStoneDialog.apply {

			getCancelButton().apply {
				text = AlarmClockText.gotIt
				onClick {
					gotItButtonClickEvent(priceAlarmClockTableArrayList)
				}
			}

			getConfirmButton().apply {
				text = AlarmClockText.viewAlarm
				onClick {
					viewAlarmButtonClickEvent(
						priceAlarmClockTableArrayList,
						priceAlarmClockTable
					)
				}
			}

			setImage(R.drawable.price_alarm_banner)

			setContent(
				"${priceAlarmClockTable.marketName} ${priceAlarmClockTable.pairDisplay}${AlarmClockText.achieve}${priceAlarmClockTable.marketPrice}",
				priceAlarmContent
			)
		}
	}

}
