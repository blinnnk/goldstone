package io.goldstone.blockchain.module.home.profile.contacts.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.profile.contacts.model.ContactsModel

/**
 * @date 26/03/2018 1:38 PM
 * @author KaySaith
 */

class ContactsAdapter(
  override val dataSet: ArrayList<ContactsModel>
  ) : HoneyBaseAdapter<ContactsModel, ContactsCell>() {

  override fun generateCell(context: Context) = ContactsCell(context)

  override fun ContactsCell.bindCell(data: ContactsModel, position: Int) {
    model = data
  }


}