package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclockoverlay.view

import android.os.Handler
import android.os.Parcelable
import android.view.ViewGroup
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.language.AlarmClockText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclockoverlay.presenter.PriceAlarmClockOverlayPresenter

/**
 * @date 15/08/2018 10:52 AM
 * @author wcx
 */
class PriceAlarmClockOverlayFragment : BaseOverlayFragment<PriceAlarmClockOverlayPresenter>() {

  private val priceAlarmClockEditorInfo by lazy {
    arguments?.getSerializable(ArgumentKey.priceAlarmClockEditorInfo) as? PriceAlarmClockTable
  }

  override val presenter = PriceAlarmClockOverlayPresenter(this)

  override fun ViewGroup.initView() {
    overlayView.header.showdeleteButton(true) {
      // 删除点击事件
      presenter.deleteAlarmClock(priceAlarmClockEditorInfo)
    }
    presenter.showPriceAlarmClockEditorFragment(priceAlarmClockEditorInfo)

    headerTitle = AlarmClockText.alarmEditor
  }

  fun getHandler(): Handler? {
    val handler: Parcelable = arguments?.getParcelable(ArgumentKey.priceAlarmClockListHandler) as Parcelable
    return handler as Handler
  }
}