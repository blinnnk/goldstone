package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.overlayview.OverlayView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.component.PriceAlarmClockCreaterView
import io.goldstone.blockchain.common.component.overlay.DashboardOverlay
import io.goldstone.blockchain.common.language.AlarmClockText
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.value.ValueTag
import io.goldstone.blockchain.module.home.quotation.markettokencenter.view.MarketTokenCenterFragment
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.presenter.PriceAlarmClockListPresenter
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import org.jetbrains.anko.wrapContent

/**
 * @data 08/08/2018 4:49 PM
 * @author wcx
 * @description 价格闹钟列表界面
 */
class PriceAlarmClockListFragment : BaseRecyclerFragment<PriceAlarmClockListPresenter, PriceAlarmClockTable>() {
	private lateinit var priceAlarmClockListAdapter: PriceAlarmClockListAdapter
	private var nowAlarmSize = 1
	private var maxAlarmSize = 50
	private var priceAlarmClockCreatorView: PriceAlarmClockCreaterView? = null
	override val presenter: PriceAlarmClockListPresenter = PriceAlarmClockListPresenter(this)
	private val quotationModel by lazy { getParentFragment<MarketTokenCenterFragment>()?.currencyInfo }

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<PriceAlarmClockTable>?
	) {
		recyclerView.layoutParams = RelativeLayout.LayoutParams(
			matchParent,
			wrapContent
		).apply {
			topMargin = 15.uiPX()
		}
		presenter.getAlarmConfigList {
			maxAlarmSize = this
		}
		presenter.getExistingAlarmAmount {
			nowAlarmSize = this
		}
		priceAlarmClockListAdapter = PriceAlarmClockListAdapter(asyncData.orEmptyArray()) {
			model.status.let {
				getSwitch().isChecked = it
			}
			getSwitch().setOnClickListener {
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
		getParentFragment<MarketTokenCenterFragment> {
			getParentFragment<QuotationOverlayFragment> {
				overlayView.header.showAddButton(true) {
					quotationModel?.let {
						showAddAlarmClockDashboard(overlayView, it)
					}
				}
			}
		}
	}

	private fun showAddAlarmClockDashboard(
		overlayView: OverlayView,
		quotationModel: QuotationModel
	) {
		if (nowAlarmSize < maxAlarmSize) {
			val priceAlarmClockTable = PriceAlarmClockTable(quotationModel)
			overlayView.apply {
				DashboardOverlay(context) {
					priceAlarmClockCreatorView = PriceAlarmClockCreaterView(context).apply {
						setTitle(AlarmClockText.createNewAlarm)
						priceAlarmClockTable.priceType.let {
							setPriceType(it)
						}
						setTargetPriceEditTextListener(priceAlarmClockTable)
						setPriceChooseContent(priceAlarmClockTable)
						setCurrencyName(priceAlarmClockTable.currencyName)
						setAlarmChooseContent(priceAlarmClockTable.alarmType)
						val greaterThanPriceCell = getGreaterThanPriceCell()
						val lessThanPriceCell = getLessThanPriceCell()
						greaterThanPriceCell.setOnClickListener {
							greaterThanPriceCell.setSwitchStatusBy(true)
							lessThanPriceCell.setSwitchStatusBy(false)
							setPriceType(0)
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
						priceAlarmClockTable.price = priceAlarmClockCreatorView?.getTargetPriceEditTextContent() ?: ""
						priceAlarmClockTable.priceType = priceAlarmClockCreatorView?.getPriceType() ?: 0
						priceAlarmClockTable.alarmType = priceAlarmClockCreatorView?.getAlarmTypeView()?.getAlarmType() ?: 0
						priceAlarmClockTable.createTime = formatDate
						priceAlarmClockTable.status = true
						presenter.insertAlarmClockToDatabase(priceAlarmClockTable) {
							presenter.getDatabaseDataRefreshList {
								presenter.updateData()
								nowAlarmSize = this.size
							}
						}
					}
				}.into(this)
			}
		} else {
			context?.toast("已达到闹铃设置最大个数")
		}
	}

	fun setNowAlarmSize(nowAlarmSize: Int) {
		this.nowAlarmSize = nowAlarmSize
	}

	fun getModel(): QuotationModel? {
		return quotationModel
	}

}
