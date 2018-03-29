package io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.getRealScreenHeight
import com.blinnnk.extension.into
import com.blinnnk.extension.orZero
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AttentionTextView
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.WalletEditText
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.CreateWalletText
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.util.HoneyUIUtils
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.presenter.MnemonicConfirmationPresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

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
      System.out.println(mnemonicCode)
      attentionTextView
        .apply { text = CreateWalletText.mnemonicConfirmationDescription }
        .into(this)

      mnemonicInput
        .into(this)

      confirmButton
        .apply {
          text = CommonText.confirm.toUpperCase()
          marginTop = 20.uiPX()
          setBlueStyle()
        }
        .click {
          presenter.clickConfirmationButton(mnemonicCode.orEmpty(), mnemonicInput.text.toString())
        }
        .into(this)

      textView("What is mnemonic?") {
        textSize = 5.uiPX().toFloat()
        typeface = GoldStoneFont.heavy(context)
        layoutParams = LinearLayout.LayoutParams(ScreenSize.Width, 30.uiPX()).apply {
          topMargin = activity?.getRealScreenHeight().orZero() - 460.uiPX() - HoneyUIUtils.getHeight(attentionTextView)
        }
        textColor = Spectrum.blue
        gravity = Gravity.CENTER
      }
    }
  }

}