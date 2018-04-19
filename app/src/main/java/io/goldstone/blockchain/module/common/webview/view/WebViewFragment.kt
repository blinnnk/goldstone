package io.goldstone.blockchain.module.common.webview.view

import android.R
import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.blinnnk.animation.updateHeightAnimation
import com.blinnnk.extension.getRealScreenHeight
import com.blinnnk.extension.into
import com.blinnnk.extension.orZero
import com.blinnnk.extension.timeUpThen
import com.blinnnk.uikit.HoneyColor
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.common.webview.presenter.WebViewPresenter
import org.jetbrains.anko.*

/**
 * @date 26/03/2018 8:11 PM
 * @author KaySaith
 */

class WebViewFragment : BaseFragment<WebViewPresenter>() {

  private val urlPath by lazy { arguments?.getString(ArgumentKey.webViewUrl) }
  private val loading by lazy { ProgressBar(this.context, null, R.attr.progressBarStyleInverse) }
  override val presenter = WebViewPresenter(this)

  @SuppressLint("SetJavaScriptEnabled")
  override fun AnkoContext<Fragment>.initView() {
    relativeLayout {

      loading.apply {
        indeterminateDrawable.setColorFilter(HoneyColor.Red, android.graphics.PorterDuff.Mode.MULTIPLY)
        lparams {
          width = 80.uiPX()
          height = 80.uiPX()
          centerInParent()
          y -= 30.uiPX()
        }
      }.into(this)

      // 当 `webView`加载完毕后清楚 `loading`
      webView {
        alpha = 0.1f
        settings.javaScriptEnabled = true
        webViewClient = WebViewClient()
        this.loadUrl(urlPath)
        layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)

        webViewClient = object : WebViewClient() {
          override fun onPageFinished(view: WebView, url: String) {
            removeView(loading)
            view.alpha = 1f
          }
        }
      }

      // 如果长时间没加载到 最长 `8s` 超时删除 `loading`
      8000L timeUpThen {
        context?.apply { removeView(loading)  }
      }

    }

    // 需要添加到 `BaseOverlayFragment` 下面
    getParentFragment<BaseOverlayFragment<*>>()?.apply {
      overlayView.contentLayout.updateHeightAnimation(activity?.getRealScreenHeight().orZero())
    }
  }

}