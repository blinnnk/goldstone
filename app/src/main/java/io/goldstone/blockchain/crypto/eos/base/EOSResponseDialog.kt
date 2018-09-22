package io.goldstone.blockchain.crypto.eos.base

import android.view.ViewGroup
import com.blinnnk.extension.into
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.base.view.CardTitleCell
import io.goldstone.blockchain.common.component.overlay.DashboardOverlay
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.PaddingSize
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.rightPadding


/**
 * @author KaySaith
 * @date  2018/09/22
 */
fun EOSResponse.showDialog(parent: ViewGroup) {
	DashboardOverlay(parent.context) {
		leftPadding = PaddingSize.content
		rightPadding = PaddingSize.content
		listOf(
			Pair("Transaction ID", transactionID),
			Pair("CPU Usage", "$cupUsageByte"),
			Pair("NET Usage", "$netUsageByte")
		).forEach { pair ->
			CardTitleCell(context).apply {
				setTitle(pair.first)
				setContent(pair.second)
			}.click {
				it.context.clickToCopy(pair.second)
			}.into(this)
		}
	}.showTitle(CommonText.succeed).into(parent)
}