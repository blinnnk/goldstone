package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.presenter

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.os.Parcel
import android.os.Parcelable
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.language.DialogText
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.AlarmConfigListModel
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable.Companion.insertPriceAlarm
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.view.PriceAlarmClockListAdapter
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.view.PriceAlarmClockListFragment
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclockoverlay.view.PriceAlarmClockOverlayFragment

/**
 * @data 07/23/2018 16/32
 * @author wcx
 * @description 价格闹钟Presenter实现类
 */
class PriceAlarmClockListPresenter(override val fragment: PriceAlarmClockListFragment)
  : BaseRecyclerPresenter<PriceAlarmClockListFragment, PriceAlarmClockTable>() {

  private val handler: Handler = @SuppressLint("HandlerLeak")
  object : Handler(), Parcelable {
    override fun writeToParcel(p0: Parcel?, p1: Int) {
    }

    override fun describeContents(): Int {
      return 0
    }

    override fun handleMessage(msg: Message?) {
      super.handleMessage(msg)
      fragment.getAdapter().notifyDataSetChanged()
      updateData()

      getExistingAlarmAmount {
        fragment.setNowAlarmSize(this)
      }
    }
  }

  override fun updateData() {
    getDatabaseDataRefreshList {
      val priceAlarmClockTableArrayList = this
      val priceAlarmClockSize = priceAlarmClockTableArrayList.size - 1
      // 检查是否有未添加到后台推送列表的闹铃提醒
      for (index: Int in 0..priceAlarmClockSize) {
        val priceAlarmClockTable = priceAlarmClockTableArrayList[index]
        if (priceAlarmClockTable.addId == "0" && priceAlarmClockTable.status) {
          filterRepeatData(priceAlarmClockTable) {
            if (this) {
              updateDatabaseRefreshList(priceAlarmClockTable)
            } else {
              GoldStoneAPI.addAlarmClock(
                priceAlarmClockTable,
                {
                  showNetworkExceptionDialog()
                }
              ) {
                val addAlarmClock = it
                priceAlarmClockTable.addId = addAlarmClock?.id ?: "0"
                updateDatabaseRefreshList(priceAlarmClockTable)
              }
            }
          }
        }
      }
    }
  }

  fun getDatabaseDataRefreshList(callback: ArrayList<PriceAlarmClockTable>. () -> Unit) {
    PriceAlarmClockTable.getAllPriceAlarm {
      val priceAlarmClockTableArrayList = getIdenticalSymbolList(it)
      fragment.asyncData.isNull() isTrue {
        fragment.asyncData = priceAlarmClockTableArrayList
      } otherwise {
        diffAndUpdateSingleCellAdapterData<PriceAlarmClockListAdapter>(priceAlarmClockTableArrayList)
      }
      callback(priceAlarmClockTableArrayList)
    }
  }

  fun getIdenticalSymbolList(it: ArrayList<PriceAlarmClockTable>): ArrayList<PriceAlarmClockTable> {
    val symbol = fragment.getModel()?.symbol
    val exchangeName = fragment.getModel()?.exchangeName
    val priceAlarmClockTableArrayList = ArrayList<PriceAlarmClockTable>()
    val allSize = it.size - 1
    for (index: Int in 0..allSize) {
      if (symbol == it[index].symbol && exchangeName == it[index].marketName) {
        priceAlarmClockTableArrayList.add(it[index])
      }
    }
    return priceAlarmClockTableArrayList
  }

  fun addDatabaseAlarmClock(
    priceAlarmClockBean: PriceAlarmClockTable,
    callback: () -> Unit
  ) {
    insertPriceAlarm(priceAlarmClockBean) {
      callback()
    }
  }

  // 修改闹钟属性
  fun showPriceAlarmModifierFragment(model: PriceAlarmClockTable) {
    model.addId
    fragment.activity?.addFragmentAndSetArguments<PriceAlarmClockOverlayFragment>(ContainerID.main) {
      putSerializable(
        ArgumentKey.priceAlarmClockEditorInfo,
        model
      )
      putParcelable(
        ArgumentKey.priceAlarmClockListHandler,
        handler as Parcelable
      )
    }
  }

  // 获取闹铃配置清单
  fun getAlarmConfigList(callback: AlarmConfigListModel.() -> Unit) {
    fragment.context?.let { _ ->
      GoldStoneAPI.getAlarmConfigList({
        showNetworkExceptionDialog()
      }) {
        it?.let {
          callback(it)
        }
      }
    }
  }

  // 修改闹铃提醒开关状态
  fun modifyOpenStatus(
    model: PriceAlarmClockTable,
    checked: Boolean
  ) {
    getDatabaseDataRefreshList {
      val priceAlarmClockTable = this[model.position]
      priceAlarmClockTable.status = checked
      priceAlarmClockTable.position = model.position
      if (checked) {
        filterRepeatData(priceAlarmClockTable) {
          if (this) {
            updateDatabaseRefreshList(priceAlarmClockTable)
          } else {
            if (priceAlarmClockTable.addId != "0") {
              GoldStoneAPI.deleteAlarmClock(
                priceAlarmClockTable,
                {
                  showNetworkExceptionDialog()
                }
              ) {
                priceAlarmClockTable.addId = "0"
                addNetAlarmClock(priceAlarmClockTable)
              }
            } else {
              addNetAlarmClock(priceAlarmClockTable)
            }
          }
        }
      } else {
        if (priceAlarmClockTable.addId == "0") {
          updateDatabaseRefreshList(priceAlarmClockTable)
        } else {
          filterRepeatData(priceAlarmClockTable) {
            if (this) {
              priceAlarmClockTable.addId = "0"
              updateDatabaseRefreshList(priceAlarmClockTable)
            } else {
              GoldStoneAPI.deleteAlarmClock(
                priceAlarmClockTable,
                {
                  updateDatabaseRefreshList(priceAlarmClockTable)
                  showNetworkExceptionDialog()
                }
              ) {
                priceAlarmClockTable.addId = "0"
                updateDatabaseRefreshList(priceAlarmClockTable)
              }
            }
          }
        }
      }
    }
  }

  private fun addNetAlarmClock(priceAlarmClockTable: PriceAlarmClockTable) {
    GoldStoneAPI.addAlarmClock(
      priceAlarmClockTable,
      {
        updateDatabaseRefreshList(priceAlarmClockTable)
        showNetworkExceptionDialog()
      }
    ) {
      priceAlarmClockTable.addId = it?.id ?: "0"
      updateDatabaseRefreshList(priceAlarmClockTable)
    }
  }

  private fun updateDatabaseRefreshList(priceAlarmClockTable: PriceAlarmClockTable) {
    PriceAlarmClockTable.updatePriceAlarm(priceAlarmClockTable) {
      getDatabaseDataRefreshList {
      }
    }
  }

  // 获取当前闹铃个数
  fun getExistingAlarmAmount(callback: Int.() -> Unit) {
    PriceAlarmClockTable.getAllPriceAlarm {
      callback(it.size)
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
                if (
                  priceAlarmClockTable.price == it[index].price
                  && priceAlarmClockTable.priceType == it[index].priceType
                  && priceAlarmClockTable.pair == it[index].pair
                ) {
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