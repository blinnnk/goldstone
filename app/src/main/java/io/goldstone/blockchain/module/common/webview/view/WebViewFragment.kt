package io.goldstone.blockchain.module.common.webview.view

import android.R
import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.text.Html
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.HoneyColor
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.language.SplashText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.kernel.commontable.AppConfigTable
import io.goldstone.blockchain.module.common.webview.presenter.WebViewPresenter
import io.goldstone.blockchain.module.home.home.view.MainActivity
import kotlinx.coroutines.Dispatchers
import org.jetbrains.anko.*

@Suppress("DEPRECATION")
/**
 * @date 26/03/2018 8:11 PM
 * @author KaySaith
 */
class WebViewFragment : BaseFragment<WebViewPresenter>() {

	override val pageTitle: String get() = arguments?.getString(ArgumentKey.webViewName).orEmpty()
	private val urlPath by lazy { arguments?.getString(ArgumentKey.webViewUrl) }
	private val loading by lazy {
		ProgressBar(this.context, null, R.attr.progressBarStyleInverse)
	}
	private lateinit var webView: WebView
	override val presenter = WebViewPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		relativeLayout {
			layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
			backgroundColor = Spectrum.white

			NetworkUtil.hasNetworkWithAlert(context) isTrue {
				showWebView()
			} otherwise {
				showLocalContent()
			}
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		presenter.prepareBackEvent {
			super.setBaseBackEvent(activity, parent)
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	private fun ViewGroup.showWebView() {
		loading.apply {
			indeterminateDrawable.setColorFilter(
				HoneyColor.Red,
				android.graphics.PorterDuff.Mode.SRC_ATOP
			)
			layoutParams = RelativeLayout.LayoutParams(80.uiPX(), 80.uiPX())
			centerInParent()
			y -= 30.uiPX()
		}.into(this)
		// 当 `webView`加载完毕后清除 `loading`
		webView = WebView(context).apply {
			alpha = 0.1f
			webViewClient = WebViewClient()
			settings.javaScriptEnabled = true
			settings.javaScriptCanOpenWindowsAutomatically = true
			settings.domStorageEnabled = true
			settings.databaseEnabled = true
			settings.allowFileAccess = true
			settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
			settings.useWideViewPort = true
			setLayerType(View.LAYER_TYPE_HARDWARE, null)
			settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
			settings.setAppCacheEnabled(true)
			settings.setAppCacheMaxSize(15 * 1024 * 1024)
			settings.cacheMode = WebSettings.LOAD_DEFAULT
			this.loadUrl(urlPath)
			layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)

			webChromeClient = object : WebChromeClient() {
				override fun onProgressChanged(view: WebView?, newProgress: Int) {
					super.onProgressChanged(view, newProgress)
					if (newProgress == 100) {
						alpha = 1f
						removeView(loading)
					}
				}
			}
		}
		webView.into(this)
	}

	private fun ViewGroup.showLocalContent() {
		if (urlPath.equals(WebUrl.terms, true)) {
			scrollView {
				verticalLayout {
					setPadding(PaddingSize.device, 0, PaddingSize.device, 0)
					linearLayout {
						lparams {
							width = matchParent
							height = 100.uiPX()
							topMargin = 30.uiPX()
						}
						imageView(io.goldstone.blockchain.R.drawable.goldstone_coin_icon) {
						}.lparams {
							width = 36.uiPX()
							height = 36.uiPX()
							topMargin = 7.uiPX()
							rightMargin = 10.uiPX()
						}
						textView(SplashText.goldStone) {
							textColor = GrayScale.black
							textSize = fontSize(20)
							typeface = GoldStoneFont.heavy(context)
							layoutParams = LinearLayout.LayoutParams(wrapContent, 50.uiPX())
							gravity = Gravity.CENTER_VERTICAL
						}
					}
					textView {
						AppConfigTable.getAppConfig(Dispatchers.Main) {
							val terms = it?.terms ?: return@getAppConfig
							setText(Html.fromHtml(terms), TextView.BufferType.SPANNABLE)
						}
					}
				}
			}
		}
	}
}