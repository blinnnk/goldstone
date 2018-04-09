package io.goldstone.blockchain.module.common.walletgeneration.view

import android.view.ViewGroup
import com.blinnnk.extension.orZero
import com.blinnnk.util.addFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.utils.getRealScreenHeight
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.module.common.createwallet.view.CreateWalletFragment

/**
 * @date 22/03/2018 9:37 PM
 * @author KaySaith
 */

class WalletGenerationFragment : BaseOverlayFragment<WalletGenerationPresenter>() {

  override val presenter = WalletGenerationPresenter(this)
  override fun setContentHeight() = activity?.getRealScreenHeight().orZero()

  override fun ViewGroup.initView() {
    addFragment<CreateWalletFragment>(this.id, FragmentTag.walletCreation)
    headerTitle = CreateWalletText.create
  }

}