package io.goldstone.blockchain.module.home.profile.contacts.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.profile.contacts.model.ContactsModel
import io.goldstone.blockchain.module.home.profile.contacts.presenter.ContactsPresenter

/**
 * @date 26/03/2018 1:37 PM
 * @author KaySaith
 */

class ContactsFragment : BaseRecyclerFragment<ContactsPresenter, ContactsModel>() {

  override val presenter = ContactsPresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<ContactsModel>?) {
    recyclerView.adapter = ContactsAdapter(asyncData.orEmptyArray())
  }

  override fun setSlideUpWithCellHeight() = 75.uiPX()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    asyncData = arrayListOf(
      ContactsModel("https://b-ssl.duitang.com/uploads/item/201410/13/20141013082510_wCKhQ.jpeg", "Calley", "0x89d87...8d7x898"),
      ContactsModel("https://b-ssl.duitang.com/uploads/item/201409/26/20140926152521_RRaKG.thumb.700_0.jpeg", "Rita", "0x89d87...8d7x898"),
      ContactsModel("https://b-ssl.duitang.com/uploads/item/201410/13/20141013082510_wCKhQ.jpeg", "Giant", "0x89d87...8d7x898"),
      ContactsModel("https://b-ssl.duitang.com/uploads/item/201409/29/20140929114932_wAuZm.thumb.700_0.jpeg", "Calley", "0x89d87...8d7x898")
    )

  }

}