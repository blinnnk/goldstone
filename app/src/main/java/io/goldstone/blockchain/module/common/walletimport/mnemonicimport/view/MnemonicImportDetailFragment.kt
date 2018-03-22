package io.goldstone.blockchain.module.common.walletimport.mnemonicimport.view

import android.graphics.Color
import android.support.v4.app.Fragment
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.module.common.walletimport.mnemonicimport.presenter.MnemonicImportDetailPresenter
import org.jetbrains.anko.*

/**
 * @date 23/03/2018 1:46 AM
 * @author KaySaith
 */

class MnemonicImportDetailFragment : BaseFragment<MnemonicImportDetailPresenter>() {
  override val presenter = MnemonicImportDetailPresenter(this)

  override fun AnkoContext<Fragment>.initView() {
    verticalLayout {
      lparams(matchParent, matchParent)
      textView("mnemonic") {
        textColor = Color.BLUE
        textSize = 18.uiPX().toFloat()
        y = 100.uiPX().toFloat()
        x = 20.uiPX().toFloat()
      }
    }
  }

}