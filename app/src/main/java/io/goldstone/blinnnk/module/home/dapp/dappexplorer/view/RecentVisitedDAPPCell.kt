package io.goldstone.blinnnk.module.home.dapp.dappexplorer.view

import android.content.Context
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.utils.glideImage
import io.goldstone.blinnnk.common.value.*
import io.goldstone.blinnnk.module.home.dapp.dappexplorer.model.DAPPRecentVisitedTable
import org.jetbrains.anko.*

/**
 * @date: 2019-01-23.
 * @author: yangLiHai
 * @description:
 */
class RecentVisitedDAPPCell(context: Context) : RelativeLayout(context) {
	
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
	
	var model: DAPPRecentVisitedTable? by observing(null) {
		if (model.isNotNull()) {
			icon.glideImage(model!!.ico)
			name.text = model!!.name
			url.text = model!!.url
		} else {
			icon.glideImage("")
			name.text = ""
			url.text = ""
		}
	}
	
	init {
		
		layoutParams = ViewGroup.LayoutParams(matchParent, 70.uiPX())
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



















