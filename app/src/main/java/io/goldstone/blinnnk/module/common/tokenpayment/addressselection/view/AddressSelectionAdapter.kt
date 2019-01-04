package io.goldstone.blinnnk.module.common.tokenpayment.addressselection.view

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.module.home.profile.contacts.contracts.model.ContactTable
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 28/03/2018 9:25 AM
 * @author KaySaith
 */

class AddressSelectionAdapter(
	override val dataSet: ArrayList<ContactTable>,
	private val cellClickEvent: (ContactTable) -> Unit,
	private val holdHeader: AddressSelectionHeaderView.() -> Unit
) :
	HoneyBaseAdapterWithHeaderAndFooter<ContactTable, AddressSelectionHeaderView, AddressSelectionCell, View>() {

	override fun generateHeader(context: Context) = AddressSelectionHeaderView(context).apply(holdHeader)
	override fun generateCell(context: Context) = AddressSelectionCell(context)
	override fun generateFooter(context: Context) =
		View(context).apply { layoutParams = LinearLayout.LayoutParams(matchParent, 70.uiPX()) }
	override fun AddressSelectionCell.bindCell(data: ContactTable, position: Int) {
		model = data
		onClick {
			cellClickEvent(data)
			preventDuplicateClicks()
		}
	}
}
