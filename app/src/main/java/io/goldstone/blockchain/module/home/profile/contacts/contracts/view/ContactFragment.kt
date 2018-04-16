package io.goldstone.blockchain.module.home.profile.contacts.contracts.view

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.presenter.ContactPresenter
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 26/03/2018 1:37 PM
 * @author KaySaith
 */

class ContactFragment : BaseRecyclerFragment<ContactPresenter, ContactTable>() {

  override val presenter = ContactPresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<ContactTable>?) {
    recyclerView.adapter = ContactsAdapter(asyncData.orEmptyArray())
  }

  override fun onResume() {
    super.onResume()
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

  private fun showAddButton(status: Boolean = true) {
    getParentFragment<ProfileOverlayFragment> {
      showAddContactButton(status) {
        onClick { presenter.showContactInputFragment() }
      }
    }
  }

}