package io.goldstone.blockchain.module.common.walletimport.mnemonicimport.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AgreementView
import io.goldstone.blockchain.common.component.MnemonicEditText
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.into
import io.goldstone.blockchain.common.utils.setMargins
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.common.walletimport.mnemonicimport.presenter.MnemonicImportDetailPresenter
import org.jetbrains.anko.*

/**
 * @date 23/03/2018 1:46 AM
 * @author KaySaith
 */

class MnemonicImportDetailFragment : BaseFragment<MnemonicImportDetailPresenter>() {

  private val mnemonicInput by lazy { MnemonicEditText(context!!) }
  private val pathInput by lazy { RoundInput(context!!) }
  private val passwordInput by lazy { RoundInput(context!!) }
  private val repeatPassword by lazy { RoundInput(context!!) }
  private val agreementView by lazy { AgreementView(context!!) }
  private val confirmButton by lazy { RoundButton(context!!) }

  override val presenter = MnemonicImportDetailPresenter(this)

  override fun AnkoContext<Fragment>.initView() {
    scrollView {
      verticalLayout {
        lparams(matchParent, matchParent)
        mnemonicInput
          .apply {
            setMargins<LinearLayout.LayoutParams> { topMargin = 80.uiPX() }
          }
          .into(this)

        pathInput
          .apply {
            text = "Path"
            setMargins<LinearLayout.LayoutParams> { topMargin = 30.uiPX() }
          }
          .into(this)

        passwordInput
          .apply {
            setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
            text = CreateWalletText.password
          }
          .into(this)

        repeatPassword
          .apply {
            setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
            text = CreateWalletText.repeatPassword
          }
          .into(this)

        agreementView.into(this)

        confirmButton
          .apply {
            setMargins<LinearLayout.LayoutParams> { topMargin = 30.uiPX() }
            text = CommonText.confirm
            setBlueStyle()
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