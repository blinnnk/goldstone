package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.content.Context
import android.content.res.Resources
import android.view.View
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.measureTextWidth
import io.goldstone.blockchain.common.value.*
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
		val textWidth = Resources.getSystem().displayMetrics.density * data.info.measureTextWidth(fontSize(14))
		// 测算文字的内容高度来修改 `Cell` 的高度布局
		Math.round(textWidth / ScreenSize.widthWithPadding.toDouble()).let {
			layoutParams.height += (it * 10.uiPX()).toInt()
		}
		if (model.description == TransactionText.url) {
			setContentColor(Spectrum.darkBlue)
		}
		if (model.description == TransactionText.transactionDate) {
			layoutParams.height = TransactionSize.cellHeight
		}
		hold(this)
	}
}
