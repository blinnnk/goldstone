package io.goldstone.blockchain.module.common.tokenpayment.addressselection.view

import android.content.Context
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.baseInfocell.BaseValueCell
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable

/**
 * @date 28/03/2018 9:25 AM
 * @author KaySaith
 */

class AddressSelectionCell(context: Context) : BaseValueCell(context) {

  var model: ContactTable by observing(ContactTable()) {
    icon.glideImage(model.avatar)
    info.apply {
      title.text = model.name
      subtitle.text = CryptoUtils.scaleMiddleAddress(model.address)
    }
  }

  init {
    setGrayStyle()
    hasArrow = true
  }

}