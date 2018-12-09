package io.goldstone.blockchain.module.home.dapp.common

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.extension.getTargetObject
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.component.cell.graySquareCell
import io.goldstone.blockchain.common.component.overlay.Dashboard
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.crypto.eos.base.showDialog
import io.goldstone.blockchain.crypto.eos.transaction.EOSTransactionInfo
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.wrapContent
import org.json.JSONObject


/**
 * @author KaySaith
 * @date  2018/12/09
 */
fun ViewGroup.showQuickPaymentDashboard(data: JSONObject, callback: (txID: String) -> Unit) {
	val info = data.getTargetObject("data")
	val transaction = EOSTransactionInfo(data)
	val contentLayout = LinearLayout(context).apply {
		orientation = LinearLayout.VERTICAL
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
		padding = PaddingSize.content
		graySquareCell {
			layoutParams.width = matchParent
			setTitle(CommonText.from)
			setSubtitle(info.safeGet("from"))
		}
		graySquareCell {
			layoutParams.width = matchParent
			setTitle(CommonText.to)
			setSubtitle(info.safeGet("to"))
		}
		graySquareCell {
			layoutParams.width = matchParent
			setTitle("Quantity")
			setSubtitle(info.safeGet("quantity"))
		}
		graySquareCell {
			layoutParams.width = matchParent
			setTitle(TransactionText.memo)
			setSubtitle(info.safeGet("memo"))
		}
	}
	Dashboard(context) {
		showDashboard(
			"Quick Payment",
			contentLayout,
			"confirmation the transaction from current DAPP, then sign the data",
			hold = {
				transaction.dappTransfer(context, callback)
			},
			cancelAction = {}
		)
	}
}

fun EOSTransactionInfo.dappTransfer(context: Context, callback: (txID: String) -> Unit) {
	trade(context) { response, error ->
		if (error.isNone() && response.isNotNull())
			insertPendingDataToDatabase(response) {
				launchUI {
					callback(response.transactionID)
					response.showDialog(context)
				}
			}
		else if (error.hasError())
			ErrorDisplayManager(error).show(context)
	}
}