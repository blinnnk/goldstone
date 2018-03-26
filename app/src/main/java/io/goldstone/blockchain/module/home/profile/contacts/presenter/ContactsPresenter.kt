package io.goldstone.blockchain.module.home.profile.contacts.presenter

import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.home.profile.contacts.model.ContactsModel
import io.goldstone.blockchain.module.home.profile.contacts.view.ContactsFragment

/**
 * @date 26/03/2018 1:36 PM
 * @author KaySaith
 */

class ContactsPresenter(
  override val fragment: ContactsFragment
  ) : BaseRecyclerPresenter<ContactsFragment, ContactsModel>() {


}