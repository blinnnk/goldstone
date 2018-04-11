package io.goldstone.blockchain.module.common.tokenpayment.addressselection.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.*
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.presenter.AddressSelectionPresenter
import io.goldstone.blockchain.module.home.profile.contacts.model.ContactsModel
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 28/03/2018 9:24 AM
 * @author KaySaith
 */

class AddressSelectionFragment : BaseRecyclerFragment<AddressSelectionPresenter, ContactsModel>() {

  val symbol by lazy { arguments?.getString(ArgumentKey.tokenDetail) }

  override val presenter = AddressSelectionPresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<ContactsModel>?) {
    recyclerView.adapter = AddressSelectionAdapter(asyncData.orEmptyArray()) {
      onClick {
        presenter.showPaymentValueDetailFragment(model.address)
        preventDuplicateClicks()
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    asyncData = arrayListOf(
      ContactsModel("https://b-ssl.duitang.com/uploads/item/201410/13/20141013082510_wCKhQ.jpeg", "Calley", "0x89d87...8d7x898"),
      ContactsModel("http://file.popoho.com/2016-08-26/b191e56c62e22c606d3a60fda795b6bb.jpg", "Rita", "0x89d87...8d7x898"),
      ContactsModel("https://b-ssl.duitang.com/uploads/item/201410/13/20141013082510_wCKhQ.jpeg", "Giant", "0x89d87...8d7x898"),
      ContactsModel("https://b-ssl.duitang.com/uploads/item/201409/29/20140929114932_wAuZm.thumb.700_0.jpeg", "Calley", "0x89d87...8d7x898"),
      ContactsModel("http://file.popoho.com/2016-08-26/c3bcc2974cf14d535c41b6016c242769.jpg", "Calley", "0x89d87...8d7x898")
    )

    recyclerView.getItemViewAtAdapterPosition<AddressSelectionHeaderView>(0) {
      setFocusStatus()
      getParentFragment<TokenDetailOverlayFragment> {
        showConfirmButton()
        getInputStatus { hasInput, address ->
          setConfirmStatus(hasInput)
          address?.apply {
            confirmButtonClickEvent = Runnable {
              this@AddressSelectionFragment.presenter.showPaymentValueDetailFragment(address)
            }
          }
        }
      }
    }
  }


  override fun onDestroyView() {
    super.onDestroyView()
    getParentFragment<TokenDetailOverlayFragment> {
      showConfirmButton(false)
      activity?.apply { SoftKeyboard.hide(this) }
    }
  }

  override fun onHiddenChanged(hidden: Boolean) {
    super.onHiddenChanged(hidden)
    // 通过自己的显示状态管理父级头部的 `ConfirmButton` 显示状态
    getParentFragment<TokenDetailOverlayFragment> {
      hidden.isTrue {
        showConfirmButton(false)
      } otherwise {
        showConfirmButton()
        setConfirmStatus(true)
        activity?.apply { SoftKeyboard.hide(this) }
      }
    }

  }

}