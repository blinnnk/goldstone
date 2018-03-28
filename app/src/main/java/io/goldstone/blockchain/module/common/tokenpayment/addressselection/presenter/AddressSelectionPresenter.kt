package io.goldstone.blockchain.module.common.tokenpayment.addressselection.presenter

import com.blinnnk.extension.hideChildFragment
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.view.AddressSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view.PaymentValueDetailFragment
import io.goldstone.blockchain.module.home.profile.contacts.model.ContactsModel
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 28/03/2018 9:24 AM
 * @author KaySaith
 */

class AddressSelectionPresenter(
  override val fragment: AddressSelectionFragment
) : BaseRecyclerPresenter<AddressSelectionFragment, ContactsModel>() {

  fun showPaymentValueDetailFragment() {
    fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
      hideChildFragment(fragment)
      addFragmentAndSetArgument<PaymentValueDetailFragment>(ContainerID.content) {
        // Send Arguments
      }
      overlayView.header.apply {
        backButton.onClick {
          headerTitle = TokenDetailText.address
          presenter.popFragmentFrom<PaymentValueDetailFragment>()
          setHeightMatchParent()
          showCloseButton(false)
        }
      }
      headerTitle = TokenDetailText.transferDetail
    }
  }

  override fun onFragmentShowFromHidden() {
    super.onFragmentShowFromHidden()
    /** 从下一个页面返回后通过显示隐藏监听重设回退按钮的事件 */
    fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
      overlayView.header.showBackButton(true) {
        presenter.setValueHeader()
        presenter.popFragmentFrom<AddressSelectionFragment>()
        setHeightMatchParent()
      }
    }
  }

}