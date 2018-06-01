package io.goldstone.blockchain.module.common.webview.view

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.blinnnk.extension.getRealScreenHeight
import com.blinnnk.extension.into
import com.blinnnk.extension.orZero
import com.blinnnk.extension.timeUpThen
import com.blinnnk.uikit.HoneyColor
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.common.webview.presenter.WebViewPresenter
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
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
			layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
			backgroundColor = Spectrum.white
			loading.apply {
				indeterminateDrawable.setColorFilter(
					HoneyColor.Red, android.graphics.PorterDuff.Mode.MULTIPLY
				)
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
						alpha = 1f
						removeView(loading)
					}
				}
			}
			// 如果长时间没加载到 最长 `8s` 超时删除 `loading`
			8000L timeUpThen {
				context?.apply {
					removeView(loading)
				}
			}
		}
		presenter.updateHeight(context?.getRealScreenHeight().orZero())
	}
	
	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(view, savedInstanceState)
		val currentActivity = activity
		when (currentActivity) {
			is SplashActivity -> {
				currentActivity.backEvent = Runnable {
					setBackEvent()
				}
			}
			
			is MainActivity -> {
				currentActivity.backEvent = Runnable {
					setBackEvent()
				}
			}
		}
	}
	
	private fun setBackEvent() {
		val parent = parentFragment
		when (parent) {
			is TransactionFragment -> {
				parent.headerTitle = TransactionText.detail
				parent.presenter.popFragmentFrom<WebViewFragment>()
			}
			
			is NotificationFragment -> {
				parent.headerTitle = NotificationText.notification
				parent.presenter.popFragmentFrom<WebViewFragment>()
			}
			
			is TokenDetailOverlayFragment -> {
				parent.headerTitle = TokenDetailText.tokenDetail
				parent.presenter.popFragmentFrom<WebViewFragment>()
			}
			
			is WalletGenerationFragment -> {
				parent.headerTitle = CreateWalletText.create
				parent.presenter.popFragmentFrom<WebViewFragment>()
			}
			
			is ProfileOverlayFragment -> {
				parent.presenter.removeSelfFromActivity()
			}
		}
	}
}