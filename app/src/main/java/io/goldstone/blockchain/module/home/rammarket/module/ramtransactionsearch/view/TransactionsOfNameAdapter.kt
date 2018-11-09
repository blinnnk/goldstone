package io.goldstone.blockchain.module.home.rammarket.module.ramtransactionsearch.view

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.baserecyclerfragment.BottomLoadingView
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import org.jetbrains.anko.matchParent

/**
 * @date: 2018/11/8.
 * @author: yanglihai
 * @description:
 */
class TransactionsOfNameAdapter(
	override val dataSet: ArrayList<TradingInfoModel>,
	private val hold: TransactionOfNameCell.() -> Unit)
	: HoneyBaseAdapterWithHeaderAndFooter<TradingInfoModel, TransactionOfNameHeaderView, TransactionOfNameCell, View>() {
	
	override fun generateHeader(context: Context) = TransactionOfNameHeaderView(context)
	override fun generateCell(context: Context) =  TransactionOfNameCell(context)
	override fun generateFooter(context: Context) = BottomLoadingView(context).apply {
		// 让出 覆盖在上面的 `Footer` 的高度
		setGrayDescription()
		addView(View(context).apply { layoutParams = LinearLayout.LayoutParams(matchParent, 50.uiPX()) })
	}
	
	override fun TransactionOfNameCell.bindCell(
		data: TradingInfoModel,
		position: Int
	) {
		model = data
		hold(this)
	}
}