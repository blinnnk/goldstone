package io.goldstone.blinnnk.common.sandbox.view.wallet

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blinnnk.common.base.basecell.BaseCell
import io.goldstone.blinnnk.common.component.title.TwoLineTitles
import io.goldstone.blinnnk.common.sandbox.WalletModel
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.*
import org.jetbrains.anko.*

/**
 * @date: 2019-01-07.
 * @author: yangLiHai
 * @description:
 */
class WalletRecoverCell(context: Context) : BaseCell(context) {
	private val nameAndType = TwoLineTitles(context).apply {
		layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
		centerInVertical()
		title.apply {
			textSize = fontSize(17)
			textColor = Spectrum.blue
			typeface = GoldStoneFont.black(context)
		}
		
		subtitle.apply {
			textSize = fontSize(12)
			textColor = GrayScale.lightGray
			typeface = GoldStoneFont.book(context)
			topPadding = 10.uiPX()
		}
	}
	
	val deleteButton = TextView(context).apply {
		leftPadding = 10.uiPX()
		rightPadding = 10.uiPX()
		topPadding = 5.uiPX()
		bottomPadding = 5.uiPX()
		text = "delete"
		gravity = Gravity.CENTER
		addCorner(5.uiPX(), Spectrum.red)
		textColor = Spectrum.white
	}
	val recoverButton = TextView(context).apply {
		leftPadding = 10.uiPX()
		rightPadding = 10.uiPX()
		topPadding = 5.uiPX()
		bottomPadding = 5.uiPX()
		text = "recover"
		gravity = Gravity.CENTER
		addCorner(5.uiPX(), Spectrum.green)
		textColor = Spectrum.white
	}
	
	var model: WalletModel? by observing(null) {
		model?.apply {
			nameAndType.title.text = name
			nameAndType.subtitle.text = getWalletType().getDisplayName()
		}
	}
	
	init {
		layoutParams = ViewGroup.MarginLayoutParams(matchParent, 100.uiPX())
		setMargins<MarginLayoutParams> {
			leftMargin = 10.uiPX()
			rightMargin = 10.uiPX()
		}
		setGrayStyle()
		hasArrow = false
		addView(nameAndType)
		linearLayout {
			layoutParams = LayoutParams(wrapContent, wrapContent)
			centerInVertical()
			addView(deleteButton)
			addView(recoverButton)
		}.apply {
			alignParentRight()
			recoverButton.setMargins<LinearLayout.LayoutParams> { leftMargin = 10.uiPX() }
		}
		
		
		
	}
	
	
}









