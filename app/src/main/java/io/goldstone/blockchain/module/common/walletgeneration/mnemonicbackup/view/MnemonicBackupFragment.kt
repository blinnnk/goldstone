package io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AttentionTextView
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.presenter.MnemonicBackupPresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.verticalLayout

/**
 * @date 22/03/2018 9:32 PM
 * @author KaySaith
 */

class MnemonicBackupFragment : BaseFragment<MnemonicBackupPresenter>() {

  private val mnemonicCode by lazy { arguments?.getString(ArgumentKey.mnemonicCode) }
  private val walletAddress by lazy { arguments?.getString(ArgumentKey.walletAddress) }

  private val mnemonic by lazy { TextView(context) }
  private val confirmButton by lazy { RoundButton(context!!) }
  private val attentionTextView by lazy { AttentionTextView(context!!) }

  override val presenter = MnemonicBackupPresenter(this)

  override fun AnkoContext<Fragment>.initView() {
    verticalLayout {

      lparams(matchParent, matchParent)
      attentionTextView
        .apply { text = CreateWalletText.mnemonicBackupAttention }
        .into(this)

      mnemonic
        .apply {
          addCorner(CornerSize.default.toInt(), GrayScale.whiteGray)
          layoutParams = LinearLayout.LayoutParams(ScreenSize.Width - PaddingSize.device * 2,
            92.uiPX()
          ).apply {
            leftMargin = PaddingSize.device
            topMargin = 20.uiPX()
            setPadding(20.uiPX(), 16.uiPX(), 20.uiPX(), 10.uiPX())
          }
          gravity = Gravity.CENTER_VERTICAL
          textSize = 5.uiPX().toFloat()
          textColor = GrayScale.black
          typeface = GoldStoneFont.heavy(context)
          text = mnemonicCode
        }
        .into(this)

      confirmButton
        .apply {
          text = CommonText.confirm.toUpperCase()
          marginTop = 20.uiPX()
          setBlueStyle()
        }
        .click {
          Bundle()
            .apply { putString(ArgumentKey.mnemonicCode, mnemonicCode) }
            .let { presenter.goToMnemonicConfirmation(it) }
        }
        .into(this)
    }
  }

}