package io.goldstone.blinnnk.module.common.tokendetail.tokeninfo.contract

import android.graphics.Bitmap
import io.goldstone.blinnnk.module.common.contract.GoldStonePresenter
import io.goldstone.blinnnk.module.common.contract.GoldStoneView


/**
 * @author KaySaith
 * @date  2018/09/13
 */

interface TokenInfoContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {
		fun setTokenInfo(
			qrCode: Bitmap?,
			title: String,
			subtitle: String,
			icon: Int,
			url: String
		)

		fun showTransactionCount(count: Int)
		fun showBalance(balance: String)
		fun showAddress(address: String, hash160: String)
		fun showTotalValue(received: String, sent: String)
	}

	interface GSPresenter : GoldStonePresenter {

	}
}