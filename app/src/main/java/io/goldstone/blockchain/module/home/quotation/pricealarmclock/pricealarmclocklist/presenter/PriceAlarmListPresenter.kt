package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.presenter

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.os.Parcel
import android.os.Parcelable
import com.blinnnk.extension.isTrue
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.AlarmConfigListModel
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmTable
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.view.PriceAlarmListAdapter
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.view.PriceAlarmListFragment

/**
 * @data 07/23/2018 16/32
 * @author wcx
 * @description 价格闹钟Presenter实现类
 */
class PriceAlarmListPresenter(override val fragment: PriceAlarmListFragment)
	: BaseRecyclerPresenter<PriceAlarmListFragment, PriceAlarmTable>() {

	companion object {
		fun insertAlarmClockToDatabase(
			priceAlarmTable: PriceAlarmTable,
			callback: () -> Unit
		) {
			// 临时替代属性
			priceAlarmTable.addId
			callback()
		}

		fun getDatabaseDataRefreshList(callback: ArrayList<PriceAlarmTable>. () -> Unit) {
			// 临时替代属性
			callback(ArrayList<PriceAlarmTable>())
		}

		fun updateData() {

		}
	}

	private val handler: Handler = @SuppressLint("HandlerLeak")
	object : Handler(), Parcelable {
		override fun writeToParcel(p0: Parcel?, p1: Int) {
		}

		override fun describeContents(): Int {
			return 0
		}

		override fun handleMessage(msg: Message?) {
			super.handleMessage(msg)
			fragment.asyncData?.let { fragment.presenter.diffAndUpdateAdapterData<PriceAlarmListAdapter>(it) }
			updateData()
			getExistingAlarmAmount {
				fragment.setNowAlarmSize(this)
			}
		}
	}

	override fun updateData() {
	}

	// 修改闹钟属性
	fun showPriceAlarmModifierFragment(model: PriceAlarmTable) {
		// 临时替代属性
		model.addId
	}

	// 获取闹铃配置清单
	fun getAlarmConfigList(callback: Int.() -> Unit) {
		val alarmConfigListModel = AlarmConfigListModel(
			0,
			ArrayList<AlarmConfigListModel.ListModel>()
		)
		val alarmConfigArrayList = alarmConfigListModel.list
		val size = alarmConfigArrayList.size
		for (index: Int in 0 until size) {
			if (alarmConfigArrayList[index].name == "alertMaxCount") {
				callback(alarmConfigArrayList[index].value.toInt())
			}
		}
	}

	// 修改闹铃提醒开关状态
	fun modifyOpenStatus(
		model: PriceAlarmTable,
		checked: Boolean
	) {
		// 临时替代属性
		model.addId
		checked.isTrue { }
	}

	// 获取当前闹铃个数
	fun getExistingAlarmAmount(callback: Int.() -> Unit) {
		// 临时替代属性
		callback(1)
	}

}