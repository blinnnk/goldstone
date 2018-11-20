package io.goldstone.blockchain.module.common.tokenpayment.gasselection.view

import android.content.Context
import android.widget.RadioButton
import android.widget.TextView
import com.blinnnk.extension.alignParentRight
import com.blinnnk.extension.centerInVertical
import com.blinnnk.extension.into
import com.blinnnk.extension.isDefaultStyle
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.GasSelectionModel
import org.jetbrains.anko.radioButton
import org.jetbrains.anko.textColor

/**
 * @date 2018/5/16 11:37 PM
 * @author KaySaith
 */

class GasSelectionCell(context: Context) : BaseCell(context) {

	var model: GasSelectionModel? by observing(null) {
		model?.let {
			info.title.text = it.count
			info.subtitle.text = it.info
			feeTypeDescription.text = it.type.type
		}
	}

	private var radioButton: RadioButton
	private val info by lazy { TwoLineTitles(context) }
	private val feeTypeDescription by lazy { TextView(context) }

	fun setSelectedStatus(isSelected: Boolean) {
		radioButton.isChecked = isSelected
	}

	init {
		info.apply {
			setBlackTitles()
			setSmallStyle()
		}.into(this)

		feeTypeDescription.apply {
			textSize = fontSize(12)
			textColor = GrayScale.gray
			typeface = GoldStoneFont.book(context)
		}.into(this)

		radioButton = radioButton {
			isDefaultStyle(Spectrum.blue)
			isClickable = false
		}.apply {
			alignParentRight()
			centerInVertical()
		}
		feeTypeDescription.apply {
			alignParentRight()
			centerInVertical()
			x -= 35.uiPX()
		}
		info.centerInVertical()
		setHorizontalPadding()
		setGrayStyle()
		hasArrow = false
		layoutParams.height = 60.uiPX()
	}

}