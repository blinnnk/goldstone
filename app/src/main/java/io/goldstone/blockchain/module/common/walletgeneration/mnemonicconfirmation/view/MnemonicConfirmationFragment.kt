package io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AttentionTextView
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.WalletEditText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.presenter.MnemonicConfirmationPresenter
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 22/03/2018 11:40 PM
 * @author KaySaith
 */

class MnemonicConfirmationFragment : BaseFragment<MnemonicConfirmationPresenter>() {

	private val mnemonicCode by lazy { arguments?.getString(ArgumentKey.mnemonicCode) }
	private val confirmButton by lazy { RoundButton(context!!) }
	private val mnemonicInput by lazy { WalletEditText(context!!) }
	private val attentionTextView by lazy { AttentionTextView(context!!) }

	override val presenter = MnemonicConfirmationPresenter(this)

	override fun AnkoContext<Fragment>.initView() {

		verticalLayout {

			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)

			attentionTextView.apply { text = CreateWalletText.mnemonicConfirmationDescription }.into(this)

			mnemonicInput.apply {
				hint = "confirm mnemonic which you got before."
			}.into(this)

			// 根据助记词生成勾选助记词的按钮集合
			relativeLayout {
				layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, 180.uiPX())
				y += 20.uiPX()
				var contentWidth = 0
				var contentTopMargin = 0
				var modulus = 0

				mnemonicCode?.split(" ".toRegex())?.shuffled()?.forEachIndexed { index, content ->
					val wordWidth = content.count() * 12.uiPX() + 10.uiPX()
					var isSelected = false
					textView {
						id = index
						text = content
						textColor = Spectrum.blue
						textSize = 5.uiPX().toFloat()
						typeface = GoldStoneFont.black(context)
						addCorner(CornerSize.default.toInt(), GrayScale.whiteGray)
						layoutParams = RelativeLayout.LayoutParams(
							wordWidth, 30.uiPX()
						)
						gravity = Gravity.CENTER

						onClick {
							selectMnemonic(mnemonicInput, isSelected)
							isSelected = !isSelected
						}

						if (contentWidth > ScreenSize.widthWithPadding - 110.uiPX()) {
							contentWidth = 0
							modulus = index
							contentTopMargin += 35.uiPX()
						}
						x = contentWidth.toFloat() + (index - modulus) * 10.uiPX()
						y = contentTopMargin.toFloat()
						contentWidth += wordWidth
					}
				}
			}

			confirmButton.apply {
				text = CommonText.confirm.toUpperCase()
				marginTop = 20.uiPX()
				setBlueStyle()
			}.click {
				presenter.clickConfirmationButton(mnemonicCode.orEmpty(), mnemonicInput.text.toString())
			}.into(this)

			textView("What is mnemonic?") {
				textSize = 5.uiPX().toFloat()
				typeface = GoldStoneFont.heavy(context)
				layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, 50.uiPX())
				textColor = Spectrum.blue
				gravity = Gravity.CENTER
			}
		}
	}

	@SuppressLint("SetTextI18n")
	private fun TextView.selectMnemonic(
		input: EditText,
		isSelected: Boolean
	) {
		if (isSelected) {
			addCorner(CornerSize.default.toInt(), GrayScale.whiteGray)
			textColor = Spectrum.blue
			input.setText(input.text.toString().replace((" " + text.toString()), ""))
		} else {
			addCorner(CornerSize.default.toInt(), Spectrum.blue)
			textColor = Spectrum.white
			val newContent = if (input.text.isEmpty()) text.toString() else " " + text.toString()
			input.setText(input.text.toString() + newContent)
		}
	}

}