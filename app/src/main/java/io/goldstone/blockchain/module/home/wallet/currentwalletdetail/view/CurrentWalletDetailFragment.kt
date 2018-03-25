package io.goldstone.blockchain.module.home.wallet.currentwalletdetail.view

import android.view.ViewGroup
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.CurrentWalletText
import io.goldstone.blockchain.module.home.wallet.currentwalletdetail.presneter.CurrentWalletDetailPresenter
import io.goldstone.blockchain.module.home.wallet.walletlist.view.WalletListFragment

/**
 * @date 24/03/2018 10:10 PM
 * @author KaySaith
 */

class CurrentWalletDetailFragment : BaseOverlayFragment<CurrentWalletDetailPresenter>() {

  override val presenter = CurrentWalletDetailPresenter(this)

  override fun ViewGroup.initView() {

    headerTitle = CurrentWalletText.Wallets

    addFragmentAndSetArgument<WalletListFragment>(ContainerID.content) {
      // Send Argument
    }

  }

}