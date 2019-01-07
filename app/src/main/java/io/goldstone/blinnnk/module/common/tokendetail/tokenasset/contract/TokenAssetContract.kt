package io.goldstone.blinnnk.module.common.tokendetail.tokenasset.contract

import io.goldstone.blinnnk.common.error.GoldStoneError
import io.goldstone.blinnnk.crypto.eos.account.EOSAccount
import io.goldstone.blinnnk.crypto.eos.base.EOSResponse
import io.goldstone.blinnnk.crypto.multichain.TokenContract
import io.goldstone.blinnnk.module.common.contract.GoldStonePresenter
import io.goldstone.blinnnk.module.common.contract.GoldStoneView
import io.goldstone.blinnnk.module.common.tokendetail.eosactivation.accountselection.model.DelegateBandWidthInfo
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/11/11
 */
interface TokenAssetContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {
		fun setTransactionCount(count: Int)
		fun setEOSBalance(balance: String)
		fun setEOSRefunds(description: String)
		fun setEOSDelegateBandWidth(value: String)
		fun showCenterLoading(status: Boolean)
		fun setResourcesValue(
			ramAvailable: BigInteger,
			ramTotal: BigInteger,
			ramEOSCount: String,
			cpuAvailable: BigInteger,
			cpuTotal: BigInteger,
			cpuWeight: String,
			netAvailable: BigInteger,
			netTotal: BigInteger,
			netWeight: String
		)
	}

	interface GSPresenter : GoldStonePresenter {
		fun getDelegateBandWidthData(hold: (ArrayList<DelegateBandWidthInfo>) -> Unit)
		fun updateRefundInfo()
		fun getLatestActivationDate(contract: TokenContract, hold: (String) -> Unit)
		fun redemptionBandwidth(
			password: String,
			receiver: EOSAccount,
			cpuAmount: BigInteger,
			netAmount: BigInteger,
			hold: (response: EOSResponse?, error: GoldStoneError) -> Unit
		)
	}
}