package io.goldstone.blockchain.module.home.quotation.quotation.view

import android.content.Context
import android.content.res.Resources
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.widget.LinearLayout
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
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

	override fun generateFooter(context: Context) =
		LinearLayout(context).apply {
			val  hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
			val footerHeight = if(hasBackKey) 50.uiPX() else 10.uiPX()
			layoutParams = LinearLayout.LayoutParams(matchParent, footerHeight)

		}

	override fun generateHeader(context: Context) =
		LinearLayout(context).apply {
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

	fun hasNavBar(): Boolean {
		val id = Resources.getSystem().getIdentifier("config_showNavigationBar", "bool", "android")
		return id > 0 && Resources.getSystem().getBoolean(id)
	}

}