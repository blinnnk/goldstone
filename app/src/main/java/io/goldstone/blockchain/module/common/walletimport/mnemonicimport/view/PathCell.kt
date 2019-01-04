package io.goldstone.blockchain.module.common.walletimport.mnemonicimport.view

import android.content.Context
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.cell.TopBottomLineCell
import io.goldstone.blockchain.common.component.edittext.TitleEditText
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.rightPadding
import org.jetbrains.anko.sdk27.coroutines.textChangedListener
import org.jetbrains.anko.wrapContent


/**
 * @author KaySaith
 * @date  2018/11/17
 */
class PathCell(context: Context) : TopBottomLineCell(context) {
	var model: PathModel? by observing(null) {
		model?.apply {
			setTitle(chainName, fontSize(14))
			editText.setTitle(pathHeader)
			editText.getEditText().setText(defaultPath)
		}
	}
	private var editText: TitleEditText

	fun updatePath() {
		model?.defaultPath = editText.getEditText().text.toString()
	}

	init {
		val paddingSize = 30.uiPX()
		setHorizontalPadding(paddingSize.toFloat())
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
		editText = TitleEditText(context).apply {
			leftPadding = paddingSize
			rightPadding = paddingSize
			layoutParams = RelativeLayout.LayoutParams(matchParent, wrapContent)
			getEditText().textChangedListener {
				afterTextChanged {
					updatePath()
				}
			}
		}
		addView(editText)
	}
}