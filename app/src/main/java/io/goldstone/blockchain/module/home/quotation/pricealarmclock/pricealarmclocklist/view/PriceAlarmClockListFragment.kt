package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.common.base.baseoverlayfragment.overlayview.OverlayView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.component.PriceAlarmClockCreatorView
import io.goldstone.blockchain.common.component.overlay.DashboardOverlay
import io.goldstone.blockchain.common.language.AlarmClockText
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.module.home.quotation.markettokencenter.view.MarketTokenCenterFragment
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.presenter.PriceAlarmClockListPresenter
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @data 08/08/2018 4:49 PM
 * @author wcx
 * @description 价格闹钟列表界面
 */
class PriceAlarmClockListFragment : BaseRecyclerFragment<
        PriceAlarmClockListPresenter,
        PriceAlarmClockTable
        >() {
  lateinit var priceAlarmClockListAdapter: PriceAlarmClockListAdapter
  private var nowAlarmSize = 1
  private var maxAlarmSize = 8
  private var priceAlarmClockCreatorView: PriceAlarmClockCreatorView? = null
  private var automaticChoosePriceTypeFlag = false
  override val presenter: PriceAlarmClockListPresenter = PriceAlarmClockListPresenter(this)
  private val quotationModel: QuotationModel = QuotationModel(
    "EOS",
    "eos",
    "7.0236",
    "-3.642",
    ArrayList(),
    "binance",
    1.0,
    "EOS/USDT",
    "eosusdt_binance",
    "usdt",
    "0x86fa049857e0209aa7d9e616f7eb3b3b78ecfdb0",
    false
  )

  override fun setRecyclerViewAdapter(
    recyclerView: BaseRecyclerView,
    asyncData: ArrayList<PriceAlarmClockTable>?
  ) {
    priceAlarmClockListAdapter = PriceAlarmClockListAdapter(asyncData.orEmptyArray()) {
      switchImageView.isChecked = model.status!!
      switchImageView.setOnClickListener {
        val checked = switchImageView.isChecked
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
    savedInstanceState: Bundle?) {
    super.onViewCreated(
      view,
      savedInstanceState
    )
    getParentFragment<MarketTokenCenterFragment> {
      getParentFragment<QuotationOverlayFragment> {
        overlayView.header.showAddButton(true) {
          showAddAlarmClockDashboard(overlayView)
        }
      }
    }
  }

  private fun showAddAlarmClockDashboard(overlayView: OverlayView) {
    if (nowAlarmSize < maxAlarmSize) {
      val priceAlarmClockTable = PriceAlarmClockTable(
        0,
        "0",
        null,
        quotationModel.exchangeName,
        quotationModel.quoteSymbol,
        quotationModel.price.toDouble(),
        quotationModel.price.toDouble(),
        false,
        quotationModel.pair,
        0,
        0,
        quotationModel.pairDisplay
      )
      overlayView.apply {
        DashboardOverlay(context) {
          priceAlarmClockCreatorView = PriceAlarmClockCreatorView(context).apply {
            setAutomaticChoosePriceType(automaticChoosePriceTypeFlag)
            setTitle(AlarmClockText.createNewAlarm)
            setPriceType(priceAlarmClockTable.priceType!!)
            setTargetPriceEditTextListener(
              priceAlarmClockTable.price,
              priceAlarmClockTable.marketPrice,
              priceAlarmClockTable.currencyName
            )
            setPriceChooseContent(
              priceAlarmClockTable.price,
              priceAlarmClockTable.currencyName
            )
            setCurrencyName(priceAlarmClockTable.currencyName)
            setAlarmChooseContent(priceAlarmClockTable.alarmType)
            val moreThanCell = getMoreThanCell()
            val lessThanCell = getLessThanCell()
            moreThanCell.setOnClickListener {
              moreThanCell.setSwitchStatusBy(true)
              lessThanCell.setSwitchStatusBy(false)
              setPriceType(0)
              automaticChoosePriceTypeFlag = true
              setAutomaticChoosePriceType(automaticChoosePriceTypeFlag)
            }
            lessThanCell.setOnClickListener {
              moreThanCell.setSwitchStatusBy(false)
              lessThanCell.setSwitchStatusBy(true)
              setPriceType(1)
              automaticChoosePriceTypeFlag = true
              setAutomaticChoosePriceType(automaticChoosePriceTypeFlag)
            }
          }
          (priceAlarmClockCreatorView as LinearLayout).into(this)
        }.apply {
          confirmEvent = Runnable {
            // 点击事件
            val formatEnglishDate = TimeUtils.formatEnglishDate(System.currentTimeMillis())
            priceAlarmClockTable.price = priceAlarmClockCreatorView?.getTargetPriceEditText()?.text.toString().toDouble()
            priceAlarmClockTable.priceType = priceAlarmClockCreatorView?.getPriceType()
            priceAlarmClockTable.alarmType = priceAlarmClockCreatorView?.getAlarmTypeView()?.getAlarmType()
            priceAlarmClockTable.createTime = formatEnglishDate
            priceAlarmClockTable.status = true
            presenter.addAlarmClock(priceAlarmClockTable) {
              presenter.getDatabaseDataRefreshList() {
                nowAlarmSize = this.size
              }
            }
          }
        }.into(this)
      }
    } else {
      Toast.makeText(context, "已达到闹铃设置最大个数", Toast.LENGTH_LONG).show();
    }
  }
}
