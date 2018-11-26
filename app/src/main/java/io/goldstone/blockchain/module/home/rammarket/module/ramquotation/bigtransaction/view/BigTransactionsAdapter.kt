package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.view

import android.content.Context
import android.view.View
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel

/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
class BigTransactionsAdapter(
	override val dataSet: ArrayList<TradingInfoModel>,
	private val hold: BigTransactionCell.() -> Unit)
	: HoneyBaseAdapterWithHeaderAndFooter<TradingInfoModel, BigTransactionsHeaderView, BigTransactionCell, View>() {
	
	override fun generateHeader(context: Context) = BigTransactionsHeaderView(context)
	override fun generateCell(context: Context) =  BigTransactionCell(context)
	override fun generateFooter(context: Context) = View(context)
	
	override fun BigTransactionCell.bindCell(
		data: TradingInfoModel,
		position: Int
	) {
		model = data
		hold(this)
	}
}