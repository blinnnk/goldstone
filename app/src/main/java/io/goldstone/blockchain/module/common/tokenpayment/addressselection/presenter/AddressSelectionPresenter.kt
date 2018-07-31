package io.goldstone.blockchain.module.common.tokenpayment.addressselection.presenter

import com.blinnnk.extension.*
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.bitcoin.AddressType
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils.isValidMultiChainAddress
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
	
	val token by lazy {
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.token
	}
	
	override fun updateData() {
		updateAddressList {
			fragment.updateHeaderViewStatus()
		}
	}
	
	fun showPaymentPrepareFragmentByQRCode(result: String) {
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
				var transaferCount = 0.0
				// 准备 `Count` 信息, 如果包含有 `amount` 关键字
				if (content.contains(amount)) {
					// 含有 `contract` 和不含有的解析 `amount` 的方式不同
					transaferCount = if (content.contains(token)) {
						content.substringAfter("amount=")
							.substringBefore("?token=").toDoubleOrNull().orZero()
					} else {
						content.substring(50, content.length).toDoubleOrNull().orElse(0.0)
					}
				}
				// 准备 `Contract` 信息, 如果包含有 `token` 关键字就是 `ERC20` 否则是 `ETH`
				val contract = when {
					content.contains(token) -> content.substringAfter("token=")
					else -> CryptoValue.ethContract
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
		toAddress: String,
		count: Double = 0.0
	) {
		// 检查当前转账地址是否为本地任何一个钱包的正在使用的默认地址, 并提示告知用户.
		fun showAlertIfLocalExistThisAddress(localAddresses: List<String>) {
			localAddresses.any { it.equals(toAddress, true) } isTrue {
				alert(
					TokenDetailText.transferToLocalWalletAlertDescription,
					TokenDetailText.transferToLocalWalletAlertTitle
				) {
					goToPaymentPrepareFragment(toAddress, count)
				}
			} otherwise {
				goToPaymentPrepareFragment(toAddress, count)
			}
		}
		// 检查地址是否合规
		when (isValidMultiChainAddress(toAddress)) {
			null -> {
				fragment.context?.alert(ImportWalletText.addressFromatAlert)
				return
			}
			
			AddressType.ETHERCOrETC -> {
				if (token?.symbol.equals(CryptoSymbol.btc, true)) {
					fragment.context.alert(
						"this is not a valid bitcoin address"
					)
					return
				}
				
				WalletTable.getAllETHAndERCAddresses {
					showAlertIfLocalExistThisAddress(this)
				}
			}
			
			AddressType.BTC -> {
				if (Config.isTestEnvironment()) {
					fragment.context.alert(
						"this is a mainnet address, please switch your local net " +
						"setting in settings first"
					)
					return
				}
				WalletTable.getAllBTCMainnetAddresses {
					showAlertIfLocalExistThisAddress(this)
				}
			}
			
			AddressType.BTCTest -> {
				if (!Config.isTestEnvironment()) {
					fragment.context.alert(
						"this is a testnet address, please switch your local net " +
						"setting in settings first"
					)
					return
				}
				WalletTable.getAllBTCTestnetAddresses {
					showAlertIfLocalExistThisAddress(this)
				}
			}
		}
	}
	
	private fun alert(title: String, subtitle: String, callback: () -> Unit) {
		fragment.alert(
			title,
			subtitle
		) {
			yesButton {
				callback()
			}
			noButton { }
		}.show()
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
					setValueHeader(token)
					presenter.popFragmentFrom<AddressSelectionFragment>()
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
					// 根据当前的 `Coin Type` 来选择展示地址的哪一项
					fragment.asyncData = if (token?.symbol.equals(CryptoSymbol.btc, true)) {
						it.map {
							it.apply {
								defaultAddress =
									if (Config.isTestEnvironment()) it.btcTestnetAddress
									else it.btcMainnetAddress
							}
						}.toArrayList()
					} else {
						it.map {
							it.apply {
								defaultAddress = ethERCAndETCAddress
							}
						}.toArrayList()
					}
				} else {
					diffAndUpdateSingleCellAdapterData<ContactsAdapter>(it)
				}
			}
			callback()
		}
	}
}