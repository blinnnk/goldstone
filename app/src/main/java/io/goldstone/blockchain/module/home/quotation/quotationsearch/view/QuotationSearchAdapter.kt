package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import android.view.*
import android.widget.*
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import org.jetbrains.anko.*

/**
 * @date 21/04/2018 4:33 PM
 * @author KaySaith
 */

class QuotationSearchAdapter(
  override val dataSet: ArrayList<QuotationSelectionTable>,
  private val hold: (QuotationSearchCell) -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<QuotationSelectionTable, View, QuotationSearchCell, View>() {
	
	override fun generateFooter(context: Context): View = View(context).apply {
		layoutParams = ViewGroup.LayoutParams(0, 0)
	}
	
	lateinit var headerView: View
	
	override fun generateHeader(context: Context): View =
		RelativeLayout(context).apply {
			layoutParams = LinearLayout.LayoutParams(matchParent, 40.uiPX())
			backgroundColor = GrayScale.lightGray
			visibility = View.GONE
			leftPadding = 10.uiPX()
			
			textView {
				id = ElementID.attentionText
				layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
				textColor = GrayScale.black
				gravity = Gravity.CENTER_VERTICAL
				singleLine = true
			}
			
			imageView {
				layoutParams = RelativeLayout.LayoutParams(40.uiPX(), matchParent)
				setAlignParentRight()
				padding = 15.uiPX()
				imageResource = R.drawable.close_icon
				click {
					(parent as? RelativeLayout)?.apply { visibility = View.GONE }
				}
			}
			headerView = this
		}
		
	
	override fun onBindViewHolder(
		holder: ViewHolder,
		position: Int
	) {
		if (getItemViewType(position) == CellType.Header.value) {
		}else {
			super.onBindViewHolder(holder, position)
		}
	}
	
	
	override fun generateCell(context: Context) = QuotationSearchCell(context)

  override fun QuotationSearchCell.bindCell(data: QuotationSelectionTable, position: Int) {
    searchModel = data
    hold(this)
  }
	
	fun updateHeaderView(text: String) {
		text.isEmpty() isTrue {
			headerView.visibility = View.GONE
		} otherwise {
			headerView.visibility = View.VISIBLE
			headerView.findViewById<TextView>(ElementID.attentionText).text = text
		}
	}

}