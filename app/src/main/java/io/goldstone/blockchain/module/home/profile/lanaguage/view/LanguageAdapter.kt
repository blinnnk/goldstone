package io.goldstone.blockchain.module.home.profile.lanaguage.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.profile.lanaguage.model.LanguageModel

/**
 * @date 26/03/2018 6:41 PM
 * @author KaySaith
 */

class LanguageAdapter(
  override val dataSet: ArrayList<LanguageModel>,
  private val callback: (LanguageCell, Int) -> Unit
  ) : HoneyBaseAdapter<LanguageModel, LanguageCell>() {

  override fun generateCell(context: Context) = LanguageCell(context)

  override fun LanguageCell.bindCell(data: LanguageModel, position: Int) {
    model = data
    callback(this, position)
  }

}