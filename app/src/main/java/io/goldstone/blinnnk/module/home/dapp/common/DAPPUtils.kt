package io.goldstone.blinnnk.module.home.dapp.common

import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.component.cell.graySquareCell
import io.goldstone.blinnnk.common.component.overlay.Dashboard
import io.goldstone.blinnnk.common.component.valueView
import io.goldstone.blinnnk.common.error.GoldStoneError
import io.goldstone.blinnnk.common.language.AlertText
import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.common.value.CornerSize
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.PaddingSize
import io.goldstone.blinnnk.crypto.eos.EOSTransactionMethod
import io.goldstone.blinnnk.crypto.eos.base.EOSResponse
import io.goldstone.blinnnk.crypto.eos.transaction.EOSAction
import io.goldstone.blinnnk.crypto.eos.transaction.EOSTransactionInfo
import io.goldstone.blinnnk.crypto.keystore.toJsonObject
import io.goldstone.blinnnk.kernel.network.eos.MultipleActionsTransaction
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.wrapContent
import org.json.JSONObject


/**
 * @author KaySaith
 * @date  2018/12/09
 * @Description
 * 这个方法包含了多 `Action`, 单独 `Action` 和 `Simple Transfer` 集中情况
 * 多 `Action` 就是包含 `Name` 为 `Transfer` 以及包含 `Name` 为自定义的 `DAPP Trading`
 * 独立 `Action` 就是常见的 Name 为 `Transfer Data` 为 标准, `From To Quantity Memo` 的 类型.
 * `Simple Transfer` 是 `Scatter` 不同 `Dapp` 使用过程中会只传递 `Data {from, to, quantity, memo}` 的
 * 特殊自定义 `Object`. 这个函数会分别解析这几种情况的交易.
 */
fun ViewGroup.showQuickPaymentDashboard(
	actions: List<JSONObject>,
	isSampleTransfer: Boolean, // 如果是 `isTransactionObject` 传入的 `JSONObject` 是含有全量信息的
	dappChainURL: String,
	cancelEvent: () -> Unit,
	confirmEvent: () -> Unit,
	callback: (EOSResponse?, GoldStoneError) -> Unit
) {
	// 如果是转账操作那么 `DAPP` 都会把具体信息放在 `memo` 里面
	// 解出 `memo` 里面的 `count` 计算出当前需要转账的货币的 `Decimal` 作为参数
	val info =
		if (!isSampleTransfer) actions.find {
			EOSTransactionMethod(it.safeGet("name")).isTransfer()
		}?.safeGet("data")?.toJsonObject()!! else actions[0]
	// 渠道分成需要在 `memo` 标记渠道, 一些 `DAPP` 会自动添加在 `memo` 里, 但是 `BetDice` 需要手动添加
	val memo = info.safeGet("memo").completeMemoChannel(info.safeGet("to"))
	// `Quantity` 解析 `Decimal` 和 `Symbol` 而用
	val quantity = info.safeGet("quantity")
	val transaction =
		if (actions.size > 1) MultipleActionsTransaction(quantity, actions.map { EOSAction(it) })
		else if (!isSampleTransfer) EOSTransactionInfo(actions[0], memo)
		else EOSTransactionInfo(actions[0])
	val contentLayout = LinearLayout(context).apply {
		orientation = LinearLayout.VERTICAL
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
		padding = PaddingSize.content
		if (memo.isNotEmpty()) valueView {
			addCorner(CornerSize.normal.toInt(), GrayScale.whiteGray)
			layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
			setContent(memo)
		}.setMargins<LinearLayout.LayoutParams> {
			bottomMargin = 10.uiPX()
		}
		val dataObjects = if (!isSampleTransfer) actions.map {
			it.getTargetObject("data")
		} else actions
		dataObjects.forEach { data ->
			val allNames = data.names()
			allNames.toList().forEach { name ->
				// `Memo` 在上面的 `ValueView` 已经显示了, 这里过滤掉
				if (data.getString(name).isNotEmpty() && !name.equals("memo", true)) {
					graySquareCell {
						layoutParams.width = matchParent
						setTitle(name)
						setSubtitle(data.getString(name))
					}
				}
			}
		}
	}
	Dashboard(context) {
		showDashboard(
			AlertText.transferRequestTitle,
			AlertText.transferRequestDescription,
			contentLayout,
			hold = {
				confirmEvent()
				when (transaction) {
					is EOSTransactionInfo -> transaction.trade(
						context,
						if (dappChainURL.isNotEmpty()) dappChainURL else SharedChain.getEOSCurrent().getURL(),
						cancelAction = cancelEvent,
						hold = callback
					)
					is MultipleActionsTransaction -> transaction.trade(
						context,
						cancelAction = cancelEvent,
						hold = callback
					)
				}
			},
			cancelAction = {
				cancelEvent()
				dismiss()
			}
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
		val dataInfo = data.safeGet("data")
		if (dataInfo.isNotEmpty()) valueView {
			addCorner(CornerSize.normal.toInt(), GrayScale.whiteGray)
			layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
			setContent(dataInfo)
		}.setMargins<LinearLayout.LayoutParams> {
			topMargin = PaddingSize.content
		}
	}
	Dashboard(context) {
		cancelOnTouchOutside()
		showDashboard(
			AlertText.signDataRequestTitle,
			AlertText.signDataRequestDescription,
			contentLayout,
			hold = {
				confirmEvent()
			},
			cancelAction = {
				cancelEvent()
				dismiss()
			}
		)
	}
}

// 渠道分成需要在 `memo` 标记渠道, 一些 `DAPP` 会自动添加在 `memo` 里, 但是 `BetDice` 需要手动添加
private fun String.completeMemoChannel(toAccount: String): String {
	return if (
		toAccount.equals("betdicebacca", true) ||
		toAccount.equals("betdiceadmin", true)
	) "$this,ref:goldstonebet"
	else this
}