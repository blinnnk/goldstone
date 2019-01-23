package io.goldstone.blinnnk.common.sandbox.view.wallet

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.alignParentRight
import com.blinnnk.extension.centerInVertical
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blinnnk.common.base.basecell.BaseCell
import io.goldstone.blinnnk.common.component.title.TwoLineTitles
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.sandbox.WalletBackUpModel
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.CornerSize
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.*

/**
 * @date: 2019-01-07.
 * @author: yangLiHai
 * @description:
 */
class WalletBackUpCell(context: Context) : BaseCell(context) {
	val deleteButton = TextView(context).apply {
		setPadding(10.uiPX(), 5.uiPX(), 10.uiPX(), 5.uiPX())
		text = CommonText.delete
		gravity = Gravity.CENTER
		addCorner(CornerSize.small.toInt(), Spectrum.red)
		textColor = Spectrum.white
	}
	val recoverButton = TextView(context).apply {
		setPadding(10.uiPX(), 5.uiPX(), 10.uiPX(), 5.uiPX())
		text = "RECOVER"
		gravity = Gravity.CENTER
		addCorner(CornerSize.small.toInt(), Spectrum.green)
		textColor = Spectrum.white
	}

	var model: WalletBackUpModel? by observing(null) {
		model?.apply {
			nameAndType.title.text = name
			nameAndType.subtitle.text = getWalletType().getDisplayName()
		}
	}

	private val nameAndType = TwoLineTitles(context).apply {
		layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
		centerInVertical()
		title.apply {
			textSize = fontSize(16)
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

	init {
		setHorizontalPadding()
		layoutParams = ViewGroup.LayoutParams(matchParent, 70.uiPX())
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









