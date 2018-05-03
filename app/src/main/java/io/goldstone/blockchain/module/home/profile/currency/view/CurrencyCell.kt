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
      "CNY" -> R.drawable.china_icon
      "JPY" -> R.drawable.japan_icon
      "KRW" -> R.drawable.korea_icon
      "RUB" -> R.drawable.russia_icon
      else -> R.drawable.amercia_icon
    }
    showIcon(image)
  }

}

