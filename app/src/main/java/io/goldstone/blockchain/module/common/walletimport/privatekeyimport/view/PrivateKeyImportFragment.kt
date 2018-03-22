package io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view

import android.graphics.Color
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AgreementView
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.component.WalletEditText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.into
import io.goldstone.blockchain.common.utils.setMargins
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter.PrivateKeyImportPresenter
import org.jetbrains.anko.*

/**
 * @date 23/03/2018 2:13 AM
 * @author KaySaith
 */

class PrivateKeyImportFragment : BaseFragment<PrivateKeyImportPresenter>() {

  private val privateKeyInput by lazy { WalletEditText(context!!) }
  private val passwordHintInput by lazy { RoundInput(context!!) }
  private val passwordInput by lazy { RoundInput(context!!) }
  private val repeatPassword by lazy { RoundInput(context!!) }
  private val agreementView by lazy { AgreementView(context!!) }
  private val confirmButton by lazy { RoundButton(context!!) }

  override val presenter = PrivateKeyImportPresenter(this)

  override fun AnkoContext<Fragment>.initView() {
    scrollView {
      verticalLayout {
        privateKeyInput
          .apply {
            setMargins<LinearLayout.LayoutParams> { topMargin = 80.uiPX() }
          }
          .into(this)

        passwordInput
          .apply {
            setMargins<LinearLayout.LayoutParams> { topMargin = 30.uiPX() }
            text = CreateWalletText.password
          }
          .into(this)

        repeatPassword
          .apply {
            setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
            text = CreateWalletText.repeatPassword
          }
          .into(this)

        passwordHintInput
          .apply {
            text = "Password Hint (Optional)"
            setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
          }
          .into(this)

        agreementView.into(this)

        confirmButton
          .apply {
            text = CommonText.confirm.toUpperCase()
            setBlueStyle()
          }
          .into(this)


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