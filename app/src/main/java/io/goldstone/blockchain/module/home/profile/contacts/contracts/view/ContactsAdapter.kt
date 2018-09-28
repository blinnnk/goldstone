package io.goldstone.blockchain.module.home.profile.contacts.contracts.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable

/**
 * @date 26/03/2018 1:38 PM
 * @author KaySaith
 */

class ContactsAdapter(
	override val dataSet: ArrayList<ContactTable>,
	private val hold: ContactsCell.() -> Unit
) : HoneyBaseAdapter<ContactTable, ContactsCell>() {

	override fun generateCell(context: Context) = ContactsCell(context)

	override fun ContactsCell.bindCell(data: ContactTable, position: Int) {
		model = data
		hold(this)
	}

}