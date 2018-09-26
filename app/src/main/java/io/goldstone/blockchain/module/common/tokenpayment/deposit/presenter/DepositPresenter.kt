package io.goldstone.blockchain.module.common.tokenpayment.deposit.presenter

import com.blinnnk.extension.orZero
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.multichain.*
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
				token?.contract.isETC() -> {
					QRCode.generateETHOrETCCode(
						Config.getCurrentETCAddress(),
						amount,
						Config.getETCCurrentChain().id
					)
				}

				token?.contract.isETH() -> {
					QRCode.generateETHOrETCCode(
						Config.getCurrentEthereumAddress(),
						amount,
						Config.getCurrentChain().id
					)
				}

				token?.contract.isBTC() -> {
					val address = if (Config.isTestEnvironment())
						Config.getCurrentBTCSeriesTestAddress()
					else Config.getCurrentBTCAddress()
					QRCode.generateBitcoinCode(address, amount)
				}

				token?.contract.isLTC() -> {
					val address = if (Config.isTestEnvironment())
						Config.getCurrentBTCSeriesTestAddress()
					else Config.getCurrentLTCAddress()
					QRCode.generateLitecoinCode(address, amount)
				}

				token?.contract.isBCH() -> {
					val address = if (Config.isTestEnvironment())
						Config.getCurrentBTCSeriesTestAddress()
					else Config.getCurrentBCHAddress()
					QRCode.generateBitcoinCashCode(address, amount)
				}

				token?.contract.isEOS() -> {
					val accountName = Config.getCurrentEOSAccount().accountName
					QRCode.generateEOSCode(
						accountName,
						EOSCodeName.EOSIO.value, // `EOS` 的 `Token` 要支持传递对应的 `Token Code`
						amount,
						token?.decimal.orZero(),
						Config.getEOSCurrentChain().id
					)
				}

				else -> {
					QRCode.generateERC20Code(
						Config.getCurrentEthereumAddress(),
						token?.contract?.contract.orEmpty(),
						amount,
						token?.decimal.orZero(),
						Config.getCurrentChain().id
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
}