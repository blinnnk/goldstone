package io.goldstone.blinnnk.module.home.dapp.dappcenter.view.applist

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.language.DappCenterText
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.DataValue
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.fontSize
import io.goldstone.blinnnk.module.home.dapp.dappcenter.model.DAPPTable
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor


/**
 * @author KaySaith
 * @date  2018/12/02
 */
class DAPPAdapter(
	override val dataSet: ArrayList<DAPPTable>,
	private val hold: DAPPTable.() -> Unit,
	private val clickFooterEvent: () -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<DAPPTable, View, DAPPCell, TextView>() {

	override fun generateFooter(context: Context) = TextView(context).apply {
		visibility = if (dataSet.size >= DataValue.dappPageCount) View.VISIBLE else View.GONE
		onClick {
			clickFooterEvent()
			preventDuplicateClicks()
		}
		layoutParams = LinearLayout.LayoutParams(matchParent, 50.uiPX())
		text = DappCenterText.checkAllDapps
		textSize = fontSize(12)
		textColor = GrayScale.midGray
		typeface = GoldStoneFont.medium(context)
		gravity = Gravity.CENTER
	}

	override fun generateHeader(context: Context) = View(context)
	override fun generateCell(context: Context) = DAPPCell(context)

	override fun DAPPCell.bindCell(data: DAPPTable, position: Int) {
		model = data
		onClick {
			hold(data)
			preventDuplicateClicks()
		}
	}

}