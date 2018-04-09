package io.goldstone.blockchain.module.home.wallet.currentwalletdetail.view

import android.view.ViewGroup
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.CurrentWalletText
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.wallet.currentwalletdetail.presneter.CurrentWalletDetailPresenter
import io.goldstone.blockchain.module.home.wallet.walletlist.view.WalletListFragment

/**
 * @date 24/03/2018 10:10 PM
 * @author KaySaith
 */

class CurrentWalletDetailFragment : BaseOverlayFragment<CurrentWalletDetailPresenter>() {

  val createWalletButton by lazy { RoundButton(context!!) }

  override val presenter = CurrentWalletDetailPresenter(this)

  override fun ViewGroup.initView() {

    headerTitle = CurrentWalletText.Wallets

    addFragmentAndSetArgument<WalletListFragment>(ContainerID.content) {
      // Send Argument
    }

    createWalletButton.apply {
      setSmallButton(Spectrum.green)
      text = CommonText.create.toUpperCase()
    }.click {
      presenter.showCreateWalletFragment()
    }.into(overlayView.header)
    createWalletButton.setCenterInVertical()

  }

}