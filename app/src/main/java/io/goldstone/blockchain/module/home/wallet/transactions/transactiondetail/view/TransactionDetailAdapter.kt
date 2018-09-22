package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.content.Context
import android.view.View
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel

/**
 * @date 27/03/2018 3:27 AM
 * @author KaySaith
 */
class TransactionDetailAdapter(
	override var dataSet: ArrayList<TransactionDetailModel>,
	private val hold: TransactionInfoCell.() -> Unit
) :
	HoneyBaseAdapterWithHeaderAndFooter<TransactionDetailModel, TransactionDetailHeaderView, TransactionInfoCell, View>() {

	override fun generateCell(context: Context) = TransactionInfoCell(context)

	override fun generateFooter(context: Context) =
		View(context)

	override fun generateHeader(context: Context) = TransactionDetailHeaderView(context)

	override fun TransactionInfoCell.bindCell(
		data: TransactionDetailModel,
		position: Int
	) {
		model = data
		hold(this)
	}
}
