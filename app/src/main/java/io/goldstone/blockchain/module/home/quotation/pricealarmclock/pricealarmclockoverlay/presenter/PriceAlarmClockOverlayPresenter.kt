package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclockoverlay.presenter

import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.language.AlarmClockText
import io.goldstone.blockchain.common.language.DialogText
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclockeditor.view.PriceAlarmClockEditorFragment
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclockoverlay.view.PriceAlarmClockOverlayFragment

/**
 * @date 15/08/2018 10:52 AM
 * @author wcx
 */
class PriceAlarmClockOverlayPresenter(override val fragment: PriceAlarmClockOverlayFragment)
  : BaseOverlayPresenter<PriceAlarmClockOverlayFragment>() {

  fun showPriceAlarmClockEditorFragment(priceAlarmClockEditorInfo: PriceAlarmClockTable?) {
    // 详情
    fragment.addFragmentAndSetArgument<PriceAlarmClockEditorFragment>(ContainerID.content) {
      putSerializable(
        ArgumentKey.priceAlarmClockEditorInfo,
        priceAlarmClockEditorInfo
      )
    }
  }

  fun deleteAlarmClock(priceAlarmClockEditorInfo: PriceAlarmClockTable?) {
    priceAlarmClockEditorInfo?.let {
      fragment.context?.showAlertView(
        AlarmClockText.confirmDelete,
        AlarmClockText.confirmDeleteEditorContent,
        false,
        {}
      ) {
        PriceAlarmClockTable.getAllPriceAlarm {
          val priceAlarmClockTable = it[priceAlarmClockEditorInfo.position]
          priceAlarmClockTable.position = priceAlarmClockEditorInfo.position

          if (priceAlarmClockEditorInfo.addId == "0") {
            deleteExistData(priceAlarmClockTable)
          } else {
            filterRepeatData(priceAlarmClockTable) {
              if (this) {
                deleteExistData(priceAlarmClockTable)
              } else {
                GoldStoneAPI.deleteAlarmClock(
                  priceAlarmClockEditorInfo,
                  {
                    showNetworkExceptionDialog()
                  }
                ) {
                  if (it?.code == 0) {
                    deleteExistData(priceAlarmClockTable)
                  } else {
                    showNetworkExceptionDialog()
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private fun deleteExistData(priceAlarmClockTable: PriceAlarmClockTable) {
    PriceAlarmClockTable.deleteAlarm(priceAlarmClockTable) {
      removeSelfFromActivity()
      fragment.getHandler()?.sendEmptyMessage(0)
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
            GoldStoneDialog.remove(context)
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