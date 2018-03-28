package io.goldstone.blockchain.module.common.walletgeneration.agreementfragment.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.webkit.WebViewClient
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.module.common.walletgeneration.agreementfragment.presenter.AgreementPresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.webView

/**
 * @date 28/03/2018 4:06 PM
 * @author KaySaith
 */

class AgreementFragment : BaseFragment<AgreementPresenter>() {

  override val presenter = AgreementPresenter(this)

  @SuppressLint("SetJavaScriptEnabled")
  override fun AnkoContext<Fragment>.initView() {
    webView {
      settings.javaScriptEnabled = true
      webViewClient = WebViewClient()
      this.loadUrl("https://eos.io/")
      layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
    }
  }

}