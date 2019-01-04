package io.goldstone.blinnnk.common.component

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.language.EOSAccountText
import io.goldstone.blinnnk.common.language.FingerprintPaymentText
import io.goldstone.blinnnk.common.language.ImportWalletText
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.ElementID
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.padding
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent


/**
 * @author KaySaith
 * @date  2018/09/25
 */
class DescriptionView(context: Context) : TextView(context) {
	init {
		gravity = Gravity.CENTER_HORIZONTAL
		padding = 20.uiPX()
		id = ElementID.attentionText
		textSize = fontSize(14)
		textColor = GrayScale.midGray
		typeface = GoldStoneFont.heavy(context)
		layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent)
	}

	fun isNameRule(): DescriptionView {
		text = EOSAccountText.activateForFriendHint
		return this
	}

	fun isFingerprint(): DescriptionView {
		text = FingerprintPaymentText.featureDescription
		return this
	}

	fun isRegisterResource(): DescriptionView {
		text = EOSAccountText.activeByContractSpendDescription
		return this
	}

	fun isRegisterByFriend(): DescriptionView {
		text = EOSAccountText.activeByFriendHint
		return this
	}

	fun isRegisterBySmartContract(isLeft: Boolean = false): DescriptionView {
		if (isLeft) gravity = Gravity.START
		text = EOSAccountText.activeByContractHint
		return this
	}

	fun isExportKeyStore(): DescriptionView {
		text = ImportWalletText.exportKeystore
		return this
	}

	fun isAvailableAccountName(): DescriptionView {
		text = EOSAccountText.checkNameResultAvailable
		return this
	}

	fun isExportPrivateKey(): DescriptionView {
		text = ImportWalletText.exportPrivateKey
		return this
	}
}