package io.goldstone.blockchain.module.common.tokenpayment.addressselection.presenter

import com.blinnnk.extension.*
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.view.AddressSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.view.PaymentPrepareFragment
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.view.ContactsAdapter
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.web3j.crypto.WalletUtils

/**
 * @date 28/03/2018 9:24 AM
 * @author KaySaith
 */

class AddressSelectionPresenter(
	override val fragment: AddressSelectionFragment
) : BaseRecyclerPresenter<AddressSelectionFragment, ContactTable>() {

	override fun updateData() {
		updateAddressList {
			fragment.updateHeaderViewStatus()
		}
	}

	override fun updateParentContentLayoutHeight(
		dataCount: Int?,
		cellHeight: Int,
		maxHeight: Int
	) {
		// 详情页面直接全屏高度
		setHeightMatchParent()
	}

	fun showPaymentValueDetailFragment(address: String): Runnable {
		return Runnable {
			WalletUtils.isValidAddress(address).isFalse {
				fragment.context?.alert("address isn't valid")
				return@Runnable
			}

			fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
				hideChildFragment(fragment)
				addFragmentAndSetArgument<PaymentPrepareFragment>(ContainerID.content) {
					putString(ArgumentKey.paymentAddress, address)
					putSerializable(ArgumentKey.tokenModel, token)
				}
				overlayView.header.apply {
					backButton.onClick {
						headerTitle = TokenDetailText.address
						presenter.popFragmentFrom<PaymentPrepareFragment>()
						setHeightMatchParent()
						showCloseButton(false)
					}
				}
				headerTitle = TokenDetailText.transferDetail
			}
		}
	}

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		/** 从下一个页面返回后通过显示隐藏监听重设回退按钮的事件 */
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			overlayView.header.showBackButton(true) {
				presenter.setValueHeader(token)
				presenter.popFragmentFrom<AddressSelectionFragment>()
				setHeightMatchParent()
			}
		}
	}

	private fun updateAddressList(callback: () -> Unit) {
		ContactTable.getAllContacts {
			it.isEmpty() isTrue {
				fragment.asyncData = arrayListOf()
			} otherwise {
				if (fragment.asyncData.isNullOrEmpty()) {
					fragment.asyncData = it
				} else {
					diffAndUpdateSingleCellAdapterData<ContactsAdapter>(it)
				}
			}
			callback()
		}
	}

}