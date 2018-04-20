package io.goldstone.blockchain.module.home.quotation.quotation.view

import android.content.Context
import android.widget.LinearLayout
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import org.jetbrains.anko.matchParent

/**
 * @date 20/04/2018 8:17 PM
 * @author KaySaith
 */

class QuotationAdapter(
  override val dataSet: ArrayList<QuotationModel>
  ) : HoneyBaseAdapterWithHeaderAndFooter<QuotationModel, LinearLayout, QuotationCell, LinearLayout>() {

  override fun generateFooter(context: Context) = LinearLayout(context).apply {
    layoutParams = LinearLayout.LayoutParams(matchParent, 10.uiPX())
  }

  override fun generateHeader(context: Context) = LinearLayout(context).apply {
    layoutParams = LinearLayout.LayoutParams(matchParent, 100.uiPX())
  }

  override fun generateCell(context: Context) = QuotationCell(context)

  override fun QuotationCell.bindCell(data: QuotationModel, position: Int) {
    model = data
  }

}