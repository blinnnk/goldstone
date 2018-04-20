package io.goldstone.blockchain.module.home.home.view

import android.support.v4.app.Fragment
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.GradientType
import io.goldstone.blockchain.common.component.GradientView
import io.goldstone.blockchain.common.component.TabBarView
import io.goldstone.blockchain.common.component.TabItem
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.module.home.home.presneter.HomePresenter
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.verticalLayout

/**
 * @date 23/03/2018 12:59 PM
 * @author KaySaith
 */

class HomeFragment : BaseFragment<HomePresenter>() {

  private val tabBar by lazy { TabBarView(context!!) }

  override val presenter = HomePresenter(this)

  override fun AnkoContext<Fragment>.initView() {
    relativeLayout {
      lparams(matchParent, matchParent)

      GradientView(context).apply { setStyle(GradientType.Blue) }.into(this)

      verticalLayout {
        id = ContainerID.home
        addFragmentAndSetArgument<WalletDetailFragment>(this.id, FragmentTag.walletDetail) {
          // Send Argument
        }
      }

      tabBar
        .apply {
          walletButton.setStyleAndClick {
            presenter.showWalletDetailFragment()
          }
          marketButton.setStyleAndClick {
            presenter.showQuotationFragment()
          }
          profileButton.setStyleAndClick {
            presenter.showProfileFragment()
          }
        }
        .into(this)
      tabBar.setAlignParentBottom()
    }
  }

  private fun TabItem.setStyleAndClick(callback: () -> Unit) {
    onClick {
      tabBar.apply {
        walletButton.resetStyle()
        marketButton.resetStyle()
        profileButton.resetStyle()
      }
      callback()
      setSelectedStyle()
      preventDuplicateClicks()
    }
  }

}