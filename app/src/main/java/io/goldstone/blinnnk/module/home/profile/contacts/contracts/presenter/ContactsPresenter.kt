package io.goldstone.blinnnk.module.home.profile.contacts.contracts.presenter

import android.os.Bundle
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.blinnnk.extension.toArrayList
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.crypto.multichain.ChainType
import io.goldstone.blinnnk.module.home.profile.contacts.contractinput.view.ContactInputFragment
import io.goldstone.blinnnk.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blinnnk.module.home.profile.contacts.contracts.model.getCurrentAddresses
import io.goldstone.blinnnk.module.home.profile.contacts.contracts.view.ContactFragment
import io.goldstone.blinnnk.module.home.profile.contacts.contracts.view.ContactsAdapter
import io.goldstone.blinnnk.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

	fun deleteContact(id: Int) {
		ContactTable.deleteContactByID(id) {
			updateAddressList()
		}
	}

	fun shoEditContactFragment(contactID: Int) {
		fragment.getParentFragment<ProfileOverlayFragment> {
			presenter.showTargetFragment<ContactInputFragment>(
				Bundle().apply { putInt(ArgumentKey.contactID, contactID) }
			)
		}
	}

	private fun updateAddressList() = GlobalScope.launch(Dispatchers.Default) {
		val contacts =
			ContactTable.dao.getAllContacts()
		val formattedContacts =
			contacts.getCurrentAddresses(ChainType(fragment.chainType.orZero()).getContract()).toArrayList()

		launchUI {
			if (contacts.isEmpty()) {
				if (fragment.asyncData.isNull()) fragment.asyncData = formattedContacts
				else diffAndUpdateSingleCellAdapterData<ContactsAdapter>(formattedContacts)
			} else {
				if (fragment.asyncData.isNull()) fragment.asyncData = contacts.toArrayList()
				else diffAndUpdateSingleCellAdapterData<ContactsAdapter>(formattedContacts)
			}
		}
	}
}