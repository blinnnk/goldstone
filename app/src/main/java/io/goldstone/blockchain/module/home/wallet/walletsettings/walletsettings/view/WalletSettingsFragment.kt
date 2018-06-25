package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view

import android.view.ViewGroup
import android.widget.RelativeLayout
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.RoundBorderButton
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.BorderSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.WalletText
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.presenter.WalletSettingsPresenter
import org.jetbrains.anko.wrapContent

/**
 * @date 25/03/2018 6:30 PM
 * @author KaySaith
 */

class WalletSettingsFragment : BaseOverlayFragment<WalletSettingsPresenter>() {

  var header: WalletSettingsHeader? = null
  var manageButton: RoundBorderButton? = null

  private val titles by lazy { arguments?.getString(ArgumentKey.walletSettingsTitle) }

  override val presenter = WalletSettingsPresenter(this)

  override fun ViewGroup.initView() {

    presenter.showTargetFragmentByTitle(titles ?: headerTitle)

  }
  
  fun generateManageButton(): RoundBorderButton {
    return RoundBorderButton(context!!).apply {
      y = 15.uiPX().toFloat()
      x = 10.uiPX().toFloat()
      layoutParams = RelativeLayout.LayoutParams(wrapContent, 24.uiPX())
      text = WalletText.manage
      themeColor = Spectrum.white
      setBorderWidth(BorderSize.bold)
      setAdjustWidth()
    }
  }

}