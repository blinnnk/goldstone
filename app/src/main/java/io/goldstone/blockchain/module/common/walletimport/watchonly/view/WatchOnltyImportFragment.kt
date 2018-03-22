package io.goldstone.blockchain.module.common.walletimport.watchonly.view

import android.graphics.Color
import android.support.v4.app.Fragment
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.module.common.walletimport.watchonly.presenter.WatchOnlyImportPresenter
import org.jetbrains.anko.*

/**
 * @date 23/03/2018 2:15 AM
 * @author KaySaith
 */

class WatchOnltyImportFragment : BaseFragment<WatchOnlyImportPresenter>() {

  override val presenter =  WatchOnlyImportPresenter(this)

  override fun AnkoContext<Fragment>.initView() {
    verticalLayout {
      lparams(matchParent, matchParent)
      textView("watch only") {
        textColor = Color.GREEN
        textSize = 18.uiPX().toFloat()
        y = 100.uiPX().toFloat()
        x = 20.uiPX().toFloat()
      }
    }
  }


}