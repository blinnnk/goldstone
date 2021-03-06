package io.goldstone.blinnnk.module.common.tokenpayment.deposit.presenter

import com.blinnnk.extension.orZero
import com.blinnnk.util.getParentFragment
import io.goldstone.blinnnk.common.base.basefragment.BasePresenter
import io.goldstone.blinnnk.common.sharedpreference.SharedAddress
import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.common.sharedpreference.SharedValue
import io.goldstone.blinnnk.crypto.eos.EOSCodeName
import io.goldstone.blinnnk.crypto.multichain.*
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blinnnk.module.common.tokenpayment.deposit.view.DepositFragment
import io.goldstone.blinnnk.module.home.wallet.walletsettings.qrcodefragment.presenter.QRCodePresenter

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
			fragment.setInputViewDescription(token?.symbol?.symbol.orEmpty())
		}
		generateQRCode()
	}

	fun generateQRCode(amount: Double = 0.0, callback: () -> Unit = {}) {
		fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
			val content = when {
				token?.contract.isETC() -> {
					QRCode.generateETHOrETCCode(
						SharedAddress.getCurrentETC(),
						amount,
						SharedChain.getETCCurrent().chainID.id
					)
				}

				token?.contract.isETH() -> {
					QRCode.generateETHOrETCCode(
						SharedAddress.getCurrentEthereum(),
						amount,
						SharedChain.getCurrentETH().chainID.id
					)
				}

				token?.contract.isBTC() -> {
					val address = if (SharedValue.isTestEnvironment())
						SharedAddress.getCurrentBTCSeriesTest()
					else SharedAddress.getCurrentBTC()
					QRCode.generateBitcoinCode(address, amount)
				}

				token?.contract.isLTC() -> {
					val address = if (SharedValue.isTestEnvironment())
						SharedAddress.getCurrentBTCSeriesTest()
					else SharedAddress.getCurrentLTC()
					QRCode.generateLitecoinCode(address, amount)
				}

				token?.contract.isBCH() -> {
					val address = if (SharedValue.isTestEnvironment())
						SharedAddress.getCurrentBTCSeriesTest()
					else SharedAddress.getCurrentBCH()
					QRCode.generateBitcoinCashCode(address, amount)
				}

				token?.contract.isEOSSeries() -> {
					val accountName = SharedAddress.getCurrentEOSAccount().name
					QRCode.generateEOSCode(
						accountName,
						if (token?.contract.isEOSToken()) token?.contract?.contract.orEmpty() else EOSCodeName.EOSIO.value,
						amount,
						token?.decimal.orZero(),
						SharedChain.getEOSCurrent().chainID.id
					)
				}

				else -> {
					QRCode.generateERC20Code(
						SharedAddress.getCurrentEthereum(),
						token?.contract?.contract.orEmpty(),
						amount,
						token?.decimal.orZero(),
						SharedChain.getCurrentETH().chainID.id
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