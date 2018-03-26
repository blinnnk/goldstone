package io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.view

import android.support.v4.app.Fragment
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.component.RoundInput
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.presenter.PasswordSettingsPresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.verticalLayout

/**
 * @date 26/03/2018 9:13 PM
 * @author KaySaith
 */

class PasswordSettingsFragment : BaseFragment<PasswordSettingsPresenter>() {

  val oldPassword by lazy { RoundInput(context!!) }
  val newPassword by lazy { RoundInput(context!!) }
  val repeatPassword by lazy { RoundInput(context!!) }
  val passwordHint by lazy { RoundInput(context!!) }
  val confirmButton by lazy { RoundButton(context!!) }

  override val presenter = PasswordSettingsPresenter(this)

  override fun AnkoContext<Fragment>.initView() {
    
    verticalLayout {

      oldPassword
        .apply {
          text = "Old Password"
          setMargins<LinearLayout.LayoutParams> { topMargin = 40.uiPX() }
        }
        .into(this)

      newPassword
        .apply {
          text = "New Password"
          setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
        }
        .into(this)

      repeatPassword
        .apply {
          text = "Repeat Password"
          setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
        }
        .into(this)

      passwordHint
        .apply {
          text = "Password Hint"
          setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
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