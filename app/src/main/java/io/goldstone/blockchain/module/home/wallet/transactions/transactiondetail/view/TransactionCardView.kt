package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.extension.forEachOrEnd
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.GSCard
import io.goldstone.blockchain.common.component.SpaceSplitLine
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick


/**
 * @author KaySaith
 * @date  2018/11/07
 */
class TransactionCardView(context: Context) : GSCard(context) {
	private val paddingSize = 5.uiPX()
	var model: List<TransactionDetailModel> by observing(listOf()) {
		removeAllViewsInLayout()
		verticalLayout {
			id = ElementID.cardLayout
			model.forEachOrEnd { item, isEnd ->
				textView {
					setPadding(paddingSize, 0, paddingSize, 0)
					layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
					textColor = Spectrum.deepBlue
					textSize = fontSize(12)
					typeface = GoldStoneFont.black(context)
					text = item.description
				}
				textView {
					setPadding(paddingSize, PaddingSize.content, paddingSize, PaddingSize.content)
					layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
					textColor = GrayScale.black
					textSize = fontSize(14)
					typeface = GoldStoneFont.medium(context)
					text = item.info
					onClick {
						context.clickToCopy(item.info)
					}
				}
				if (!isEnd && model.size > 1) SpaceSplitLine(context).apply {
					layoutParams = ViewGroup.LayoutParams(matchParent, 20.uiPX())
					setStyle(GrayScale.whiteGray, BorderSize.default)
				}.into(this)
			}
		}
	}

	fun addContent(hold: LinearLayout.() -> Unit) {
		findViewById<LinearLayout>(ElementID.cardLayout)?.let(hold)
	}

	init {
		setContentPadding(PaddingSize.overlay, 20.uiPX(), PaddingSize.overlay, PaddingSize.overlay)
	}
}