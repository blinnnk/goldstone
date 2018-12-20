package io.goldstone.blockchain.module.home.dapp.common

import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.getTargetObject
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.cell.graySquareCell
import io.goldstone.blockchain.common.component.overlay.Dashboard
import io.goldstone.blockchain.common.component.valueView
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.eos.transaction.EOSTransactionInfo
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.wrapContent
import org.json.JSONObject


/**
 * @author KaySaith
 * @date  2018/12/09
 */
fun ViewGroup.showQuickPaymentDashboard(
	data: JSONObject,
	isSampleTransfer: Boolean, // 如果是 `isTransactionObject` 传入的 `JSONObject` 是含有全量信息的
	cancelEvent: () -> Unit,
	confirmEvent: () -> Unit,
	callback: (EOSResponse?, GoldStoneError) -> Unit
) {
	// 如果是转账操作那么 `DAPP` 都会把具体信息放在 `memo` 里面
	// 解除 `memo` 里面的 `count` 计算出当前需要转账的货币的 `Decimal` 作为参数
	val info = if (!isSampleTransfer) data.getTargetObject("data") else data
	// 渠道分成需要在 `memo` 标记渠道, 一些 `DAPP` 会自动添加在 `memo` 里, 但是 `BetDice` 需要手动添加
	val memo = if (info.safeGet("to").equals("betdicebacca", true))
		"${info.safeGet("memo")},ref:goldstonebet"
	else info.safeGet("memo")
	val transaction =
		if (!isSampleTransfer) EOSTransactionInfo(data, memo)
		else EOSTransactionInfo(data)
	val contentLayout = LinearLayout(context).apply {
		orientation = LinearLayout.VERTICAL
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
		padding = PaddingSize.content
		valueView {
			addCorner(CornerSize.normal.toInt(), GrayScale.whiteGray)
			layoutParams.width = matchParent
			setContent(memo)
		}.setMargins<LinearLayout.LayoutParams> {
			bottomMargin = 10.uiPX()
		}
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
	}
	Dashboard(context) {
		showDashboard(
			"Quick Payment",
			"confirmation the transaction from current DAPP, then sign the data",
			contentLayout,
			hold = {
				confirmEvent()
				transaction.trade(context, callback)
			},
			cancelAction = cancelEvent
		)
	}
}

fun ViewGroup.showOperationDashboard(
	data: JSONObject,
	cancelEvent: () -> Unit,
	confirmEvent: () -> Unit
) {
	val contentLayout = LinearLayout(context).apply {
		orientation = LinearLayout.VERTICAL
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
		padding = PaddingSize.content
		graySquareCell {
			layoutParams.width = matchParent
			setTitle("Contract")
			setSubtitle(data.safeGet("account"))
		}

		valueView {
			addCorner(CornerSize.normal.toInt(), GrayScale.whiteGray)
			layoutParams.width = matchParent
			setContent(data.safeGet("data"))
		}.setMargins<LinearLayout.LayoutParams> {
			topMargin = PaddingSize.content
		}
	}
	Dashboard(context) {
		dialog.cancelOnTouchOutside(false)
		showDashboard(
			"Contract Operation",
			"contract need you to sign the data, and proof it is valid operation",
			contentLayout,
			hold = {
				confirmEvent()
			},
			cancelAction = cancelEvent
		)
	}
}