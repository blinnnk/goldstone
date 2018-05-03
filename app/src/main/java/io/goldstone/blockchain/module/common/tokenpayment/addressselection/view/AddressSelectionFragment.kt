package io.goldstone.blockchain.module.common.tokenpayment.addressselection.view

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.presenter.AddressSelectionPresenter
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable

/**
 * @date 28/03/2018 9:24 AM
 * @author KaySaith
 */

class AddressSelectionFragment : BaseRecyclerFragment<AddressSelectionPresenter, ContactTable>() {

	override val presenter = AddressSelectionPresenter(this)

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<ContactTable>?
	) {
		recyclerView.adapter = AddressSelectionAdapter(asyncData.orEmptyArray()) {
			clickEvent = presenter.showPaymentValueDetailFragment(model.address)
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		getParentFragment<TokenDetailOverlayFragment> {
			showConfirmButton(false)
		}
	}

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		// 通过自己的显示状态管理父级头部的 `ConfirmButton` 显示状态
		getParentFragment<TokenDetailOverlayFragment> {
			hidden isTrue {
				showConfirmButton(false)
			} otherwise {
				showConfirmButton()
				setConfirmStatus(true)
			}
		}
	}

	fun updateHeaderViewStatus() {
		recyclerView.getItemViewAtAdapterPosition<AddressSelectionHeaderView>(0) {
			it?.setFocusStatus()
			getParentFragment<TokenDetailOverlayFragment> {
				showConfirmButton()
				it?.getInputStatus { hasInput, address ->
					setConfirmStatus(hasInput)
					address?.apply {
						confirmButtonClickEvent =
							this@AddressSelectionFragment.presenter.showPaymentValueDetailFragment(address)
					}
				}
			}
		}
	}

}