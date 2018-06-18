package io.goldstone.blockchain.module.common.webview.view

import android.R
import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.text.Html
import android.view.Gravity
import android.view.ViewGroup
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
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.common.webview.presenter.WebViewPresenter
import org.jetbrains.anko.*

@Suppress("DEPRECATION")
/**
 * @date 26/03/2018 8:11 PM
 * @author KaySaith
 */
class WebViewFragment : BaseFragment<WebViewPresenter>() {
	
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
	
	@SuppressLint("SetJavaScriptEnabled")
	private fun ViewGroup.showWebView() {
		loading.apply {
			indeterminateDrawable.setColorFilter(
				HoneyColor.Red,
				android.graphics.PorterDuff.Mode.MULTIPLY
			)
			layoutParams = RelativeLayout.LayoutParams(80.uiPX(), 80.uiPX())
			setCenterInParent()
			y -= 30.uiPX()
		}.into(this)
		// 当 `webView`加载完毕后清楚 `loading`
		webView = webView {
			alpha = 0.1f
			settings.javaScriptEnabled = true
			webViewClient = WebViewClient()
			this.loadUrl(urlPath)
			layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
			
			webViewClient = object : WebViewClient() {
				override fun onPageFinished(view: WebView, url: String) {
					alpha = 1f
					removeView(loading)
				}
			}
		}
		// 如果长时间没加载到 最长 `8s` 超时删除 `loading`
		8000L timeUpThen {
			context?.apply {
				webView.alpha = 1f
				removeView(loading)
			}
		}
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
						AppConfigTable.getAppConfig {
							it?.apply {
								setText(Html.fromHtml(terms), TextView.BufferType.SPANNABLE)
							}
						}
					}
				}
			}
		}
	}
}