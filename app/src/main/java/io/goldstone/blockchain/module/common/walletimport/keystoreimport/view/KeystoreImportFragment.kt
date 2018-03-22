package io.goldstone.blockchain.module.common.walletimport.keystoreimport.view

import android.graphics.Color
import android.support.v4.app.Fragment
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.module.common.walletimport.keystoreimport.presenter.KeystoreImportPresenter
import org.jetbrains.anko.*

/**
 * @date 23/03/2018 1:49 AM
 * @author KaySaith
 */

class KeystoreImportFragment : BaseFragment<KeystoreImportPresenter>() {

  override val presenter = KeystoreImportPresenter(this)

  override fun AnkoContext<Fragment>.initView() {
    verticalLayout {
      lparams(matchParent, matchParent)
      textView("keystore") {
        textColor = Color.BLUE
        textSize = 18.uiPX().toFloat()
        y = 100.uiPX().toFloat()
        x = 20.uiPX().toFloat()
      }
    }
  }

}