package io.goldstone.blockchain.module.home.quotation.quotationmanagement.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.QuotationSize
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor

/**
 * @date 21/04/2018 3:58 PM
 * @author KaySaith
 */

class QuotationManagementAdapter(
  override val dataSet: ArrayList<DefaultTokenTable>
  ) : HoneyBaseAdapterWithHeaderAndFooter<DefaultTokenTable, LinearLayout, QuotationManagementCell, LinearLayout>() {

  override fun generateFooter(context: Context) = LinearLayout(context)


  override fun generateHeader(context: Context) = LinearLayout(context).apply {
    layoutParams = LinearLayout.LayoutParams(matchParent, QuotationSize.attentionHeight)
    addAttentionText()
  }

  override fun generateCell(context: Context) = QuotationManagementCell(context)

  override fun QuotationManagementCell.bindCell(data: DefaultTokenTable, position: Int) {
    model = data
  }

  @SuppressLint("SetTextI18n")
  private fun ViewGroup.addAttentionText() {
    TextView(context).apply {
      layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 40.uiPX())
      x += PaddingSize.device
      y += 15.uiPX()
      textColor = GrayScale.midGray
      gravity = Gravity.CENTER
      text = "Long press and drag the item to reordering your custom token management"
      typeface = GoldStoneFont.book(context)
      textSize = 4.uiPX().toFloat()
    }.into(this)
  }

}