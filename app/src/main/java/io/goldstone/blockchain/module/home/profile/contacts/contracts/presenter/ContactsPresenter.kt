package io.goldstone.blockchain.module.home.profile.contacts.contracts.presenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.utils.showAlertView
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
	
	fun deleteContact(id: Int) {
		fragment.context?.showAlertView(
			ProfileText.deletContactAlertTitle,
			ProfileText.deleteContactAlertDescription,
			false
		) {
			ContactTable.deleteContactByID(id) {
				updateAddressList()
			}
		}
	}
	
	private fun updateAddressList() {
		ContactTable.getAllContacts {
			it.isEmpty() isTrue {
				if (fragment.asyncData.isNull()) fragment.asyncData = it
				else {
					diffAndUpdateSingleCellAdapterData<ContactsAdapter>(it)
				}
			} otherwise {
				if (fragment.asyncData.isNull()) {
					fragment.asyncData = it
				} else {
					diffAndUpdateSingleCellAdapterData<ContactsAdapter>(it)
				}
			}
		}
	}
}