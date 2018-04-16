package io.goldstone.blockchain.module.home.profile.contacts.contracts.presenter

import com.blinnnk.extension.isNullOrEmpty
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.view.ContactFragment
import io.goldstone.blockchain.module.home.profile.contacts.contracts.view.ContactsAdapter

/**
 * @date 26/03/2018 1:36 PM
 * @author KaySaith
 */

class ContactPresenter(
  override val fragment: ContactFragment
  ) : BaseRecyclerPresenter<ContactFragment, ContactTable>() {

  override fun updateData() {
    super.updateData()
    updateAddressList()
  }

  override fun onFragmentShowFromHidden() {
    super.onFragmentShowFromHidden()
    updateAddressList()
  }

  private fun updateAddressList() {
    ContactTable.getAllContacts {
      it.isEmpty().isTrue {
        fragment.asyncData = arrayListOf()
      } otherwise {
        if (fragment.asyncData.isNullOrEmpty()) {
          fragment.asyncData = it
        } else {
          diffAndUpdateSingleCellAdapterData<ContactsAdapter>(it)
        }
      }
    }
  }

}