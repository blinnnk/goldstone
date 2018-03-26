package io.goldstone.blockchain.module.home.wallet.walletsettings.walletnameeditor.view

import android.support.v4.app.Fragment
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletnameeditor.presenter.WalletNameEditorPresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.verticalLayout

/**
 * @date 26/03/2018 10:44 PM
 * @author KaySaith
 */

class WalletNameEditorFragment : BaseFragment<WalletNameEditorPresenter>() {

  private val nameInput by lazy { RoundInput(context!!) }
  private val confirmButton by lazy { RoundButton(context!!) }

  override val presenter = WalletNameEditorPresenter(this)

  override fun AnkoContext<Fragment>.initView() {
    verticalLayout {
      nameInput
        .apply {
          text = WalletSettingsText.walletNameSettings
          setMargins<LinearLayout.LayoutParams> { topMargin = 40.uiPX() }
        }
        .into(this)

      confirmButton
        .apply {
          text = CommonText.confirm
          setGrayStyle()
          setMargins<LinearLayout.LayoutParams> { topMargin = 15.uiPX() }
        }
        .into(this)
    }
  }

}