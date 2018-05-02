package io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AgreementView
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.component.WalletEditText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter.PrivateKeyImportPresenter
import org.jetbrains.anko.*

/**
 * @date 23/03/2018 2:13 AM
 * @author KaySaith
 */

class PrivateKeyImportFragment : BaseFragment<PrivateKeyImportPresenter>() {

  private val privateKeyInput by lazy { WalletEditText(context!!) }
  private val passwordHintInput by lazy { RoundInput(context!!) }

  private val nameInput by lazy { RoundInput(context!!) }
  private val passwordInput by lazy { RoundInput(context!!) }
  private val repeatPassword by lazy { RoundInput(context!!) }
  private val agreementView by lazy { AgreementView(context!!) }
  private val confirmButton by lazy { RoundButton(context!!) }

  override val presenter = PrivateKeyImportPresenter(this)

  override fun AnkoContext<Fragment>.initView() {
    scrollView {
      verticalLayout {

        privateKeyInput.apply {
          hint = ImportWalletText.privateKeyHint
          setMargins<LinearLayout.LayoutParams> { topMargin = 80.uiPX() }
        }.into(this)

        nameInput.apply {
          setMargins<LinearLayout.LayoutParams> { topMargin = 30.uiPX() }
          text = CreateWalletText.name
        }.into(this)

        passwordInput.apply {
          setPasswordInput()
          setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
          text = CreateWalletText.password
        }.into(this)

        repeatPassword.apply {
          setPasswordInput()
          setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
          text = CreateWalletText.repeatPassword
        }.into(this)

        passwordHintInput.apply {
          text = CreateWalletText.hint
          setTextInput()
          setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
        }.into(this)

        agreementView.into(this)

        confirmButton.apply {
          text = CommonText.confirm.toUpperCase()
          setBlueStyle()
          y += 10.uiPX()
        }.click {
            presenter.importWalletByPrivateKey(
              privateKeyInput,
              passwordInput,
              repeatPassword,
              agreementView.radioButton.isChecked,
              nameInput,
              passwordHintInput
            )
          }.into(this)


        textView("What is private key?") {
          textSize = 5.uiPX().toFloat()
          typeface = GoldStoneFont.heavy(context)
          layoutParams = LinearLayout.LayoutParams(ScreenSize.Width, 30.uiPX()).apply {
            topMargin = 20.uiPX()
          }
          textColor = Spectrum.blue
          gravity = Gravity.CENTER
        }
      }
    }
  }


}