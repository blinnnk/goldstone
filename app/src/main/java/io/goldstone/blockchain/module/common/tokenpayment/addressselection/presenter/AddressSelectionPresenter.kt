package io.goldstone.blockchain.module.common.tokenpayment.addressselection.presenter

import com.blinnnk.extension.*
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.language.QRText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.crypto.CryptoName
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.bitcoin.AddressType
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils.isValidMultiChainAddress
import io.goldstone.blockchain.kernel.commonmodel.QRCodeModel
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.view.AddressSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.deposit.presenter.DepositPresenter
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
		if (isValidQRCodeContent(result)) {
			if (result.contains("transfer")) {
				DepositPresenter.convertERC20QRCode(result).let {
					isCorrectCoinOrChainID(it) {
						showPaymentPrepareFragment(it.walletAddress, it.amount)
					}
				}
			} else {
				when {
					token?.symbol.equals(CryptoSymbol.btc(), true) -> {
						DepositPresenter.convertBitcoinQRCode(result).let {
							isCorrectCoinOrChainID(it) {
								showPaymentPrepareFragment(it.walletAddress, it.amount)
							}
						}
					}

					token?.symbol.equals(CryptoSymbol.ltc, true) -> {
						// TODO LTC By Code
					}

					token?.symbol.equals(CryptoSymbol.bch, true) -> {
						// TODO BCH By Code
					}

					token?.symbol.equals(CryptoSymbol.etc, true)
						|| token?.symbol.equals(CryptoSymbol.eth, true) -> {
						DepositPresenter.convertETHOrETCQRCOde(result).let {
							isCorrectCoinOrChainID(it) {
								showPaymentPrepareFragment(it.walletAddress, it.amount)
							}
						}
					}
				}
			}
		} else {
			fragment.context.alert(QRText.invalidQRCodeAlert)
		}
	}

	fun showPaymentPrepareFragment(toAddress: String, count: Double = 0.0) {
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
		when (isValidMultiChainAddress(toAddress, token?.symbol.orEmpty())) {
			null -> {
				fragment.context?.alert(ImportWalletText.addressFromatAlert)
				return
			}

			AddressType.ETHERCOrETC -> {
				if (token?.symbol.equals(CryptoSymbol.btc(), true)) {
					fragment.context.alert(
						"this is not a valid bitcoin address"
					)
					return
				}

				WalletTable.getAllETHAndERCAddresses {
					showAlertIfLocalExistThisAddress(this)
				}
			}

			AddressType.LTC -> {
				if (!token?.symbol.equals(CryptoSymbol.ltc, true)) {
					fragment.context.alert(
						"This is a invalid address type for ${CryptoSymbol.ltc}, Please check it agin"
					)
					return
				} else {
					WalletTable.getAllLTCAddresses {
						showAlertIfLocalExistThisAddress(this)
					}
				}
			}

			AddressType.BCH -> {
				if (!token?.symbol.equals(CryptoSymbol.bch, true)) {
					fragment.context.alert(
						"This is a invalid address type for ${CryptoSymbol.bch}, Please check it agin"
					)
					return
				} else {
					WalletTable.getAllBCHAddresses {
						showAlertIfLocalExistThisAddress(this)
					}
				}
			}

			AddressType.BTC -> {
				if (Config.isTestEnvironment()) {
					fragment.context.alert(
						"this is a mainnet address, please switch your local net " +
							"setting in settings first"
					)
					return
				} else if (!token?.symbol.equals(CryptoSymbol.btc(), true)) {
					fragment.context.alert(
						"This is a invalid address type for ${CryptoSymbol.btc()}, Please check it agin"
					)
					return
				} else {
					WalletTable.getAllBTCMainnetAddresses {
						showAlertIfLocalExistThisAddress(this)
					}
				}
			}

			AddressType.BTCSeriesTest -> {
				if (!Config.isTestEnvironment()) {
					fragment.context.alert(
						"this is a testnet address, please switch your local net " +
							"setting in settings first"
					)
					return
				} else if (!CryptoSymbol.isBTCSeriesSymbol(token?.symbol)) {
					fragment.context.alert(
						"This is a invalid address type for Testnet, Please check it agin"
					)
					return
				} else {
					WalletTable.getAllBTCSeriesTestnetAddresses {
						showAlertIfLocalExistThisAddress(this)
					}
				}
			}
		}
	}

	private fun isValidQRCodeContent(content: String): Boolean {
		return when {
			content.isEmpty() -> false
			!content.contains(":") -> false
			CryptoName.allChainName.none {
				content.contains(it.toLowerCase())
			} -> false
			content.length < CryptoValue.bitcoinAddressLength -> false
			else -> true
		}
	}

	private fun isCorrectCoinOrChainID(qrModel: QRCodeModel, callback: () -> Unit) {
		when {
			token?.symbol.equals(CryptoSymbol.etc, true) -> {
				when {
					!qrModel.contractAddress.equals(CryptoValue.etcContract, true) -> {
						fragment.context.alert(QRText.invalidContract)
						return
					}

					!qrModel.chainID.equals(Config.getETCCurrentChain(), true) -> {
						fragment.context.alert(CommonText.wrongChainID)
						return
					}

					else -> callback()
				}
			}

			token?.symbol.equals(CryptoSymbol.btc(), true) -> {
				when {
					!qrModel.contractAddress.equals(CryptoValue.btcContract, true) -> {
						fragment.context.alert(QRText.invalidContract)
						return
					}

					!qrModel.chainID.equals(Config.getBTCCurrentChain(), true) -> {
						fragment.context.alert(CommonText.wrongChainID)
						return
					}

					else -> callback()
				}
			}

			else -> {
				when {
					!qrModel.contractAddress.equals(token?.contract, true) -> {
						fragment.context.alert(QRText.invalidContract)
						return
					}

					!qrModel.chainID.equals(Config.getCurrentChain(), true) -> {
						fragment.context.alert(CommonText.wrongChainID)
						return
					}

					else -> callback()
				}
			}
		}
	}

	private fun alert(title: String, subtitle: String, callback: () -> Unit) {
		fragment.alert(title, subtitle) {
			yesButton { callback() }
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
		ContactTable.getAllContacts { contacts ->
			contacts.isEmpty() isTrue {
				fragment.asyncData = arrayListOf()
			} otherwise {
				if (fragment.asyncData.isNullOrEmpty()) {
					// 根据当前的 `Coin Type` 来选择展示地址的哪一项
					fragment.asyncData = when {
						token?.symbol.equals(CryptoSymbol.btc(), true) -> contacts.map {
							it.apply {
								defaultAddress =
									if (Config.isTestEnvironment()) it.btcSeriesTestnetAddress
									else it.btcMainnetAddress
							}
						}.toArrayList()
						token?.symbol.equals(CryptoSymbol.ltc, true) -> contacts.map {
							it.apply {
								defaultAddress =
									if (Config.isTestEnvironment()) it.btcSeriesTestnetAddress
									else it.ltcAddress
							}
						}.toArrayList()
						else -> contacts.map {
							it.apply {
								defaultAddress = ethERCAndETCAddress
							}
						}.toArrayList()
					}
				} else {
					diffAndUpdateSingleCellAdapterData<ContactsAdapter>(contacts)
				}
			}
			callback()
		}
	}
}