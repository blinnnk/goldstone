package io.goldstone.blockchain.module.common.mnemonicconfirmation.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.orZero
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AttentionTextView
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.common.mnemonicconfirmation.presenter.MnemonicConfirmationPresenter
import org.jetbrains.anko.*

/**
 * @date 22/03/2018 11:40 PM
 * @author KaySaith
 */

class MnemonicConfirmationFragment : BaseFragment<MnemonicConfirmationPresenter>() {

  val confirmButton by lazy { RoundButton(context!!) }

  private val attentionTextView by lazy { AttentionTextView(context!!) }

  override val presenter = MnemonicConfirmationPresenter(this)

  override fun AnkoContext<Fragment>.initView() {
    verticalLayout {

      attentionTextView
        .apply {
          text = CreateWalletText.mnemonicConfirmationDescription
        }
        .into(this)

      editText {
        addCorner(CornerSize.default.toInt(), Grayscale.whiteGray)
        layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 120.uiPX()).apply {
          leftMargin = PaddingSize.device
          topMargin = 40.uiPX()
          padding = 20.uiPX()
        }
        hint = "mnemonics split with space"
        hintTextColor = Grayscale.midGray
        textSize = 5.uiPX().toFloat()
        textColor = Grayscale.black
        typeface = GoldStoneFont.heavy(context)
        gravity = Gravity.START
      }

      confirmButton
        .apply {
          text = "Confirm".toUpperCase()
          marginTop = 20.uiPX()
          setBlueStyle()
        }
        .click {  }
        .into(this)

      textView("What is mnemonic?") {
        textSize = 5.uiPX().toFloat()
        typeface = GoldStoneFont.heavy(context)
        layoutParams = LinearLayout.LayoutParams(ScreenSize.Width, 30.uiPX()).apply {
          topMargin = activity?.getRealScreenHeight().orZero() - 460.uiPX() - UIUtils.getHeight(attentionTextView)
        }
        textColor = Spectrum.blue
        gravity = Gravity.CENTER
      }

    }
  }

}