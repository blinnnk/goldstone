package io.goldstone.blockchain.module.common.tokenpayment.deposit.presenter

import com.blinnnk.extension.orZero
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.ChainID
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.kernel.commonmodel.QRCodeModel
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.deposit.view.DepositFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.presenter.QRCodePresenter

/**
 * @date 2018/5/7 11:41 PM
 * @author KaySaith
 */
class DepositPresenter(
	override val fragment: DepositFragment
) : BasePresenter<DepositFragment>() {
	
	var qrContent: String = ""
	
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			fragment.setInputViewDescription(token?.symbol.orEmpty())
		}
		generateQRCode()
	}
	
	fun generateQRCode(amount: Double = 0.0, callback: () -> Unit = {}) {
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			val content = when {
				token?.symbol.equals(CryptoSymbol.etc) -> {
					generateETHOrETCCode(
						Config.getCurrentETCAddress(),
						amount,
						Config.getETCCurrentChain()
					)
				}
				
				token?.symbol.equals(CryptoSymbol.eth) -> {
					generateETHOrETCCode(
						Config.getCurrentEthereumAddress(),
						amount,
						Config.getCurrentChain()
					)
				}
				
				token?.symbol.equals(CryptoSymbol.btc) -> {
					if (Config.isTestEnvironment()) {
						generateBitcoinCode(
							Config.getCurrentBTCTestAddress(),
							amount
						)
					} else {
						generateBitcoinCode(
							Config.getCurrentBTCAddress(),
							amount
						)
					}
				}
				
				else -> {
					generateERC20OCode(
						Config.getCurrentEthereumAddress(),
						token?.contract!!,
						amount,
						token?.decimal?.toInt().orZero(),
						Config.getCurrentChain()
					)
				}
			}
			qrContent = content
			QRCodePresenter.generateQRCode(content).let {
				fragment.setQRImage(it)
				callback()
			}
		}
	}
	
	private fun generateETHOrETCCode(
		walletAddress: String,
		value: Double,
		chainID: String
	): String {
		// 没有设置 `金额`
		return when (value) {
			0.0 ->
				"ethereum:$walletAddress?value=${0.0}&chain_id=$chainID"
		// 有设置 `燃气费` 或 `Memo`
			else -> "ethereum:$walletAddress?value=${value}e18&chain_id=$chainID"
		}
	}
	
	private fun generateBitcoinCode(
		walletAddress: String,
		amount: Double
	): String {
		// 没有设置 `金额`
		return when (amount) {
			0.0 ->
				"bitcoin:$walletAddress"
		// 有设置 `燃气费` 或 `Memo`
			else -> "bitcoin:$walletAddress?amount=$amount"
		}
	}
	
	private fun generateERC20OCode(
		walletAddress: String,
		contractAddress: String,
		value: Double,
		decimal: Int,
		chainID: String
	): String {
		return when ( // 没有设置 `金额`
		value) {
			0.0 ->
				"ethereum:$contractAddress/transfer?address=$walletAddress&unit256=1&chain_id=$chainID"
		// 有设置 `燃气费` 或 `Memo`
			else ->
				"ethereum:$contractAddress/transfer?address=$walletAddress&value=${value}e$decimal&unit256=1&chain_id=$chainID"
		}
	}
	
	companion object {
		fun convertETHOrETCQRCOde(content: String): QRCodeModel {
			val chainID = if (content.contains("chain_id"))
				content.substringAfter("chain_id=").substringBefore("e18")
			else ""
			val contract = if (
				chainID.equals(ChainID.ETCMain.id, true) ||
				chainID.equals(ChainID.ETCTest.id, true)
			) CryptoValue.etcContract else CryptoValue.ethContract
			return if (content.contains("?")) {
				val address = content.substringBefore("?").substringAfter(":")
				val value = content.substringAfter("value=").substringBefore("e18")
				val amount = value.toDoubleOrNull().orZero()
				QRCodeModel(amount, address, contract, chainID)
			} else {
				val address = content.substringAfter(":")
				QRCodeModel(0.0, address, contract, chainID)
			}
		}
		
		fun convertERC20QRCode(content: String): QRCodeModel {
			val chainIDContent =
				if (content.contains("chain_id="))
					content.substringAfter("chain_id=")
				else ""
			val chainID =
				when {
					chainIDContent.isEmpty() -> ChainID.Main.id
					chainIDContent.contains("&") -> chainIDContent.substringBefore("&")
					else -> chainIDContent
				}
			val walletAddress = content.substringAfter("address=").substringBefore("&")
			val contract = content.substringBefore("/transfe").substringAfter(":")
			return if (content.contains("value")) {
				val value = content.substringAfter("value=").substringBefore("e")
				val decimalContent = content.substringAfter("value=").substringAfter("e")
				val decimal =
					if (decimalContent.contains("&")) {
						decimalContent.substringBefore("&")
					} else {
						decimalContent
					}
				// 如果未来需要解析精度会用到 `Decimal` 这里暂时保留方法 By KaySaith
				LogUtil.debug("convertERC20QRCode", decimal)
				QRCodeModel(value.toDoubleOrNull().orZero(), walletAddress, contract, chainID)
			} else {
				QRCodeModel(0.0, walletAddress, contract, chainID)
			}
		}
		
		fun convertBitcoinQRCode(content: String): QRCodeModel {
			return if (content.contains("?")) {
				val address = content.substringBefore("?").substringAfter(":")
				val amount = content.substringAfter("amount=").toDoubleOrNull().orZero()
				val chainID =
					if (BTCUtils.isValidTestnetAddress(address)) ChainID.BTCTest.id
					else ChainID.BTCMain.id
				QRCodeModel(amount, address, CryptoValue.btcContract, chainID)
			} else {
				val address = content.substringAfter(":")
				val chainID =
					if (BTCUtils.isValidTestnetAddress(address)) ChainID.BTCTest.id
					else ChainID.BTCMain.id
				QRCodeModel(0.0, address, CryptoValue.btcContract, chainID)
			}
		}
	}
}