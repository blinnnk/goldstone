package io.goldstone.blockchain.module.home.profile.currency.view

import android.content.Context
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
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
    val image = when(model.symbol) {
      "CNY" -> R.drawable.ic_china
      "JPY" -> R.drawable.ic_japan
      "KRW" -> R.drawable.ic_korea
      "RUB" -> R.drawable.ic_russia
      else -> R.drawable.ic_usa
    }
    showIcon(image)
  }

}

