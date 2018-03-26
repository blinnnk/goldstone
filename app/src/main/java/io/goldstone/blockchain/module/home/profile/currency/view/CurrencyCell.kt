package io.goldstone.blockchain.module.home.profile.currency.view

import android.content.Context
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.BaseRadioCell
import io.goldstone.blockchain.module.home.profile.currency.model.CurrencyModel

/**
 * @date 26/03/2018 2:26 PM
 * @author KaySaith
 */

class CurrencyCell(context: Context) : BaseRadioCell(context) {

  var model: CurrencyModel by observing(CurrencyModel()) {
    title.text = model.symbol
    checkedStatus = model.isChecked
  }

}

