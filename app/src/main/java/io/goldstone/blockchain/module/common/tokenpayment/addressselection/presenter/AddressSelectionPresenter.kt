package io.goldstone.blockchain.module.common.tokenpayment.addressselection.presenter

import com.blinnnk.extension.hideChildFragment
import com.blinnnk.extension.isFalse
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.view.AddressSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view.PaymentValueDetailFragment
import io.goldstone.blockchain.module.home.profile.contacts.model.ContactsModel
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.web3j.crypto.WalletUtils

/**
 * @date 28/03/2018 9:24 AM
 * @author KaySaith
 */

class AddressSelectionPresenter(
  override val fragment: AddressSelectionFragment
) : BaseRecyclerPresenter<AddressSelectionFragment, ContactsModel>() {

  fun showPaymentValueDetailFragment(address: String) {

    WalletUtils.isValidAddress(address).isFalse {
      fragment.context?.alert("address isn't valid")
      return
    }

    fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
      hideChildFragment(fragment)
      addFragmentAndSetArgument<PaymentValueDetailFragment>(ContainerID.content) {
        putString(ArgumentKey.paymentAddress, address)
        putString(ArgumentKey.paymentSymbol, symbol)
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
        presenter.setValueHeader(symbol)
        presenter.popFragmentFrom<AddressSelectionFragment>()
        setHeightMatchParent()
      }
    }
  }

}