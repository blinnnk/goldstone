package io.goldstone.blockchain.module.common.walletgeneration.createwallet.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AgreementView
import io.goldstone.blockchain.common.component.AttentionView
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.verticalLayout

/**
 * @date 22/03/2018 2:23 AM
 * @author KaySaith
 */

class CreateWalletFragment : BaseFragment<CreateWalletPresenter>() {

	private val attentionView by lazy { AttentionView(context!!) }
	private val nameEditText by lazy { RoundInput(context!!) }
	private val passwordEditText by lazy { RoundInput(context!!) }
	private val repeatPasswordEditText by lazy { RoundInput(context!!) }
	private val hintInput by lazy { RoundInput(context!!) }
	private val agreementView by lazy { AgreementView(context!!) }
	private val createButton by lazy { RoundButton(context!!) }

	override val presenter = CreateWalletPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			lparams(matchParent, matchParent)

			attentionView.apply {
				text = CreateWalletText.attention
				textSize = 4.uiPX().toFloat()
				textColor = Spectrum.white
				setMargins<LinearLayout.LayoutParams> {
					topMargin = 20.uiPX()
				}
			}.into(this)

			nameEditText.apply {
				title = CreateWalletText.name
				setMargins<LinearLayout.LayoutParams> {
					topMargin = 30.uiPX()
				}
			}.into(this)

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
				textView.onClick { presenter.showAgreementFragment() }
			}.into(this)

			createButton.apply {
				text = CreateWalletText.create.toUpperCase()
				setGrayStyle()
				setMargins<LinearLayout.LayoutParams> {
					topMargin = 20.uiPX()
				}
			}.click {
				presenter.generateWalletWith(agreementView.radioButton.isChecked, hintInput)
			}.into(this)
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter.updateConfirmButtonStyle(
			nameEditText, passwordEditText, repeatPasswordEditText, createButton
		)
	}

}