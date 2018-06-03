package io.goldstone.blockchain.module.home.profile.lanaguage.view

import android.content.Context
import com.blinnnk.util.observing
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.base.BaseRadioCell
import io.goldstone.blockchain.common.value.HoneyLanguage
import io.goldstone.blockchain.module.home.profile.lanaguage.model.LanguageModel

/**
 * @date 26/03/2018 6:42 PM
 * @author KaySaith
 */

class LanguageCell(context: Context) : BaseRadioCell(context) {

  var model: LanguageModel by observing(LanguageModel()) {
    title.text = model.name
    checkedStatus = GoldStoneApp.getCurrentLanguage() == HoneyLanguage.getLanguageCode(model.name)
  }

}