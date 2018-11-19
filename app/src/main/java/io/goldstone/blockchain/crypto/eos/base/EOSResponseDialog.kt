package io.goldstone.blockchain.crypto.eos.base

import android.content.Context
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.base.view.CardTitleCell
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.click


/**
 * @author KaySaith
 * @date  2018/09/22
 */
fun EOSResponse.showDialog(parent: ViewGroup) {
	val data = arrayListOf(
		Pair("Transaction ID", transactionID),
		Pair("CPU Usage", "$cupUsageByte"),
		Pair("NET Usage", "$netUsageByte")
	)
	MaterialDialog(parent.context)
		.title(text = CommonText.succeed)
		.customListAdapter(EOSResponseAdapter(data))
		.positiveButton(text = CommonText.confirm)
		.show()
}

class EOSResponseAdapter(
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