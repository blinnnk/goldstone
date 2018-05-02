package io.goldstone.blockchain.module.common.walletimport.mnemonicimport.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
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
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.common.walletimport.mnemonicimport.presenter.MnemonicImportDetailPresenter
import org.jetbrains.anko.*

/**
 * @date 23/03/2018 1:46 AM
 * @author KaySaith
 */

class MnemonicImportDetailFragment : BaseFragment<MnemonicImportDetailPresenter>() {

  private val confirmButton by lazy { RoundButton(context!!) }
  private val mnemonicInput by lazy { WalletEditText(context!!) }
  private val pathInput by lazy { RoundInput(context!!) }
  private val walletNameInput by lazy { RoundInput(context!!) }
  private val passwordInput by lazy { RoundInput(context!!) }
  private val repeatPassword by lazy { RoundInput(context!!) }
  private val hintInput by lazy { RoundInput(context!!) }
  private val agreementView by lazy { AgreementView(context!!) }

  override val presenter = MnemonicImportDetailPresenter(this)

  override fun AnkoContext<Fragment>.initView() {
    scrollView {
      verticalLayout {
        lparams(matchParent, matchParent)
        mnemonicInput
          .apply {
            hint = ImportWalletText.mnemonicHint
            setMargins<LinearLayout.LayoutParams> { topMargin = 80.uiPX() }
          }
          .into(this)

        pathInput
          .apply {
            text = "Path"
            hint = "m/44'/60'/0'/0/0"
            setMargins<LinearLayout.LayoutParams> { topMargin = 30.uiPX() }
          }
          .into(this)

        walletNameInput
          .apply {
            setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
            text = CreateWalletText.name
          }
          .into(this)

        passwordInput
          .apply {
            setPasswordInput()
            setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
            text = CreateWalletText.password
          }
          .into(this)

        repeatPassword
          .apply {
            setPasswordInput()
            setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
            text = CreateWalletText.repeatPassword
          }
          .into(this)

        hintInput
          .apply {
            setTextInput()
            setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
            text = CreateWalletText.hint
          }
          .into(this)

        agreementView.into(this)

        confirmButton
          .apply {
            text = CommonText.confirm.toUpperCase()
            setBlueStyle()
            y += 10.uiPX()
          }
          .click {
            presenter.importWalletByMnemonic(
              mnemonicInput,
              passwordInput,
              repeatPassword,
              hintInput,
              agreementView.radioButton.isChecked,
              walletNameInput
            )
            it.preventDuplicateClicks()
          }
          .into(this)


        textView("What is mnemonic?") {
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