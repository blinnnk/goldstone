package io.goldstone.blockchain.module.common.webview.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.blinnnk.animation.updateHeightAnimation
import com.blinnnk.extension.getRealScreenHeight
import com.blinnnk.extension.orZero
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.common.webview.presenter.WebViewPresenter
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.webView

/**
 * @date 26/03/2018 8:11 PM
 * @author KaySaith
 */

class WebViewFragment : BaseFragment<WebViewPresenter>() {

  private val urlPath by lazy { arguments?.getString(ArgumentKey.webViewUrl) }

  override val presenter = WebViewPresenter(this)

  @SuppressLint("SetJavaScriptEnabled")
  override fun AnkoContext<Fragment>.initView() {
    webView {
      settings.javaScriptEnabled = true
      webViewClient = WebViewClient()
      this.loadUrl(urlPath)
      layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
    }

    // 需要添加到 `BaseOverlayFragment` 下面
    getParentFragment<BaseOverlayFragment<*>>()?.apply {
      overlayView.contentLayout.updateHeightAnimation(activity?.getRealScreenHeight().orZero())
    }
  }

}