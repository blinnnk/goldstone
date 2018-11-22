package io.goldstone.blockchain.module.common.tokendetail.tokenasset.contract

import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.DelegateBandWidthInfo
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
		fun redemptionBandwidth(
			password: String,
			receiver: EOSAccount,
			cpuAmount: BigInteger,
			netAmount: BigInteger,
			hold: (response: EOSResponse) -> Unit
		)
	}
}