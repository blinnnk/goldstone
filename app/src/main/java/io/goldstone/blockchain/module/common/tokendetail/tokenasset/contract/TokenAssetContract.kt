package io.goldstone.blockchain.module.common.tokendetail.tokenasset.contract

import android.graphics.Bitmap
import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
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
		fun showError(error: Throwable)
	}

	interface GSPresenter : GoldStonePresenter {
	}
}