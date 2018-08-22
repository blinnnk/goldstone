package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.presenter

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.os.Parcel
import android.os.Parcelable
import com.blinnnk.extension.isTrue
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.AlarmConfigListModel
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.view.PriceAlarmClockListFragment

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
	}

	fun getDatabaseDataRefreshList(callback: ArrayList<PriceAlarmClockTable>. () -> Unit) {
		callback(ArrayList<PriceAlarmClockTable>())
	}

	fun addDatabaseAlarmClock(
		priceAlarmClockBean: PriceAlarmClockTable,
		callback: () -> Unit
	) {
		priceAlarmClockBean.addId
		callback()
	}

	// 修改闹钟属性
	fun showPriceAlarmModifierFragment(model: PriceAlarmClockTable) {
		model.addId
	}

	// 获取闹铃配置清单
	fun getAlarmConfigList(callback: AlarmConfigListModel.() -> Unit) {
		callback(AlarmConfigListModel(0, ArrayList<AlarmConfigListModel.ListBean>()))
	}

	// 修改闹铃提醒开关状态
	fun modifyOpenStatus(
		model: PriceAlarmClockTable,
		checked: Boolean
	) {
		model.addId
		checked.isTrue { }
	}

	// 获取当前闹铃个数
	fun getExistingAlarmAmount(callback: Int.() -> Unit) {
		callback(1)
	}

}