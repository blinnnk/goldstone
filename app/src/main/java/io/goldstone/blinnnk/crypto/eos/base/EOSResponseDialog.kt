package io.goldstone.blinnnk.crypto.eos.base

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.util.clickToCopy
import io.goldstone.blinnnk.common.base.view.CardTitleCell
import io.goldstone.blinnnk.common.component.overlay.Dashboard
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.language.TransactionText
import io.goldstone.blinnnk.common.utils.click


/**
 * @author KaySaith
 * @date  2018/09/22
 */
fun EOSResponse.showDialog(context: Context) {
	val data = arrayListOf(
		Pair(TransactionText.transactionHash, transactionID),
		Pair(TransactionText.netUsage, "$cupUsageByte"),
		Pair(TransactionText.cpuUsage, "$netUsageByte")
	)
	Dashboard(context) {
		showList(CommonText.succeed, TitleCellAdapter(data))
	}
}

class TitleCellAdapter(
	override val dataSet: ArrayList<Pair<String, String>>
) : HoneyBaseAdapter<Pair<String, String>, CardTitleCell>() {
	override fun generateCell(context: Context) = CardTitleCell(context)

	override fun CardTitleCell.bindCell(data: Pair<String, String>, position: Int) {
		setTitle(data.first)
		setSubtitle(data.second)
		click {
			it.context.clickToCopy(data.second)
		}
	}

}