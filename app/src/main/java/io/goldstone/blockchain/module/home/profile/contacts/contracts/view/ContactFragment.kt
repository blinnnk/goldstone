package io.goldstone.blockchain.module.home.profile.contacts.contracts.view

import android.os.Bundle
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.component.overlay.ContentScrollOverlayView
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.presenter.ContactPresenter
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton

/**
 * @date 26/03/2018 1:37 PM
 * @author KaySaith
 * @Description
 * 这个模块同时也是 `ContactDashboard` 的内嵌公用模块, 如果是 `ContactDashboard` 会传递
 * `ChainType` 参数到这个界面. 所以用 `ChainType` 作为来源的判断.
 */
class ContactFragment : BaseRecyclerFragment<ContactPresenter, ContactTable>() {

	var chainType: Int? = null
	var selectedAddress: String? = null
	var clickCellEvent: Runnable? = null

	override val pageTitle: String = ProfileText.contacts
	override val presenter = ContactPresenter(this)

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<ContactTable>?
	) {
		recyclerView.adapter = ContactsAdapter(asyncData.orEmptyArray()) {
			onClick {
				if (chainType != null) {
					selectedAddress = this@ContactsAdapter.model.defaultAddress
					if (selectedAddress?.isEmpty() == true) context.alert(AccountError.InvalidAddress.message)
					clickCellEvent?.run()
				}
				preventDuplicateClicks()
			}
		}

		// 没有这两个参数意味着就是在 `ContactFragment` 本页, 不然可能会在 `BaseTradingFragment` 做内联的公用模块
		if (chainType.isNull() && clickCellEvent.isNull()) {
			recyclerView.addSwipeEvent<ContactsCell>(R.drawable.delete_icon, 20.uiPX(), ItemTouchHelper.LEFT) { position, cell ->
				alert {
					isCancelable = false
					title = ProfileText.deletContactAlertTitle
					message = ProfileText.deleteContactAlertDescription
					yesButton { cell?.apply { presenter.deleteContact(model.id) } }
					cancelButton {
						recyclerView.adapter?.notifyItemChanged(position)
						it.dismiss()
					}
				}.show()
			}

			recyclerView.addSwipeEvent<ContactsCell>(R.drawable.edit_contact_icon, 20.uiPX(), ItemTouchHelper.RIGHT) { position, cell ->
				alert {
					isCancelable = false
					title = ProfileText.deletContactAlertTitle
					message = ProfileText.deleteContactAlertDescription
					yesButton {
						cell?.apply { presenter.shoEditContactFragment(model.id) }
						recyclerView.adapter?.notifyItemChanged(position)
					}
					cancelButton {
						recyclerView.adapter?.notifyItemChanged(position)
						it.dismiss()
					}
				}.show()
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		showAddButton()
	}

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		if (isHidden) {
			showAddButton(false)
		} else {
			showAddButton()
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		val parent = parentFragment
		if (parent is TokenDetailOverlayFragment) {
			val dashboard =
				parent.getContainer().findViewById<ContentScrollOverlayView>(ElementID.contentScrollview)
			// 判断是否打开通讯录来更改回退栈
			if (!dashboard.isNull()) {
				parent.getContainer().removeView(dashboard)
				parent.removeChildFragment(this)
			} else super.setBackEvent(mainActivity)
		} else super.setBackEvent(mainActivity)

	}

	private fun showAddButton(status: Boolean = true) {
		getParentFragment<ProfileOverlayFragment> {
			showAddButton(status) {
				presenter.showContactInputFragment()
			}
		}
	}
}