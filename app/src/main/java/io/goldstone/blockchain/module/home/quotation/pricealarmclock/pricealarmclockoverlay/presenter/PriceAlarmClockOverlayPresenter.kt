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
      }
    }
  }

}