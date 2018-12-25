package io.goldstone.blockchain.module.common.tokenpayment.addressselection.presenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.ErrorText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.language.QRText
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.MultiChainUtils
import io.goldstone.blockchain.kernel.commontable.model.QRCodeModel
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.contract.AddressSelectionContract
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.getCurrentAddresses
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @date 28/03/2018 9:24 AM
 * @author KaySaith
 */
class AddressSelectionPresenter(
	private val token: WalletDetailCellModel,
	private val selectionView: AddressSelectionContract.GSView
) : AddressSelectionContract.GSPresenter {

	override fun start() {
		setAddressList()
	}

	override fun showPaymentDetailByQRCode(qrCode: QRCode) {
		if (qrCode.isValid()) {
			if (qrCode.content.contains("transfer")) {
				if (token.contract.isEOSSeries()) qrCode.convertEOSQRCode().let {
					isCorrectCoinOrChainID(it) { showPaymentDetail(it.walletAddress, it.amount) }
				} else qrCode.convertERC20QRCode().let {
					isCorrectCoinOrChainID(it) { showPaymentDetail(it.walletAddress, it.amount) }
				}
			} else when {
				token.contract.isBTCSeries() -> {
					val qrModel = qrCode.convertBitcoinQRCode()
					if (qrModel.isNull()) selectionView.showError(Throwable(QRText.invalidContract))
					else isCorrectCoinOrChainID(qrModel) {
						showPaymentDetail(qrModel.walletAddress, qrModel.amount)
					}
				}

				token.contract.isEOSSeries() -> {
					qrCode.convertEOSQRCode().let {
						isCorrectCoinOrChainID(it) {
							showPaymentDetail(it.walletAddress, it.amount)
						}
					}
				}

				token.contract.isETC() || token.contract.isETH() -> {
					qrCode.convertETHSeriesQRCode().let {
						isCorrectCoinOrChainID(it) {
							showPaymentDetail(it.walletAddress, it.amount)
						}
					}
				}
			}
		} else {
			// 如果不是 `681` 格式的 `QRCode` 那么当作纯地址进行检测
			val addressType =
				MultiChainUtils.isValidMultiChainAddress(qrCode.content, token.symbol)
			if (addressType.isNull() || !addressType.symbol.equals(token.symbol.symbol, true))
				selectionView.showError(Throwable(QRText.invalidQRCodeAlert))
			else showPaymentDetail(qrCode.content, 0.0)
		}
	}

	override fun showPaymentDetail(toAddress: String, count: Double) {
		val addressType =
			MultiChainUtils.isValidMultiChainAddress(toAddress, token.symbol)
		when (addressType) {
			null -> selectionView.showError(Throwable(ImportWalletText.addressFormatAlert))
			AddressType.ETHSeries -> when {
				!token.contract.isETHSeries() ->
					selectionView.showError(Throwable(AccountError.InvalidAddress))
				else -> WalletTable.getAllETHSeriesAddresses {
					selectionView.goToPaymentDetailWithExistedCheckedDialog(this, toAddress, count, token)
				}
			}

			AddressType.EOS,
			AddressType.EOSJungle,
			AddressType.EOSKylin,
			AddressType.EOSAccountName -> when {
				!token.contract.isEOSSeries() ->
					selectionView.showError(Throwable(AccountError.InvalidAddress))
				// 查询数据库对应的当前链下的全部 `EOS Account Name` 用来提示比对
				else -> WalletTable.getAllEOSAccountNames {
					selectionView.goToPaymentDetailWithExistedCheckedDialog(this, toAddress, count, token)
				}
			}

			AddressType.LTC -> when {
				!token.contract.isLTC() ->
					selectionView.showError(Throwable(ErrorText.invalidChainAddress(CoinSymbol.ltc)))
				else -> WalletTable.getAllLTCAddresses {
					selectionView.goToPaymentDetailWithExistedCheckedDialog(this, toAddress, count, token)
				}
			}

			AddressType.BCH -> when {
				!token.contract.isBCH() ->
					selectionView.showError(Throwable(ErrorText.invalidChainAddress(CoinSymbol.bch)))
				else -> WalletTable.getAllBCHAddresses {
					selectionView.goToPaymentDetailWithExistedCheckedDialog(this, toAddress, count, token)
				}
			}

			AddressType.BTC -> when {
				SharedValue.isTestEnvironment() ->
					selectionView.showError(Throwable(QRText.findMainNetAddress))
				!token.contract.isBTC() ->
					selectionView.showError(Throwable(ErrorText.invalidChainAddress(CoinSymbol.btc)))
				else -> WalletTable.getAllBTCMainnetAddresses {
					selectionView.goToPaymentDetailWithExistedCheckedDialog(this, toAddress, count, token)
				}
			}

			AddressType.BTCSeriesTest -> when {
				!SharedValue.isTestEnvironment() ->
					selectionView.showError(Throwable(QRText.findTestNetAddress))
				!token.contract.isBTCSeries() ->
					selectionView.showError(Throwable(QRText.findInvalidTestNetAddress))
				else -> WalletTable.getAllBTCSeriesTestnetAddresses {
					selectionView.goToPaymentDetailWithExistedCheckedDialog(this, toAddress, count, token)
				}
			}
		}
	}

	private fun isCorrectCoinOrChainID(qrModel: QRCodeModel, callback: () -> Unit) {
		when {
			token.contract.isETC() -> when {
				!TokenContract(qrModel.contractAddress, "", null).isETC() ->
					selectionView.showError(Throwable(QRText.invalidContract))
				!qrModel.chainID.equals(SharedChain.getETCCurrent().chainID.id, true) ->
					selectionView.showError(Throwable(CommonText.wrongChainID))
				else -> callback()
			}
			token.contract.isEOSSeries() -> when {
				!TokenContract(qrModel.contractAddress, "", null).isEOSSeries() ->
					selectionView.showError(Throwable(QRText.invalidContract))
				!qrModel.chainID.equals(SharedChain.getEOSCurrent().chainID.id, true) ->
					selectionView.showError(Throwable(CommonText.wrongChainID))
				else -> callback()
			}

			token.contract.isBTC() -> when {
				!TokenContract(qrModel.contractAddress, "", null).isBTC() ->
					selectionView.showError(Throwable(QRText.invalidContract))
				!qrModel.chainID.equals(SharedChain.getBTCCurrent().chainID.id, true) ->
					selectionView.showError(Throwable(CommonText.wrongChainID))
				else -> callback()
			}

			token.contract.isLTC() -> when {
				!TokenContract(qrModel.contractAddress, "", null).isLTC() ->
					selectionView.showError(Throwable(QRText.invalidContract))
				!qrModel.chainID.equals(SharedChain.getLTCCurrent().chainID.id, true) ->
					selectionView.showError(Throwable(CommonText.wrongChainID))
				else -> callback()
			}

			token.contract.isBCH() -> when {
				!TokenContract(qrModel.contractAddress, "", null).isBCH() ->
					selectionView.showError(Throwable(QRText.invalidContract))
				!qrModel.chainID.equals(SharedChain.getBCHCurrent().chainID.id, true) ->
					selectionView.showError(Throwable(CommonText.wrongChainID))
				else -> callback()
			}

			else -> when {
				!qrModel.contractAddress.equals(token.contract.contract, true) ->
					selectionView.showError(Throwable(QRText.invalidContract))
				!qrModel.chainID.equals(SharedChain.getCurrentETH().chainID.id, true) ->
					selectionView.showError(Throwable(CommonText.wrongChainID))
				else -> callback()
			}
		}
	}

	// 根据当前的 `Coin Type` 来选择展示地址的哪一项
	private fun setAddressList() = GlobalScope.launch(Dispatchers.Default) {
		val contacts = ContactTable.dao.getAllContacts()
		withContext(Dispatchers.Main) {
			selectionView.showAddresses(
				contacts.getCurrentAddresses(token.contract).toArrayList()
			)
			selectionView.updateInputStatus()
		}
	}
}