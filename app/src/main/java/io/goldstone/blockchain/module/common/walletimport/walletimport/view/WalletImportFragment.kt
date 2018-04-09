package io.goldstone.blockchain.module.common.walletimport.walletimport.view

import android.view.ViewGroup
import com.blinnnk.extension.getRealScreenHeight
import com.blinnnk.extension.into
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.component.MenuBar
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.module.common.walletimport.walletimport.presenter.WalletImportPresenter

/**
 * @date 23/03/2018 12:54 AM
 * @author KaySaith
 */

class WalletImportFragment : BaseOverlayFragment<WalletImportPresenter>() {

  val menuBar by lazy { MenuBar(context!!) }
  private val viewPager by lazy { WalletImportViewPager(this) }

  override val presenter = WalletImportPresenter(this)

  override fun setContentHeight() = activity?.getRealScreenHeight().orZero()

  override fun ViewGroup.initView() {

    addView(viewPager)
    menuBar.into(this)

    headerTitle = ImportWalletText.importWallet
  }

}