package io.goldstone.blockchain.module.common.tokenpayment.addressselection.presenter

import com.blinnnk.extension.*
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.Address
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.isValid
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.view.AddressSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.view.PaymentPrepareFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.view.ContactsAdapter
import org.jetbrains.anko.noButton
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton

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
	
	fun showPaymentPrepareFragmentByQRCode(result: String) {
		val minERC20ResultLength = 100
		val content = result.orEmpty()
		// 不符合标准的长度直接返回
		if (content.length < CryptoValue.bip39AddressLength || content.substring(0, 2) != "0x") {
			fragment.context?.alert(QRText.unvalidQRCodeAlert)
			return
		}
		// 单纯的地址二维码
		if (content.length == CryptoValue.bip39AddressLength) {
			showPaymentPrepareFragment(content)
			return
		}
		// 校验信息
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.token?.let {
			if (content.length > CryptoValue.bip39AddressLength) {
				val amount = "amount"
				val token = "token"
				val ethContract = "0x0"
				var transaferCount = 0.0
				// 准备 `Count` 信息, 如果包含有 `amount` 关键字
				if (content.contains(amount)) {
					// 含有 `contract` 和不含有的解析 `amount` 的方式不同
					transaferCount = if (content.contains(token)) {
						content.substring(50, content.length - 49).toDoubleOrNull().orElse(0.0)
					} else {
						content.substring(50, content.length).toDoubleOrNull().orElse(0.0)
					}
				}
				// 准备 `Contract` 信息, 如果包含有 `token` 关键字就是 `ERC20` 否则是 `ETH`
				val contract = if (content.contains(token) && content.length >= minERC20ResultLength) {
					content.substring(content.length - CryptoValue.bip39AddressLength, content.length)
				} else {
					ethContract
				}
				
				if (contract.isNotEmpty() && it.contract != contract) {
					fragment.context?.alert(QRText.unvalidContract)
					return
				}
				
				showPaymentPrepareFragment(
					content.substring(0, CryptoValue.bip39AddressLength), transaferCount
				)
			}
		}
	}
	
	fun showPaymentPrepareFragment(
		address: String,
		count: Double = 0.0
	) {
		Address(address).isValid() isFalse {
			fragment.context?.alert(ImportWalletText.addressFromatAlert)
			return
		}
		WalletTable.getAllAddresses {
			any { it == address } isTrue {
				fragment.alert(
					TokenDetailText.transferToLocalWalletAlertDescription,
					TokenDetailText.transferToLocalWalletAlertTitle
				) {
					yesButton {
						goToPaymentPrepareFragment(address, count)
					}
					noButton { }
				}.show()
			} otherwise {
				goToPaymentPrepareFragment(address, count)
			}
		}
	}
	
	private fun goToPaymentPrepareFragment(
		address: String,
		count: Double = 0.0
	) {
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			hideChildFragment(fragment)
			addFragmentAndSetArgument<PaymentPrepareFragment>(ContainerID.content) {
				putString(ArgumentKey.paymentAddress, address)
				putDouble(ArgumentKey.paymentCount, count)
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
	
	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		/** 从下一个页面返回后通过显示隐藏监听重设回退按钮的事件 */
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			if (!isFromQuickTransfer) {
				overlayView.header.showBackButton(true) {
					presenter.setValueHeader(token)
					presenter.popFragmentFrom<AddressSelectionFragment>()
					setHeightMatchParent()
				}
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