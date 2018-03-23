package io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AttentionTextView
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.into
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.presenter.MnemonicBackupPresenter
import org.jetbrains.anko.*

/**
 * @date 22/03/2018 9:32 PM
 * @author KaySaith
 */

class MnemonicBackupFragment : BaseFragment<MnemonicBackupPresenter>() {

  val mnemonic by lazy { TextView(context) }
  val confirmButton by lazy { RoundButton(context!!) }

  private val attentionTextView by lazy { AttentionTextView(context!!) }

  override val presenter = MnemonicBackupPresenter(this)

  @SuppressLint("SetTextI18n")
  override fun AnkoContext<Fragment>.initView() {
    verticalLayout {

      lparams(matchParent, matchParent)

      attentionTextView
        .apply {
          text = CreateWalletText.mnemonicBackupAttention
        }
        .into(this)

      mnemonic
        .apply {
          addCorner(CornerSize.default.toInt(), GrayScale.whiteGray)
          layoutParams = LinearLayout.LayoutParams(ScreenSize.Width - PaddingSize.device * 2, 80.uiPX()).apply {
            leftMargin = PaddingSize.device
            topMargin = 20.uiPX()
            setPadding(20.uiPX(), 16.uiPX(), 20.uiPX(), 10.uiPX())
          }

          textSize = 5.uiPX().toFloat()
          textColor = GrayScale.black
          typeface = GoldStoneFont.heavy(context)
          text = "mnemonic split with space test what are you doing now baby"
        }
        .into(this)

      confirmButton
        .apply {
          text = "Confirm".toUpperCase()
          marginTop = 20.uiPX()
          setBlueStyle()
        }
        .click { presenter.goToMnemonicConfirmation() }
        .into(this)

    }
  }

}