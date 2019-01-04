package io.goldstone.blockchain.module.home.profile.contacts.component

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.customListAdapter
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.EmptyType
import io.goldstone.blockchain.common.component.EmptyView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.getCurrentAddresses
import io.goldstone.blockchain.module.home.profile.contacts.contracts.view.ContactsAdapter


/**
 * @author KaySaith
 * @date  2018/09/24
 */
fun <T : BaseOverlayFragment<*>> T.showContactDashboard(
	chainType: ChainType,
	hold: (address: String) -> Unit
) {
	load {
		val contacts =
			ContactTable.dao.getAllContacts()
		contacts.getCurrentAddresses(ChainType(chainType.id).getContract()).toArrayList()
	} then {
		val emptyView = EmptyView(context!!).apply { setStyle(EmptyType.Contact) }
		val dialog = MaterialDialog(context!!)
		with(dialog) {
			title(text = ProfileText.contacts)
			if (it.isEmpty()) customView(view = emptyView)
			else customListAdapter(ContactsAdapter(it) { contact ->
				hold(contact.defaultAddress)
				dialog.dismiss()
			})
			positiveButton(text = CommonText.gotIt)
			show()
		}
	}
}