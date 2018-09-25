package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
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
		text = "The username must be 12 characters long, and the character content can only contain the letters A~Z or the numbers 1~5."
		return this
	}

	fun isRegisterResource(): DescriptionView {
		text = "Registering an account requires injecting a certain amount of resources into the new account, so that the new account can complete the most basic operations."
		return this
	}

	fun isRegisterByFriend(): DescriptionView {
		text = "Copy the public key and user name and send it to your friends with EOS resources, and register them according to the conditions given."
		return this
	}

	fun isRegisterBySmartContract(isLeft: Boolean = false): DescriptionView {
		if (isLeft) gravity = Gravity.START
		text = "Contract registration is all done automatically, the contract code open source can be browsed at will. Note that the contract registration should be careful, once the error due to operational errors can not recover the loss."
		return this
	}

	fun isResourceAssign(): DescriptionView {
		text = "Contract registration is all done automatically, the contract code open source can be browsed at will. Note that the contract registration should be careful, once the error due to operational errors can not recover the loss."
		return this
	}

	fun isExportKeyStore(): DescriptionView {
		text = ImportWalletText.exportKeystore
		return this
	}

	fun isAvailableAccountName(): DescriptionView {
		text = "Congratulations, this name has not yet been registered by others. If you have friends using GoldStone Wallet, you can go to the settings interface, register the EOS account module, and register with the following information."
		return this
	}

	fun isExportPrivateKey(): DescriptionView {
		text = ImportWalletText.exportPrivateKey
		return this
	}
}