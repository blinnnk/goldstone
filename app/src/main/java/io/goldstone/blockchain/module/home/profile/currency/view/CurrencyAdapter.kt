package io.goldstone.blockchain.module.home.profile.currency.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable

/**
 * @date 26/03/2018 2:26 PM
 * @author KaySaith
 */

class CurrencyAdapter(
  override val dataSet: ArrayList<SupportCurrencyTable>,
  private val callback: (CurrencyCell, Int) -> Unit
  ) : HoneyBaseAdapter<SupportCurrencyTable, CurrencyCell>() {

  override fun generateCell(context: Context) = CurrencyCell(context)

  override fun CurrencyCell.bindCell(data: SupportCurrencyTable, position: Int) {
    model = data
    callback(this, position)
  }

}