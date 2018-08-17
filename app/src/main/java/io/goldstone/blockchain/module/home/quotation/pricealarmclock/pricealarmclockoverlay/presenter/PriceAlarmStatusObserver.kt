package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclockoverlay.presenter

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.RelativeLayout
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.into
import com.blinnnk.extension.isNull
import com.blinnnk.uikit.uiPX
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
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclockoverlay.receiver.PriceAlarmClockReceiver
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
    if (MainActivity.backgroundFlag) {
      PriceAlarmClockTable.getAllPriceAlarm {
        val priceAlarmClockTableArrayList = it
        val parameter = eliminateDuplicationData(priceAlarmClockTableArrayList)
        GoldStoneAPI.getPricePairs(parameter, {
        }) {
          val pricePairArrayList = resolveJsonData(it)
          juxtaposeData(
            pricePairArrayList,
            priceAlarmClockTableArrayList
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
    val localSize = priceAlarmClockTableArrayList.size - 1
    val pairArrayList = ArrayList<String>()
    val parameter = JsonArray()
    for (index: Int in 0..localSize) {
      priceAlarmClockTableArrayList[index].pair?.let {
        pairArrayList.add(it)
      }
    }
    // 使用hashset去重
    val pairHashSet = HashSet<String>(pairArrayList)
    val iterator = pairHashSet.iterator()
    while (iterator.hasNext()) {
      parameter.add(iterator.next())
    }
    return parameter
  }

  // 手动解析json数据
  private fun resolveJsonData(it: String): ArrayList<PricePairModel> {
    val gson = Gson()
    var map = gson.fromJson(it, Map::class.java)
    val value: String = (map.get("data")).toString()
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
  private fun juxtaposeData(
    pricePairArrayList: ArrayList<PricePairModel>,
    priceAlarmClockTableArrayList: ArrayList<PriceAlarmClockTable>) {
    val netDataSize = pricePairArrayList.size - 1
    val localSize = priceAlarmClockTableArrayList.size - 1
    loop@ for (netDataIndex: Int in 0..netDataSize) {
      for (localIndex: Int in 0..localSize) {
        val pricePairModel = pricePairArrayList[netDataIndex]
        val priceAlarmClockTable = priceAlarmClockTableArrayList[localIndex]
        if (priceAlarmClockTable.status) {
          if (pricePairModel.pair == priceAlarmClockTable.pair) {
            val priceType = priceAlarmClockTable.priceType
            if ((priceType == 0 && pricePairModel.price.toDouble() > priceAlarmClockTable.price.toString().toDouble()) ||
              (priceType != 0 && pricePairModel.price.toDouble() < priceAlarmClockTable.price.toString().toDouble())) {
              pricePairModel.pairDisplay = priceAlarmClockTable.pairDisplay.toString()
              pricePairModel.marketName = priceAlarmClockTable.marketName.toString()
              closeOnceAlarm(priceAlarmClockTable)
              priceAlarmClockTableArrayList.remove(priceAlarmClockTable)
              showPriceAlarmDialog(
                context,
                pricePairModel,
                priceAlarmClockTable,
                pricePairArrayList,
                priceAlarmClockTableArrayList
              )
              break@loop
            }
          }
        } else {
          priceAlarmClockTableArrayList.remove(priceAlarmClockTable)
        }
      }
    }
  }

  // 关闭单次闹铃
  private fun closeOnceAlarm(priceAlarmClockTable: PriceAlarmClockTable) {
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
    pricePairModel: PricePairModel,
    priceAlarmClockTable: PriceAlarmClockTable,
    pricePairArrayList: ArrayList<PricePairModel>,
    priceAlarmClockTableArrayList: ArrayList<PriceAlarmClockTable>
  ) {
    GoldStoneAPI.context.runOnUiThread {

      PriceAlarmClockUtils.sendAlarmReceiver(
        0,
        context,
        1
      )
      val priceAlarmContent = AlarmClockText.priceAlarmNotificationContent(
        pricePairModel,
        priceAlarmClockTable
      )
      PriceAlarmClockNotificationUtils.sendPriceAlarmClockNotification(
        context,
        AlarmClockText.priceWarning,
        priceAlarmContent,
        priceAlarmClockTable.price.toString()
      )
      val goldStoneDialogFlag = (context as Activity).findViewById<GoldStoneDialog>(ElementID.dialog).isNull {}
      if (goldStoneDialogFlag) {
        GoldStoneDialog.show(context) {
          showButtons {
            gotItButtonClickEvent(pricePairArrayList, priceAlarmClockTableArrayList)
          }
          setGoldStoneDialogAttributes(
            this,
            pricePairModel,
            priceAlarmClockTable,
            pricePairArrayList,
            priceAlarmClockTableArrayList
          )
        }
      } else {
        val goldStoneDialog = context.findViewById<GoldStoneDialog>(ElementID.dialog)
        removeDialog(goldStoneDialog)
        goldStoneDialog.into(context.findViewById<RelativeLayout>(ContainerID.main))
        setGoldStoneDialogAttributes(
          goldStoneDialog,
          pricePairModel,
          priceAlarmClockTable,
          pricePairArrayList,
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
            priceAlarmClockTable.symbol.toString(),
            priceAlarmClockTable.name.toString(),
            priceAlarmClockTable.price.toString(),
            "-3.642",
            ArrayList(),
            priceAlarmClockTable.marketName.toString(),
            1.0,
            priceAlarmClockTable.pairDisplay.toString(),
            priceAlarmClockTable.pair.toString(),
            priceAlarmClockTable.currencyName.toString(),
            "0x86fa049857e0209aa7d9e616f7eb3b3b78ecfdb0",
            false
          )
        )
      }
    }
  }

  private fun gotItButtonClickEvent(
    pricePairArrayList: ArrayList<PricePairModel>,
    priceAlarmClockTableArrayList: ArrayList<PriceAlarmClockTable>
  ) {
    PriceAlarmClockUtils.stopAlarmReceiver(
      context,
      1
    )
    PriceAlarmClockReceiver.stopAlarmClock()
    GoldStoneDialog.remove(context)
    val mainLayout = (context as Activity).findViewById<RelativeLayout>(ContainerID.main)
    val goldStoneDialog = context.findViewById<GoldStoneDialog>(ElementID.dialog)
    val childCount = mainLayout.childCount - 1
    for (index: Int in 0..childCount) {
      val childAt = mainLayout.getChildAt(index)
      if (childAt == goldStoneDialog) {
        mainLayout.removeView(goldStoneDialog)
      }
    }
    juxtaposeData(
      pricePairArrayList,
      priceAlarmClockTableArrayList
    )
  }

  private fun viewAlarmButtonClickEvent(
    pricePairArrayList: ArrayList<PricePairModel>,
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
    juxtaposeData(
      pricePairArrayList,
      priceAlarmClockTableArrayList
    )
    showMarketTokenDetailFragment(priceAlarmClockTable)
  }

  private fun setGoldStoneDialogAttributes(
    goldStoneDialog: GoldStoneDialog,
    pricePairModel: PricePairModel,
    priceAlarmClockTable: PriceAlarmClockTable,
    pricePairArrayList: ArrayList<PricePairModel>,
    priceAlarmClockTableArrayList: ArrayList<PriceAlarmClockTable>
  ) {
    val priceAlarmContent = AlarmClockText.priceAlarmContent(
      pricePairModel,
      priceAlarmClockTable
    )
    goldStoneDialog.apply {

      getCancelButton().apply {
        text = AlarmClockText.viewAlarm
        onClick {
          viewAlarmButtonClickEvent(
            pricePairArrayList,
            priceAlarmClockTableArrayList,
            priceAlarmClockTable
          )
        }
      }

      getConfirmButton().apply {
        text = CommonText.confirm
        onClick {
          gotItButtonClickEvent(pricePairArrayList, priceAlarmClockTableArrayList)
        }
      }

      getConfirmButton().text = AlarmClockText.gotIt

      setImage(R.drawable.price_alarm_banner)
      setImageLayoutParams(
        300.uiPX(),
        180.uiPX()
      )

      setContent(
        "${pricePairModel.marketName} ${pricePairModel.pairDisplay}${AlarmClockText.achieve}${pricePairModel.price}",
        priceAlarmContent
      )
    }
  }

}

data class PricePairModel(var pair: String, var price: String, var marketName: String = "", var pairDisplay: String = "") {
}