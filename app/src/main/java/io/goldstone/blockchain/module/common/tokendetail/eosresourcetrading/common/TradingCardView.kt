package io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common

import android.content.Context
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.GrayCardView
import io.goldstone.blockchain.common.component.ProgressView
import io.goldstone.blockchain.common.component.RoundTitleInput
import io.goldstone.blockchain.common.component.SpaceSplitLine
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.matchParent
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/19
 */
class TradingCardView(context: Context) : GrayCardView(context) {

	private val contentWidth = ScreenSize.widthWithPadding - 30.uiPX()

	private val processCell by lazy {
		ProgressView(context).apply {
			setSubtitle(CommonText.calculating)
		}
	}

	private val confirmButton by lazy {
		RoundButton(context).apply {
			setBlueStyle(20.uiPX(), contentWidth)
			text = CommonText.confirm
		}
	}

	private val accountNameEditText by lazy {
		RoundTitleInput(context).apply {
			layoutParams = RelativeLayout.LayoutParams(contentWidth, 46.uiPX())
			setTitle("Account")
			setHint("enter account name")
		}
	}

	private val amountEditText by lazy {
		RoundTitleInput(context).apply {
			layoutParams = RelativeLayout.LayoutParams(contentWidth, 46.uiPX()).apply {
				topMargin = 10.uiPX()
			}
			setTitle("EOS Amount")
			setHint("enter eos amount")
		}
	}

	init {
		setCardParams(ScreenSize.widthWithPadding, 300.uiPX())
		addView(processCell)
		SpaceSplitLine(context).apply {
			layoutParams = LinearLayout.LayoutParams(matchParent, 40.uiPX())
		}.into(getContainer())
		accountNameEditText.into(getContainer())
		amountEditText.into(getContainer())
		addView(confirmButton)
		accountNameEditText.showButton()
	}

	fun setProcessValue(type: String, subtitle: String, leftValue: BigInteger, rightValue: BigInteger) {
		processCell.setTitle(type)
		processCell.setSubtitle(subtitle)
		processCell.setLeftValue(
			leftValue,
			TokenDetailText.available,
			true
		)
		processCell.setRightValue(
			rightValue,
			TokenDetailText.total,
			true
		)
	}

	fun setAccountHint(hint: String) {
		accountNameEditText.setHint(hint)
	}
}