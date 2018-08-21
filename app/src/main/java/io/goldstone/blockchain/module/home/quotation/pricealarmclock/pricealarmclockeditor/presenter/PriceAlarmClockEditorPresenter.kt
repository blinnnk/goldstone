package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclockeditor.presenter

import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.language.DialogText
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclockeditor.view.PriceAlarmClockEditorFragment
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable

/**
 * @date 14/08/2018 6:52 PM
 * @author wcx
 */
class PriceAlarmClockEditorPresenter(override val fragment: PriceAlarmClockEditorFragment)
  : BasePresenter<PriceAlarmClockEditorFragment>() {

  override fun onFragmentViewCreated() {
    super.onFragmentViewCreated()
    fragment.getPriceAlarmInfo()?.apply {
    }
  }

  fun modifyAlarmClock(
    newPriceAlarmClockInfo: PriceAlarmClockTable,
    callback: () -> Unit
  ) {
    newPriceAlarmClockInfo.addId
    callback()
  }

}
