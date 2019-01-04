package io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.common

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.toDoubleOrZero
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.base.view.GrayCardView
import io.goldstone.blinnnk.common.component.ProcessType
import io.goldstone.blinnnk.common.component.ProgressView
import io.goldstone.blinnnk.common.component.SpaceSplitLine
import io.goldstone.blinnnk.common.component.button.RoundButton
import io.goldstone.blinnnk.common.component.edittext.RoundTitleInput
import io.goldstone.blinnnk.common.component.title.RadioWithTitle
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.language.TokenDetailText
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.crypto.eos.EOSUnit
import io.goldstone.blinnnk.crypto.eos.account.EOSAccount
import io.goldstone.blinnnk.crypto.multichain.CoinSymbol
import io.goldstone.blinnnk.crypto.utils.formatCount
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.wrapContent
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/19
 */
class TradingCardView(context: Context) : GrayCardView(context) {

	private val contentWidth = ScreenSize.widthWithPadding - 30.uiPX()

	private val processCell = ProgressView(context).apply {
		setSubtitle(CommonText.calculating)
	}

	private val confirmButton = RoundButton(context).apply {
		setBlueStyle(15.uiPX(), contentWidth)
		text = CommonText.confirm
	}

	private val accountNameEditText = RoundTitleInput(context).apply {
		layoutParams = RelativeLayout.LayoutParams(contentWidth, 46.uiPX())
		setTitle(TokenDetailText.tradeForAccountTitle)
		setHint(TokenDetailText.tradeForAccountPlaceholder)
	}

	var amountEditTextChanged: Runnable? = null
	private val amountEditText = RoundTitleInput(context).apply {
		setNumberPadKeyboard()
		setTitle(TokenDetailText.eosAmountTitle)
		setHint(TokenDetailText.eosAmountPlaceholder)
		onTextChanged = Runnable {
			amountEditTextChanged?.run()
		}
	}

	fun setSellingRAMStyle() {
		accountNameEditText.visibility = View.GONE
		amountEditText.setTitle(TokenDetailText.tradeRamByBytesTitle)
		amountEditText.setHint(TokenDetailText.tradeRamByBytesPlaceholder)
	}

	fun showRAMEOSCount(eosCount: Double) {
		amountEditText.setTitle(TokenDetailText.tradeRamByBytesTitle + " ≈ ${eosCount.formatCount(4)} ${CoinSymbol.EOS.symbol}")
	}

	fun showRAMAmount(amount: Double) {
		amountEditText.setTitle(TokenDetailText.eosAmountTitle + " ≈ ${amount.formatCount(2)} ${EOSUnit.KB.value}")
	}

	private val radioCellWidth = 110.uiPX()
	private val transferResourceRadio by lazy {
		RadioWithTitle(context).apply {
			setTitle(TokenDetailText.delegateTypeTransfer)
			setRadioStatus(true)
			layoutParams = LinearLayout.LayoutParams(radioCellWidth, matchParent)
		}
	}

	private val rentResourceRadio by lazy {
		RadioWithTitle(context).apply { setTitle(TokenDetailText.delegateTypeRent) }.apply {
			layoutParams = LinearLayout.LayoutParams(radioCellWidth, matchParent)
		}
	}

	private var radioContainer: LinearLayout

	init {
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
		container.apply {
			bottomPadding = 10.uiPX()
			processCell.into(this)
			SpaceSplitLine(context).apply {
				layoutParams = LinearLayout.LayoutParams(matchParent, 40.uiPX())
			}.into(this)

			accountNameEditText.into(this)
			amountEditText.into(this)
			// 兼容 `API 21/22` 在添加后才识别 `Margin`
			val params =
				LinearLayout.LayoutParams(contentWidth, 46.uiPX())
			params.topMargin = 10.uiPX()
			amountEditText.layoutParams = params

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

	fun setProcessValue(type: String, subtitle: String, leftValue: BigInteger, rightValue: BigInteger, processType: ProcessType) {
		processCell.setTitle(type)
		processCell.setSubtitle(subtitle)
		processCell.setLeftValue(
			leftValue,
			TokenDetailText.available,
			processType
		)
		processCell.setRightValue(
			rightValue,
			TokenDetailText.total,
			processType
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
		launchUI {
			confirmButton.showLoadingStatus(status)
		}
	}
}