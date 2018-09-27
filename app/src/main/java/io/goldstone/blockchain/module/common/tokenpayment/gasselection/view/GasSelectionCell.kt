package io.goldstone.blockchain.module.common.tokenpayment.gasselection.view

import android.content.Context
import android.widget.RadioButton
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.GasSelectionModel
import org.jetbrains.anko.radioButton
import org.jetbrains.anko.textColor

/**
 * @date 2018/5/16 11:37 PM
 * @author KaySaith
 */

class GasSelectionCell(context: Context) : BaseCell(context) {

	var model: GasSelectionModel by observing(GasSelectionModel()) {
		info.title.text = model.count
		info.subtitle.text = model.info
		feeTypeDescription.text = model.type
		radioButton.isChecked = model.type == model.currentType
	}

	private var radioButton: RadioButton
	private val info by lazy { TwoLineTitles(context) }
	private val feeTypeDescription by lazy { TextView(context) }

	init {
		info
			.apply {
				setBlackTitles()
				setSmallStyle()
			}
			.into(this)

		feeTypeDescription
			.apply {
				textSize = fontSize(12)
				textColor = GrayScale.gray
				typeface = GoldStoneFont.book(context)
			}
			.into(this)

		radioButton = radioButton {
			isClickable = false
		}.apply {
			setAlignParentRight()
			setCenterInVertical()
		}
		feeTypeDescription.apply {
			setAlignParentRight()
			setCenterInVertical()
			x -= 35.uiPX()
		}
		info.setCenterInVertical()
		setHorizontalPadding()
		setGrayStyle()
		hasArrow = false
		layoutParams.height = 60.uiPX()
	}

}