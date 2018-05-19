package io.goldstone.blockchain.module.home.quotation.quotation.view

import android.content.Context
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.widget.LinearLayout
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.keyboardHeightListener
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import org.jetbrains.anko.matchParent

/**
 * @date 20/04/2018 8:17 PM
 * @author KaySaith
 */

class QuotationAdapter(
	override var dataSet: ArrayList<QuotationModel>,
	private val hold: QuotationCell.() -> Unit
) :
	HoneyBaseAdapterWithHeaderAndFooter<QuotationModel, LinearLayout, QuotationCell, LinearLayout>() {

	private var hasHiddenSoftNavigationBar = false
	override fun generateFooter(context: Context) =
		LinearLayout(context).apply {
			val barHeight =
				if (!hasHiddenSoftNavigationBar && !KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)) {
					60.uiPX()
				} else 10.uiPX()
			layoutParams = LinearLayout.LayoutParams(matchParent, barHeight)
		}

	override fun generateHeader(context: Context) =
		LinearLayout(context).apply {
			/**
			 * 判断不同手机的不同 `Navigation` 的状态决定 `Footer` 的补贴高度
			 * 主要是, `Samsung S8, S9` 的 `Navigation` 状态判断
			 */
			keyboardHeightListener {
				if (it < 0) {
					hasHiddenSoftNavigationBar = true
				}
			}
			layoutParams = LinearLayout.LayoutParams(matchParent, 100.uiPX())
		}

	override fun generateCell(context: Context) =
		QuotationCell(context)

	override fun QuotationCell.bindCell(
		data: QuotationModel,
		position: Int
	) {
		model = data
		hold(this)
	}
}