package io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.toDoubleOrZero
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.view.GrayCardView
import io.goldstone.blockchain.common.component.ProgressView
import io.goldstone.blockchain.common.component.SpaceSplitLine
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.edittext.RoundTitleInput
import io.goldstone.blockchain.common.component.title.RadioWithTitle
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.wrapContent
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
			setBlueStyle(15.uiPX(), contentWidth)
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
			setNumberPadKeyboard()
			setTitle("EOS Amount")
			setHint("enter eos amount")
		}
	}

	fun setSellingRAMStyle() {
		amountEditText.setTitle("Bytes")
		amountEditText.setHint("enter ram byte amount")
	}

	private val radioCellWidth = 100.uiPX()
	private val transferResourceRadio by lazy {
		RadioWithTitle(context).apply { setTitle("Transfer") }.apply {
			setRadioStatus(true)
			layoutParams = LinearLayout.LayoutParams(radioCellWidth, matchParent)
		}
	}

	private val rentResourceRadio by lazy {
		RadioWithTitle(context).apply { setTitle("Rent") }.apply {
			layoutParams = LinearLayout.LayoutParams(radioCellWidth, matchParent)
		}
	}

	private lateinit var radioContainer: LinearLayout

	init {
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
		getContainer().apply {
			bottomPadding = 10.uiPX()
			processCell.into(this)
			SpaceSplitLine(context).apply {
				layoutParams = LinearLayout.LayoutParams(matchParent, 40.uiPX())
			}.into(this)
			accountNameEditText.into(this)
			amountEditText.into(this)
			radioContainer = linearLayout {
				layoutParams = LinearLayout.LayoutParams(wrapContent, 30.uiPX())
				y = 10.uiPX().toFloat()
				transferResourceRadio.into(this)
				rentResourceRadio.into(this)
				transferResourceRadio.onClick {
					rentResourceRadio.setRadioStatus(false)
					transferResourceRadio.setRadioStatus(true)
					preventDuplicateClicks()
				}
				rentResourceRadio.onClick {
					rentResourceRadio.setRadioStatus(true)
					transferResourceRadio.setRadioStatus(false)
					preventDuplicateClicks()
				}
			}
			confirmButton.into(this)
			accountNameEditText.showButton()
		}
	}

	fun setProcessValue(type: String, subtitle: String, leftValue: BigInteger, rightValue: BigInteger, isTime: Boolean) {
		processCell.setTitle(type)
		processCell.setSubtitle(subtitle)
		processCell.setLeftValue(
			leftValue,
			TokenDetailText.available,
			isTime
		)
		processCell.setRightValue(
			rightValue,
			TokenDetailText.total,
			isTime
		)
	}

	fun showRadios(status: Boolean) {
		radioContainer.visibility = if (status) View.VISIBLE else View.GONE
	}

	fun setAccountHint(hint: String) {
		accountNameEditText.setHint(hint)
	}

	fun setAccount(name: String) {
		accountNameEditText.setContent(name)
	}

	fun getInputValue(): Pair<EOSAccount, Double> {
		return Pair(EOSAccount(accountNameEditText.getContent()), amountEditText.getContent().toDoubleOrZero())
	}

	fun setConfirmClickEvent(action: () -> Unit) {
		confirmButton.onClick {
			action()
			confirmButton.preventDuplicateClicks()
		}
	}

	fun setContactButtonClickEvent(action: () -> Unit) {
		accountNameEditText.setButtonClickEvent(action)
	}

	fun clearInput() {
		accountNameEditText.clearText()
		amountEditText.clearText()
	}

	fun isSelectedTransfer(): Boolean = transferResourceRadio.getRadioStatus()

	fun showLoading(status: Boolean) {
		confirmButton.showLoadingStatus(status)
	}
}