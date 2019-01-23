package io.goldstone.blinnnk.module.home.dapp.dappexplorer.view

import android.content.Context
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.*
import org.jetbrains.anko.*

/**
 * @date: 2019-01-23.
 * @author: yangLiHai
 * @description:
 */
class RencentVisitedDAPPCell(context: Context) : RelativeLayout(context) {
	
	private val icon = imageView {
		layoutParams = LayoutParams(30.uiPX(), 30.uiPX())
		centerInVertical()
		addCorner(CornerSize.small.toInt(), Spectrum.white)
	}
	
	private val name = TextView(context).apply {
		textSize = fontSize(14)
		textColor = GrayScale.black
		typeface = GoldStoneFont.heavy(context)
	}
	
	private val url = TextView(context).apply {
		textSize = fontSize(12)
		textColor = GrayScale.midGray
		typeface = GoldStoneFont.black(context)
	}
	
	init {
		
		layoutParams = ViewGroup.LayoutParams(70.uiPX(), matchParent)
		leftPadding = PaddingSize.content
		rightPadding = PaddingSize.content
		
		verticalLayout {
			layoutParams = LayoutParams(matchParent, wrapContent)
			centerInVertical()
			setMargins<RelativeLayout.LayoutParams> {
				leftMargin = 70.uiPX()
			}
			addView(name)
			addView(url)
		}
	}
	
}








