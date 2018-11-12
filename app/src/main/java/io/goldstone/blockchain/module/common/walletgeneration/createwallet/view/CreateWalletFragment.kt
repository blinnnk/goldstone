package io.goldstone.blockchain.module.common.walletgeneration.createwallet.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.replaceFragmentAndSetArgument
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AgreementView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.title.AttentionView
import io.goldstone.blockchain.common.language.CreateWalletText
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.UIUtils.generateDefaultName
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.view.MnemonicBackupFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 22/03/2018 2:23 AM
 * @author KaySaith
 */
class CreateWalletFragment : BaseFragment<CreateWalletPresenter>() {

	override val pageTitle: String = CreateWalletText.create
	private val attentionView by lazy { AttentionView(context!!) }
	private val nameEditText by lazy { RoundInput(context!!) }
	private val passwordEditText by lazy { RoundInput(context!!) }
	private val repeatPasswordEditText by lazy { RoundInput(context!!) }
	private val hintInput by lazy { RoundInput(context!!) }
	private val agreementView by lazy { AgreementView(context!!) }
	private val createButton by lazy { RoundButton(context!!) }
	override val presenter = CreateWalletPresenter(this)

	private var nameText = ""
	private var passwordText = ""
	private var repeatPasswordText = ""

	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, matchParent)
				attentionView.apply {
					text = CreateWalletText.attention
					textSize = fontSize(13)
					textColor = Spectrum.white
					setMargins<LinearLayout.LayoutParams> {
						topMargin = 20.uiPX()
					}
				}.into(this)

				nameEditText.apply {
					title = CreateWalletText.name
					hint = generateDefaultName()
					setMargins<LinearLayout.LayoutParams> {
						topMargin = 30.uiPX()
					}
				}.into(this)

				textView {
					text = CreateWalletText.passwordRules
					textSize = fontSize(12)
					textColor = GrayScale.midGray
					typeface = GoldStoneFont.medium(context)
					gravity = Gravity.CENTER_HORIZONTAL
					layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
					y += 12.uiPX()
				}

				passwordEditText.apply {
					title = CreateWalletText.password
					setPasswordInput()
					setMargins<LinearLayout.LayoutParams> {
						topMargin = 30.uiPX()
					}
				}.into(this)

				repeatPasswordEditText.apply {
					title = CreateWalletText.repeatPassword
					setPasswordInput()
					setMargins<LinearLayout.LayoutParams> {
						topMargin = 10.uiPX()
					}
				}.into(this)

				hintInput.apply {
					title = CreateWalletText.hint
					setTextInput()
					setMargins<LinearLayout.LayoutParams> {
						topMargin = 10.uiPX()
					}
				}.into(this)

				agreementView.apply {
					radioButton.onClick { setRadioStatus() }
					textView.onClick {
						presenter.showAgreementFragment()
					}
				}.into(this)
				createButton.apply {
					text = CreateWalletText.create.toUpperCase()
					setGrayStyle(20.uiPX())
				}.click { button ->
					button.showLoadingStatus()
					CreateWalletPresenter.checkInputValue(
						nameText,
						passwordText,
						repeatPasswordText,
						agreementView.radioButton.isChecked
					) { password, walletName, error ->
						if (error.hasError()) safeShowError(error)
						else presenter.generateWalletWith(
							context!!,
							password!!,
							walletName!!,
							hintInput.text?.toString()
						) {
							if (it.hasError()) safeShowError(it)
							else launchUI { button.showLoadingStatus(false) }
						}
					}
				}.into(this)
			}
		}
	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		updateConfirmButtonStyle()
	}

	fun showMnemonicBackupFragment(arguments: Bundle) {
		// 创建钱包一旦成功跳转到 `备份助记词` 界面就不准许再回到创建的界面防止重复创建账号
		// 所以这里使用了覆盖 `Fragment` 的方法
		getParentFragment<WalletGenerationFragment> {
			replaceFragmentAndSetArgument<MnemonicBackupFragment>(ContainerID.content) {
				putAll(arguments)
			}
			headerTitle = CreateWalletText.mnemonicBackUp
		}
	}

	private fun updateConfirmButtonStyle() {
		nameEditText.afterTextChanged = Runnable {
			nameText = nameEditText.getContent()
			setConfirmButtonStyle(createButton)
		}
		passwordEditText.afterTextChanged = Runnable {
			CreateWalletPresenter.showPasswordSafeLevel(passwordEditText)
			passwordText = passwordEditText.getContent()
			setConfirmButtonStyle(createButton)
		}
		repeatPasswordEditText.afterTextChanged = Runnable {
			repeatPasswordText = repeatPasswordEditText.getContent()
			setConfirmButtonStyle(createButton)
		}
	}

	private fun setConfirmButtonStyle(confirmButton: RoundButton) {
		if (passwordText.length * repeatPasswordText.length != 0)
			confirmButton.setBlueStyle(20.uiPX())
		else confirmButton.setGrayStyle(20.uiPX())
	}
}