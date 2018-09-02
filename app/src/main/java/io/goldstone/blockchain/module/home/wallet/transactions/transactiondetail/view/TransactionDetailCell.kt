package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.setUnderline
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.cell.TopBottomLineCell
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel.Companion.convertMultiToOrFromAddresses
import org.jetbrains.anko.firstChild
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor

/**
 * @date 27/03/2018 3:27 AM
 * @author KaySaith
 */
class TransactionDetailCell(context: Context) : TopBottomLineCell(context) {
	private var addressCells: List<TransactionAddressCell> = listOf()
	var model: TransactionDetailModel by observing(TransactionDetailModel()) {
		if (
			model.description.equals(CommonText.from, true)
			|| model.description.equals(CommonText.to, true)
		) {
			convertMultiToOrFromAddresses(model.info).let { addresses ->
				addresses.forEach {
					TransactionAddressCell(context)
						.apply {
							setAddress(it)
							addressCells += this
						}
						.into(this)
				}
				layoutParams.height = 35.uiPX() * addresses.size + 30.uiPX()
			}
		} else if (model.description.equals(TransactionText.url, true)) {
			info.visibility = View.VISIBLE
			info.textColor = Spectrum.darkBlue
			info.text = model.info.setUnderline()
			layoutParams.height += 20.uiPX()
		} else {
			info.visibility = View.VISIBLE
			info.text =
				if (model.info.isEmpty()) {
					if (model.description.equals(TransactionText.memo, true))
						TransactionText.noMemo
					else CommonText.waiting
				} else model.info
		}
		setTitle(model.description)
	}
	private val info by lazy {
		TextView(context).apply {
			visibility = View.GONE
			textSize = fontSize(14)
			textColor = GrayScale.black
			typeface = GoldStoneFont.medium(context)
			layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, matchParent)
			x = PaddingSize.device.toFloat()
			y -= 3.uiPX()
		}
	}

	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, 65.uiPX())
		setHorizontalPadding(PaddingSize.device.toFloat())
		info.into(this)
	}

	fun showAddContactButton(index: Int, hold: ImageView.() -> Unit) {
		addressCells.isNotEmpty() isTrue {
			addressCells[index].apply {
				getButton().visibility = View.VISIBLE
				hold(this.getButton())
			}
		}
	}
}



















