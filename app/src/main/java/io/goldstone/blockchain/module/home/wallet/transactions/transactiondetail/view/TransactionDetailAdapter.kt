package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.content.Context
import android.view.View
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.updateHeightByText
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel

/**
 * @date 27/03/2018 3:27 AM
 * @author KaySaith
 */

class TransactionDetailAdapter(
	override var dataSet: ArrayList<TransactionDetailModel>,
	private val hold: TransactionDetailCell.() -> Unit
) :
	HoneyBaseAdapterWithHeaderAndFooter<TransactionDetailModel, TransactionDetailHeaderView, TransactionDetailCell, View>() {

	override fun generateCell(context: Context) =
		TransactionDetailCell(context)

	override fun generateFooter(context: Context) =
		View(context)

	override fun generateHeader(context: Context) =
		TransactionDetailHeaderView(context)

	override fun TransactionDetailCell.bindCell(
		data: TransactionDetailModel,
		position: Int
	) {
		model = data
		updateHeightByText(data.info, fontSize(14), 20.uiPX(), ScreenSize.widthWithPadding)
		if (model.description == TransactionText.url) {
			setContentColor(Spectrum.darkBlue)
		}
		hold(this)
	}
}

