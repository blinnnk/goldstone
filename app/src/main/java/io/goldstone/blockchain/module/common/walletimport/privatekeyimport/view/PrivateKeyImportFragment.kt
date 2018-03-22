package io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view

import android.graphics.Color
import android.support.v4.app.Fragment
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter.PrivateKeyImportPresenter
import org.jetbrains.anko.*

/**
 * @date 23/03/2018 2:13 AM
 * @author KaySaith
 */

class PrivateKeyImportFragment : BaseFragment<PrivateKeyImportPresenter>() {

  override val presenter = PrivateKeyImportPresenter(this)

  override fun AnkoContext<Fragment>.initView() {
    verticalLayout {
      lparams(matchParent, matchParent)
      textView("private key") {
        textColor = Color.CYAN
        textSize = 18.uiPX().toFloat()
        y = 100.uiPX().toFloat()
        x = 20.uiPX().toFloat()
      }
    }
  }


}