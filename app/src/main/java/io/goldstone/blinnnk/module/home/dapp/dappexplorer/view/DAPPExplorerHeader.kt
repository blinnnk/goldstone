package io.goldstone.blinnnk.module.home.dapp.dappexplorer.view

import android.content.Context
import android.view.ViewGroup
import android.widget.*
import com.blinnnk.extension.alignParentRight
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.common.value.*
import io.goldstone.blinnnk.module.home.dapp.dappexplorer.model.DAPPRecentVisitedTable
import org.jetbrains.anko.*

/**
 * @date: 2019-01-18.
 * @author: yangLiHai
 * @description:
 */
class DAPPExplorerHeader(
	context: Context
) : LinearLayout(context) {
	
	private val clearAll = TextView(context).apply {
		textColor = Spectrum.deepBlue
		textSize = fontSize(14)
		typeface = GoldStoneFont.black(context)
		layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
		alignParentRight()
	}
	init {
		layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
		orientation = LinearLayout.VERTICAL
		leftPadding = PaddingSize.content
		rightPadding = PaddingSize.content
		
		relativeLayout {
			textView {
				textColor = GrayScale.midGray
				textSize = fontSize(14)
				typeface = GoldStoneFont.black(context)
				text = "Recent Visited"
			}
			addView(clearAll)
		}
	}
	
	fun notifyData(data: ArrayList<DAPPRecentVisitedTable>, clickEvent: DAPPRecentVisitedTable.() -> Unit) {
		data.forEach { table ->
			addView(RecentVisitedDAPPCell(context).apply {
				model = table
				click {
					clickEvent(table)
				}
			})
		}
	}
	
}