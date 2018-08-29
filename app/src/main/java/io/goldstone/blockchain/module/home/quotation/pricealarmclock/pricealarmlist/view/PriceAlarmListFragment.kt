package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmlist.view

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
import io.goldstone.blockchain.common.language.AlarmText
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.home.quotation.markettokencenter.view.MarketTokenCenterFragment
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmlist.model.PriceAlarmTable
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmlist.presenter.PriceAlarmListPresenter
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
		wrapper.layoutParams.width = ScreenSize.widthWithPadding
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
				presenter.modifyOpenStatus(
					model,
					checked
				)
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
				var priceAlarmCreaterView: PriceAlarmCreaterView? = null
				overlayView.apply {
					DashboardOverlay(context) {
						priceAlarmCreaterView = PriceAlarmCreaterView(context).apply {
							setTitle(AlarmText.createNewAlarm)
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
						(priceAlarmCreaterView as LinearLayout).into(this)
					}.apply {
						confirmEvent = Runnable {
							// 点击事件
							val formatDate = TimeUtils.formatDate(System.currentTimeMillis())
							priceAlarmTable.apply {
								price = priceAlarmCreaterView?.getTargetPriceEditTextContent() ?: ""
								priceType = priceAlarmCreaterView?.getPriceType() ?: ArgumentKey.greaterThanForPriceType
								alarmType = priceAlarmCreaterView?.getAlarmTypeView()?.getAlarmType() ?: ArgumentKey.repeatingForAlarm
								createTime = formatDate
								status = true
							}
							PriceAlarmListPresenter.insertAlarmToDatabase(priceAlarmTable) {
								PriceAlarmListPresenter.getLocalDataRefreshList() {
									PriceAlarmListPresenter.updateData()
									currentAlarmSize = this.size
								}
							}
						}
					}.into(this)
				}
			} else {
				overlayView.context?.toast(AlarmText.alarmNumberMaximumPrompt)
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
