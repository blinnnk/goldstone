package io.goldstone.blockchain.module.home.profile.aboutus.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.blinnnk.animation.updateHeightAnimation
import com.blinnnk.extension.getRealScreenHeight
import com.blinnnk.extension.orZero
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.module.home.profile.aboutus.presenter.AboutUsPresenter
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.webView

/**
 * @date 26/03/2018 8:11 PM
 * @author KaySaith
 */

class AboutUsFragment : BaseFragment<AboutUsPresenter>() {

  override val presenter = AboutUsPresenter(this)

  @SuppressLint("SetJavaScriptEnabled")
  override fun AnkoContext<Fragment>.initView() {
    webView {
      settings.javaScriptEnabled = true
      webViewClient = WebViewClient()
      this.loadUrl("https://eos.io/")
      layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
    }

    getParentFragment<ProfileOverlayFragment>()?.apply {
      overlayView.contentLayout.updateHeightAnimation(activity?.getRealScreenHeight().orZero())
    }
  }

}