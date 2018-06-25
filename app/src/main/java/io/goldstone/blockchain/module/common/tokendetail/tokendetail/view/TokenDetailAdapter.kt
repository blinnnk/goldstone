package io.goldstone.blockchain.module.common.tokendetail.tokendetail.view

import android.content.Context
import android.widget.LinearLayout
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.matchParent

/**
 * @date 27/03/2018 3:36 PM
 * @author KaySaith
 */
class TokenDetailAdapter(
	override val dataSet: ArrayList<TransactionListModel>,
	private val callback: TokenDetailCell.() -> Unit,
	private val holdHeader: TokenDetailHeaderView.() -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<TransactionListModel, TokenDetailHeaderView, TokenDetailCell, LinearLayout>() {
	
	override fun generateCell(context: Context) = TokenDetailCell(context)
	
	override fun generateFooter(context: Context) = LinearLayout(context).apply {
		// 让出 覆盖在上面的 `Footer` 的高度
		layoutParams = LinearLayout.LayoutParams(matchParent, 80.uiPX())
	}
	
	override fun generateHeader(context: Context) = TokenDetailHeaderView(context).apply(holdHeader)
	
	override fun TokenDetailCell.bindCell(data: TransactionListModel, position: Int) {
		model = data
		callback(this)
	}
}