package io.goldstone.blockchain.module.home.profile.contacts.contracts.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.getCurrentAddresses
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
		ContactTable.deleteContactByID(id) {
			updateAddressList()
		}
	}

	private fun updateAddressList() {
		ContactTable.getAllContacts {
			val formattedContacts =
				it.getCurrentAddresses(ChainType(fragment.chainType.orZero()).getContract()).toArrayList()
			it.isEmpty() isTrue {
				if (fragment.asyncData.isNull()) fragment.asyncData = formattedContacts
				else diffAndUpdateSingleCellAdapterData<ContactsAdapter>(formattedContacts)
			} otherwise {
				if (fragment.asyncData.isNull()) fragment.asyncData = it
				else diffAndUpdateSingleCellAdapterData<ContactsAdapter>(formattedContacts)
			}
		}
	}
}