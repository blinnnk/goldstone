package io.goldstone.blinnnk.module.common.walletgeneration.mnemonicconfirmation.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.base.basefragment.BaseFragment
import io.goldstone.blinnnk.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blinnnk.common.component.button.RoundButton
import io.goldstone.blinnnk.common.component.button.roundButton
import io.goldstone.blinnnk.common.component.edittext.WalletEditText
import io.goldstone.blinnnk.common.component.title.AttentionTextView
import io.goldstone.blinnnk.common.component.title.ExplanationTitle
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.language.CreateWalletText
import io.goldstone.blinnnk.common.language.QAText
import io.goldstone.blinnnk.common.language.WalletSettingsText
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.common.value.*
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.module.common.walletgeneration.mnemonicconfirmation.presenter.MnemonicConfirmationPresenter
import io.goldstone.blinnnk.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blinnnk.module.common.webview.view.WebViewFragment
import io.goldstone.blinnnk.module.entrance.splash.view.SplashActivity
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import io.goldstone.blinnnk.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 22/03/2018 11:40 PM
 * @author KaySaith
 */
class MnemonicConfirmationFragment : BaseFragment<MnemonicConfirmationPresenter>() {

	override val pageTitle: String = CreateWalletText.mnemonicConfirmation
	private val mnemonicCode by lazy { arguments?.getString(ArgumentKey.mnemonicCode) }
	private lateinit var confirmButton: RoundButton
	private val mnemonicInput by lazy { WalletEditText(context!!) }
	private val attentionTextView by lazy { AttentionTextView(context!!) }
	override val presenter = MnemonicConfirmationPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)

			attentionTextView.apply { text = CreateWalletText.mnemonicConfirmationDescription }.into(this)

			mnemonicInput.apply {
				hint = WalletSettingsText.backUpMnemonicGotBefore
			}.into(this)
			// 根据助记词生成勾选助记词的按钮集合
			relativeLayout {
				layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, 180.uiPX())
				y += 20.uiPX()
				var contentWidth = 0
				var contentTopMargin = 0
				var modulus = 0

				mnemonicCode?.split(" ".toRegex())?.shuffled()?.forEachIndexed { index, content ->
					val wordWidth = content.measureTextWidth(15.uiPX().toFloat()).toInt() + 20.uiPX()
					var isSelected = false
					textView {
						id = index
						text = content
						textColor = Spectrum.blue
						textSize = fontSize(15)
						typeface = GoldStoneFont.black(context)
						addCorner(15.uiPX(), GrayScale.whiteGray)
						layoutParams = RelativeLayout.LayoutParams(wordWidth, 30.uiPX())
						gravity = Gravity.CENTER

						onClick {
							selectMnemonic(mnemonicInput, !isSelected)
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

			confirmButton = roundButton {
				text = CommonText.confirm.toUpperCase()
				marginTop = 20.uiPX()
				setBlueStyle()
			}.click {
				presenter.clickConfirmationButton(mnemonicCode.orEmpty(), mnemonicInput.text.toString())
			}

			ExplanationTitle(context).apply {
				text = QAText.whatIsMnemonic.setUnderline()
			}.click {
				parentFragment.let {
					when (it) {
						is WalletGenerationFragment -> it.showWebView()
						is WalletSettingsFragment -> it.showWebView()
					}
				}
			}.into(this)
		}
	}

	private fun BaseOverlayFragment<*>.showWebView() {
		presenter.showTargetFragment<WebViewFragment>(
			Bundle().apply {
				putString(ArgumentKey.webViewUrl, WebUrl.whatIsMnemonic)
				putString(ArgumentKey.webViewName, QAText.whatIsMnemonic)
			}
		)
	}

	@SuppressLint("SetTextI18n")
	private fun TextView.selectMnemonic(
		input: EditText,
		isSelected: Boolean
	) {
		if (!isSelected) {
			addCorner(CornerSize.default.toInt(), GrayScale.whiteGray)
			textColor = Spectrum.blue
			if (input.text.toString().isNotEmpty()) {
				if (input.text.toString().contains(" ")) {
					input.setText(input.text.toString().replace((" " + text.toString()), ""))
				} else {
					input.setText(input.text.toString().replace((text.toString()), ""))
				}
			}
		} else {
			addCorner(CornerSize.default.toInt(), Spectrum.blue)
			textColor = Spectrum.white
			val newContent = if (input.text.isEmpty()) text.toString() else " " + text.toString()
			input.setText(input.text.toString() + newContent)
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		if (parent is BaseOverlayFragment<*>) {
			parent.presenter.popFragmentFrom<MnemonicConfirmationFragment>()
		}
	}

	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(view, savedInstanceState)
		backEventForSplashActivity()
	}

	private fun backEventForSplashActivity() {
		val currentActivity = activity
		if (currentActivity is SplashActivity) {
			currentActivity.backEvent = Runnable {
				getParentFragment<WalletGenerationFragment> {
					headerTitle = CreateWalletText.mnemonicBackUp
					presenter.popFragmentFrom<MnemonicConfirmationFragment>()
				}
			}
		}
	}
}