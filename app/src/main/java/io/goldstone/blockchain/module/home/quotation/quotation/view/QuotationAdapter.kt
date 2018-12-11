package io.goldstone.blockchain.module.home.quotation.quotation.view

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 20/04/2018 8:17 PM
 * @author KaySaith
 */
class QuotationAdapter(
	override var dataSet: ArrayList<QuotationModel>,
	private val clickEvent: (QuotationModel) -> Unit
) :
	HoneyBaseAdapterWithHeaderAndFooter<QuotationModel, View, QuotationCell, View>() {

	override fun generateFooter(context: Context) =
		View(context).apply {
			layoutParams = LinearLayout.LayoutParams(matchParent, 60.uiPX())
		}

	override fun generateHeader(context: Context) =
		View(context).apply {
			layoutParams = LinearLayout.LayoutParams(matchParent, 100.uiPX())
		}

	override fun generateCell(context: Context) = QuotationCell(context)

	override fun QuotationCell.bindCell(
		data: QuotationModel,
		position: Int
	) {
		model = data
		onClick {
			clickEvent(data)
			preventDuplicateClicks()
		}
	}
}