package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.overlayview.OverlayView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.component.PriceAlarmCreaterView
import io.goldstone.blockchain.common.component.overlay.DashboardOverlay
import io.goldstone.blockchain.common.language.AlarmClockText
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ValueTag
import io.goldstone.blockchain.module.home.quotation.markettokencenter.view.MarketTokenCenterFragment
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmTable
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.presenter.PriceAlarmListPresenter
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

/**
 * @data 08/08/2018 4:49 PM
 * @author wcx
 * @description 价格闹钟列表界面
 */
class PriceAlarmListFragment : BaseRecyclerFragment<PriceAlarmListPresenter, PriceAlarmTable>() {
	private lateinit var priceAlarmClockListAdapter: PriceAlarmListAdapter
	override val presenter: PriceAlarmListPresenter = PriceAlarmListPresenter(this)
	private val quotationModel by lazy { getParentFragment<MarketTokenCenterFragment>()?.currencyInfo }

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<PriceAlarmTable>?
	) {
		presenter.getAlarmConfigList {
			maxAlarmSize = this
		}
		presenter.getExistingAlarmAmount {
			currentAlarmSize = this
		}
		priceAlarmClockListAdapter = PriceAlarmListAdapter(asyncData.orEmptyArray()) {
			getSwitch().isChecked = model.status
			getSwitch().onClick {
				val checked = getSwitch().isChecked
				if (checked) {
					// 打开
					presenter.modifyOpenStatus(
						model,
						checked)
				} else {
					// 关闭
					presenter.modifyOpenStatus(
						model,
						checked)
				}
				preventDuplicateClicks()
			}
			onClick {
				presenter.showPriceAlarmModifierFragment(model)
				preventDuplicateClicks()
			}
		}
		recyclerView.adapter = priceAlarmClockListAdapter
	}

	@SuppressLint("ResourceType")
	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(
			view,
			savedInstanceState
		)
	}

	companion object {
		private var currentAlarmSize = 1
		private var maxAlarmSize = 50

		fun showAddAlarmClockDashboard(
			overlayView: OverlayView,
			quotationModel: QuotationModel
		) {
			if (currentAlarmSize < maxAlarmSize) {
				val priceAlarmTable = PriceAlarmTable(quotationModel)
				var priceAlarmClockCreatorView: PriceAlarmCreaterView? = null
				overlayView.apply {
					DashboardOverlay(context) {
						priceAlarmClockCreatorView = PriceAlarmCreaterView(context).apply {
							setTitle(AlarmClockText.createNewAlarm)
							priceAlarmTable.priceType.let {
								setPriceType(it)
							}
							setTargetPriceEditTextListener(priceAlarmTable)
							setPriceChooseContent(priceAlarmTable)
							setCurrencyName(priceAlarmTable.currencyName)
							setAlarmChooseContent(priceAlarmTable.alarmType)
							val greaterThanPriceCell = getGreaterThanPriceCell()
							val lessThanPriceCell = getLessThanPriceCell()
							greaterThanPriceCell.setOnClickListener {
								greaterThanPriceCell.setSwitchStatusBy(true)
								lessThanPriceCell.setSwitchStatusBy(false)
								setPriceType(ArgumentKey.greaterThanForPriceType)
								setAutomaticChoosePriceType(true)
							}
							lessThanPriceCell.setOnClickListener {
								greaterThanPriceCell.setSwitchStatusBy(false)
								lessThanPriceCell.setSwitchStatusBy(true)
								setPriceType(1)
								setAutomaticChoosePriceType(true)
							}
						}
						(priceAlarmClockCreatorView as LinearLayout).into(this)
					}.apply {
						confirmEvent = Runnable {
							// 点击事件
							val formatDate = TimeUtils.formatDate(System.currentTimeMillis())
							priceAlarmTable.price = priceAlarmClockCreatorView?.getTargetPriceEditTextContent() ?: ""
							priceAlarmTable.priceType = priceAlarmClockCreatorView?.getPriceType() ?: ArgumentKey.greaterThanForPriceType
							priceAlarmTable.alarmType = priceAlarmClockCreatorView?.getAlarmTypeView()?.getAlarmType() ?: ArgumentKey.repeatingForAlarm
							priceAlarmTable.createTime = formatDate
							priceAlarmTable.status = true
							PriceAlarmListPresenter.insertAlarmClockToDatabase(priceAlarmTable) {
								PriceAlarmListPresenter.getDatabaseDataRefreshList {
									PriceAlarmListPresenter.updateData()
									currentAlarmSize = this.size
								}
							}
						}
					}.into(this)
				}
			} else {
				overlayView.context?.toast("已达到闹铃设置最大个数")
			}
		}
	}

	fun setNowAlarmSize(nowAlarmSize: Int) {
		currentAlarmSize = nowAlarmSize
	}

	fun getModel(): QuotationModel? {
		return quotationModel
	}

}
