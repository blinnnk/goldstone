package io.goldstone.blinnnk.module.home.profile.contacts.contracts.view

import android.os.Bundle
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blinnnk.common.component.overlay.Dashboard
import io.goldstone.blinnnk.common.language.ProfileText
import io.goldstone.blinnnk.module.home.profile.contacts.contracts.event.ContactUpdateEvent
import io.goldstone.blinnnk.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blinnnk.module.home.profile.contacts.contracts.presenter.ContactPresenter
import io.goldstone.blinnnk.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @date 26/03/2018 1:37 PM
 * @author KaySaith
 * @Description
 * 这个模块同时也是 `ContactDashboard` 的内嵌公用模块, 如果是 `ContactDashboard` 会传递
 * `ChainType` 参数到这个界面. 所以用 `ChainType` 作为来源的判断.
 */
class ContactFragment : BaseRecyclerFragment<ContactPresenter, ContactTable>() {

	var chainType: Int? = null

	override val pageTitle: String = ProfileText.contacts
	override val presenter = ContactPresenter(this)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		EventBus.getDefault().register(this)
	}

	override fun onDestroy() {
		super.onDestroy()
		EventBus.getDefault().unregister(this)
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	fun updateContactListEvent(updateEvent: ContactUpdateEvent) {
		if (updateEvent.hasChanged) presenter.updateData()
	}

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<ContactTable>?
	) {
		recyclerView.adapter = ContactsAdapter(asyncData.orEmptyArray()) {
			presenter.shoEditContactFragment(it.id)
		}

		recyclerView.addSwipeEvent<ContactsCell>(R.drawable.delete_icon, 20.uiPX(), ItemTouchHelper.LEFT) { position, cell ->
			Dashboard(context!!) {
				cancelOnTouchOutside()
				showAlert(
					ProfileText.deleteContactAlertTitle,
					ProfileText.deleteContactAlertDescription,
					cancelAction = {
						recyclerView.adapter?.notifyItemChanged(position)
					},
					confirmAction = {
						cell?.apply { presenter.deleteContact(model.id) }
					}
				)
			}
		}

		recyclerView.addSwipeEvent<ContactsCell>(R.drawable.edit_contact_icon, 20.uiPX(), ItemTouchHelper.RIGHT) { position, cell ->
			Dashboard(context!!) {
				showAlert(
					ProfileText.editContactAlertTitle,
					ProfileText.editContactAlertDescription,
					cancelAction = {
						recyclerView.adapter?.notifyItemChanged(position)
					},
					confirmAction = {
						cell?.apply { presenter.shoEditContactFragment(model.id) }
						recyclerView.adapter?.notifyItemChanged(position)
					}
				)
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		showAddButton(true)
	}

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		showAddButton(!isHidden)
	}

	private fun showAddButton(status: Boolean) {
		getParentFragment<ProfileOverlayFragment> {
			showCloseButton(status) {
				presenter.removeSelfFromActivity()
			}
			showAddButton(status) {
				presenter.showContactInputFragment()
			}
		}
	}
}