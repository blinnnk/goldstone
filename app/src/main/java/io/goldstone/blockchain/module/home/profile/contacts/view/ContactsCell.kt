package io.goldstone.blockchain.module.home.profile.contacts.view

import android.content.Context
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.baseInfocell.BaseValueCell
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.module.home.profile.contacts.model.ContactsModel

/**
 * @date 26/03/2018 1:37 PM
 * @author KaySaith
 */

class ContactsCell(context: Context) : BaseValueCell(context) {

  var model: ContactsModel by observing(ContactsModel()) {
    icon.glideImage(model.avatar)
    info.apply {
      title.text = model.name
      subtitle.text = model.address
    }
  }

  init {
    setGrayStyle()
    hasArrow = false
  }

}