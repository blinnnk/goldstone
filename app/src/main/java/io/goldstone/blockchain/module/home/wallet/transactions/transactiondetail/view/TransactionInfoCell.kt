package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.setUnderline
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.view.CardTitleCell
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.rightPadding
import org.jetbrains.anko.wrapContent


/**
 * @author KaySaith
 * @date  2018/09/22
 */
class TransactionInfoCell(context: Context) : CardTitleCell(context) {
	private var addressCells: List<TransactionAddressCell> = listOf()
	var model: TransactionDetailModel by observing(TransactionDetailModel()) {
		if (
			model.description.equals(CommonText.from, true)
			|| model.description.equals(CommonText.to, true)
		) {
			TransactionListModel.convertMultiToOrFromAddresses(model.info).let { addresses ->
				addresses.forEach {
					TransactionAddressCell(context).apply {
						setAddress(it)
						addressCells += this
					}.into(getContainer())
				}
			}
		} else {
			val content =
				when {
						model.info.isEmpty() && model.description.equals(TransactionText.memo, true) -> TransactionText.noMemo
						model.info.isEmpty() -> CommonText.waiting
						model.description.equals(TransactionText.url, true) -> model.info.setUnderline()
						else -> model.info
				}
			setContent(content)
		}
		setTitle(model.description)
	}

	fun showAddContactButton(index: Int, hold: ImageView.() -> Unit) {
		addressCells.isNotEmpty() isTrue {
			addressCells[index].apply {
				getButton().visibility = View.VISIBLE
				hold(this.getButton())
			}
		}
	}

	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, wrapContent)
		leftPadding = PaddingSize.content
		rightPadding = PaddingSize.content
	}
}