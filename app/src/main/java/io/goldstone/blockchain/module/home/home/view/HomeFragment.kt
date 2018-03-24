package io.goldstone.blockchain.module.home.home.view

import android.support.v4.app.Fragment
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.GradientView
import io.goldstone.blockchain.common.component.TabBarView
import com.blinnnk.extension.into
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.GradientType
import io.goldstone.blockchain.module.home.home.presneter.HomePresenter
import io.goldstone.blockchain.module.home.wallet.wallet.view.WalletDetailFragment
import org.jetbrains.anko.*

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
        addFragmentAndSetArgument<WalletDetailFragment>(this.id) {
          // Send Argument
        }
      }

      tabBar
        .apply {
          lparams {
            width = matchParent
            height = 80.uiPX()
            alignParentBottom()
          }
        }
        .into(this)
    }
  }

}