package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclockeditor.presenter

import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.language.DialogText
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclockeditor.view.PriceAlarmClockEditorFragment
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable

/**
 * @date 14/08/2018 6:52 PM
 * @author wcx
 */
class PriceAlarmClockEditorPresenter(override val fragment: PriceAlarmClockEditorFragment)
  : BasePresenter<PriceAlarmClockEditorFragment>() {

  override fun onFragmentViewCreated() {
    super.onFragmentViewCreated()
    fragment.getPriceAlarmInfo()?.apply {
    }
  }

  override fun onFragmentDestroy() {
    super.onFragmentDestroy()
  }

  fun modifyAlarmClock(
    newPriceAlarmClockInfo: PriceAlarmClockTable,
    callback: () -> Unit
  ) {
    PriceAlarmClockTable.getAllPriceAlarm {
      val priceAlarmClockTableList = it
      val oldPriceAlarmClockTable = priceAlarmClockTableList[newPriceAlarmClockInfo.position]
      oldPriceAlarmClockTable.position = newPriceAlarmClockInfo.position

      filterRepeatData(newPriceAlarmClockInfo) {
        if (this) { // 希望修改的数据类型在数据库中已存在
          if (oldPriceAlarmClockTable.addId == "0") {
            modifyExistOldData(
              oldPriceAlarmClockTable,
              newPriceAlarmClockInfo,
              callback
            )
          } else {
            filterRepeatData(oldPriceAlarmClockTable) {
              if (this) { // 原始数据类型在数据库中已存在
                modifyExistOldData(
                  oldPriceAlarmClockTable,
                  newPriceAlarmClockInfo,
                  callback
                )
              } else { // 原始数据类型在数据库中不存在
                GoldStoneAPI.deleteAlarmClock(
                  oldPriceAlarmClockTable,
                  {
                    showNetworkExceptionDialog()
                  }
                ) {
                  if (it?.code == 0) {
                    modifyExistOldData(
                      oldPriceAlarmClockTable,
                      newPriceAlarmClockInfo,
                      callback
                    )
                  }
                }
              }
            }
          }
        } else { // 希望修改的数据类型在数据库中不存在
          if (oldPriceAlarmClockTable.addId == "0") {
            modifyNonexistentOldData(
              oldPriceAlarmClockTable,
              newPriceAlarmClockInfo,
              callback)
          } else {
            filterRepeatData(oldPriceAlarmClockTable) {
              if (this) { // 原始数据类型在数据库中已存在
                oldPriceAlarmClockTable.addId = "0"
                modifyNonexistentOldData(
                  oldPriceAlarmClockTable,
                  newPriceAlarmClockInfo,
                  callback)
              } else { // 原始数据类型在数据库中不存在
                GoldStoneAPI.deleteAlarmClock(
                  oldPriceAlarmClockTable,
                  {
                    showNetworkExceptionDialog()
                  }) {
                  if (it?.code == 0) {
                    oldPriceAlarmClockTable.addId = "0"
                    modifyNonexistentOldData(
                      oldPriceAlarmClockTable,
                      newPriceAlarmClockInfo,
                      callback)
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private fun modifyExistOldData(
    oldPriceAlarmClockTable: PriceAlarmClockTable,
    newPriceAlarmClockInfo: PriceAlarmClockTable,
    callback: () -> Unit
  ) {
    oldPriceAlarmClockTable.apply {
      addId = newPriceAlarmClockInfo.addId
      price = newPriceAlarmClockInfo.price
      priceType = newPriceAlarmClockInfo.priceType
      alarmType = newPriceAlarmClockInfo.alarmType
    }.let {
      PriceAlarmClockTable.updatePriceAlarm(oldPriceAlarmClockTable) {
        callback()
      }
    }
  }

  private fun modifyNonexistentOldData(
    oldPriceAlarmClockTable: PriceAlarmClockTable,
    newPriceAlarmClockInfo: PriceAlarmClockTable,
    callback: () -> Unit
  ) {
    oldPriceAlarmClockTable.apply {
      price = newPriceAlarmClockInfo.price
      priceType = newPriceAlarmClockInfo.priceType
      alarmType = newPriceAlarmClockInfo.alarmType
    }.let {
      PriceAlarmClockTable.updatePriceAlarm(oldPriceAlarmClockTable) {
        callback()
        GoldStoneAPI.addAlarmClock(
          it,
          {
            showNetworkExceptionDialog()
          }
        ) {
          it?.id?.let {
            oldPriceAlarmClockTable.addId = it
            PriceAlarmClockTable.updatePriceAlarm(oldPriceAlarmClockTable) {
              callback()
            }
          }
        }
      }
    }
  }


  // 过滤本地数据库检查是否有相同类型数据
  private fun filterRepeatData(
    priceAlarmClockTable: PriceAlarmClockTable,
    callback: Boolean.() -> Unit
  ) {
    PriceAlarmClockTable.getAllPriceAlarm {
      val size = it.size - 1
      if (size == -1) {
        callback(false)
      } else {
        loop@ for (index: Int in 0..size) {
          if (index == size) {
            if (priceAlarmClockTable.position != index) {
              if (it[index].addId != "0") {
                if (priceAlarmClockTable.price == it[index].price
                  && priceAlarmClockTable.priceType == it[index].priceType
                  && priceAlarmClockTable.pair == it[index].pair) {
                  priceAlarmClockTable.addId = it[index].addId
                  callback(true)
                } else {
                  callback(false)
                }
              } else {
                callback(false)
              }
            } else {
              callback(false)
            }
          } else {
            if (priceAlarmClockTable.position != index) {
              if (it[index].addId != "0") {
                if (priceAlarmClockTable.price == it[index].price
                  && priceAlarmClockTable.priceType == it[index].priceType
                  && priceAlarmClockTable.pair == it[index].pair) {
                  priceAlarmClockTable.addId = it[index].addId
                  callback(true)
                  break@loop
                }
              }
            }
          }
        }
      }
    }
  }

  private fun showNetworkExceptionDialog() {
    NetworkUtil.hasNetwork(fragment.context) isTrue {
    } otherwise {
      fragment.context?.let {
        GoldStoneDialog.show(it) {
          showOnlyConfirmButton {
            GoldStoneDialog.remove(it)
          }
          setImage(R.drawable.network_browken_banner)
          setContent(
            DialogText.networkTitle,
            DialogText.networkDescription
          )
        }
      }
    }
  }
}
