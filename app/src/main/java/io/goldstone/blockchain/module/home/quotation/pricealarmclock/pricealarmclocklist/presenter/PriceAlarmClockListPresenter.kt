package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.presenter

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration
import android.os.Handler
import android.os.Message
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.language.DialogText
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.AlarmConfigListModel
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable.Companion.deleteAllAlarm
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable.Companion.insertPriceAlarm
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.view.PriceAlarmClockListAdapter
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.view.PriceAlarmClockListFragment
import java.io.Serializable

/**
 * @data 07/23/2018 16/32
 * @author wcx
 * @description 价格闹钟Presenter实现类
 */
class PriceAlarmClockListPresenter(override val fragment: PriceAlarmClockListFragment)
  : BaseRecyclerPresenter<PriceAlarmClockListFragment, PriceAlarmClockTable>() {

  private val handler: Handler = object : Handler(), Serializable {
    override fun handleMessage(msg: Message?) {
      super.handleMessage(msg)
      fragment.priceAlarmClockListAdapter.notifyDataSetChanged()
      updateData()
//      fragment.getParentFragment<PriceAlarmClockOverlayFragment> {
//        getExistingAlarmAmount() {
//          setNowAlarmSize(this)
//        }
//      }
    }
  }
  // 更新数据库版本
  val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL("ALTER TABLE price_alarm_clock "
              + " ADD COLUMN onceFlag TEXT");
    }

  }

  override fun updateData() {
    getDatabaseDataRefreshList {
      val priceAlarmClockTableArrayList = this
      val priceAlarmClockSize = priceAlarmClockTableArrayList.size - 1
      // 检查是否有未添加到后台推送列表的闹铃提醒
      for (index: Int in 0..priceAlarmClockSize) {
        var priceAlarmClockTable = priceAlarmClockTableArrayList[index]
        if (priceAlarmClockTable.addId == "0" && priceAlarmClockTable.status!!) {
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
      fragment.asyncData.isNull() isTrue {
        fragment.asyncData = it
      } otherwise {
        diffAndUpdateSingleCellAdapterData<PriceAlarmClockListAdapter>(it)
      }
      callback(it)
    }
  }

  fun addAlarmClock(priceAlarmClockBean: PriceAlarmClockTable, callback: () -> Unit) {
    insertPriceAlarm(priceAlarmClockBean) {
      callback()
    }
  }

  fun deleteAllAlarmClock(callback: () -> Unit) {
    deleteAllAlarm() {
      callback()
    }
  }

  // 显示价格闹钟
  fun showPriceAlarmModifierFragment(model: PriceAlarmClockTable) {
    model.addId
//    fragment.activity?.addFragmentAndSetArguments<PriceAlarmClockOverlayFragment>(ContainerID.main) {
//      putSerializable(ArgumentKey.priceAlarmClockEditorInfo, model)
//      putSerializable("priceAlarmClockListHandler", handler as Serializable)
//
//    }
  }

  // 获取闹铃配置清单
  fun getAlarmConfigList(callback: AlarmConfigListModel.() -> Unit) {
    fragment.context?.let { _ ->
      GoldStoneAPI.getAlarmConfigList({
        showNetworkExceptionDialog()
      }) {
        callback(it!!)
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
        }
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
      GoldStoneDialog.show(fragment.context!!) {
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