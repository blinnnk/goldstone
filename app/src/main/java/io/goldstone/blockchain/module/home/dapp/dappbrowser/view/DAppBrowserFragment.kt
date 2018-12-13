package io.goldstone.blockchain.module.home.dapp.dappbrowser.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.blinnnk.extension.hideStatusBar
import com.blinnnk.extension.removeFragment
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.gsfragment.GSFragment
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.utils.transparentStatus
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.home.dapp.common.DAPPBrowser
import io.goldstone.blockchain.module.home.dapp.dappbrowser.contract.DAppBrowserContract
import io.goldstone.blockchain.module.home.dapp.dappbrowser.presenter.DAppBrowserPresenter
import io.goldstone.blockchain.module.home.dapp.dapplist.event.DAPPListDisplayEvent
import io.goldstone.blockchain.module.home.dapp.dappoverlay.event.DAPPExplorerDisplayEvent
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI


/**
 * @author KaySaith
 * @date  2018/11/29
 */
class DAppBrowserFragment : GSFragment(), DAppBrowserContract.GSView {

	override val pageTitle: String = "DApp Browser"
	override lateinit var presenter: GoldStonePresenter
	private lateinit var browser: DAPPBrowser
	private lateinit var progressView: ProgressBar

	private val url by lazy {
		arguments?.getString(ArgumentKey.webViewUrl)
	}

	private val fromView by lazy {
		arguments?.getInt(ArgumentKey.fromView)
	}

	override fun showError(error: Throwable) {
		safeShowError(error)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter = DAppBrowserPresenter()
		presenter.start()
		activity?.hideStatusBar()
	}

	override fun onDestroy() {
		super.onDestroy()
		when (fromView) {
			PreviousView.DAPPList -> EventBus.getDefault().post(DAPPListDisplayEvent(true))
			PreviousView.DAPPExplorer -> EventBus.getDefault().post(DAPPExplorerDisplayEvent(true))
			PreviousView.DAPPCenter -> getMainActivity()?.apply {
				getDAPPCenterFragment()?.let {
					showChildFragment(it)
					setBaseBackEvent(this, getHomeFragment())
				}
				transparentStatus()
			}
			else -> activity?.transparentStatus()
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
			relativeLayout {
				progressView = horizontalProgressBar {
					layoutParams = RelativeLayout.LayoutParams(matchParent, wrapContent)
					z = 1f
				}
				lparams(matchParent, matchParent)
				browser = DAPPBrowser(context, formattedURL(url.orEmpty())) {
					progressView.progress = it
					if (it == 100) removeView(progressView)
				}
				addView(browser)
				button {
					x += 100.uiPX()
					y += 100.uiPX()
					text = "Close"
					onClick {
						removeSelfFromActivity()
					}
				}
				backgroundColor = Spectrum.white
			}
		}.view
	}

	private fun formattedURL(url: String): String {
		return if (!url.contains("http", true)) {
			"http://$url"
		} else url
	}

	private fun removeSelfFromActivity() {
		getMainActivity()?.removeFragment(this)
		getMainActivity()?.showHomeFragment()
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		browser.goBack()
		browser.backEvent {
			activity?.removeFragment(this)
			activity?.showHomeFragment()
		}
	}
}

object PreviousView {
	const val DAPPList = 0
	const val DAPPExplorer = 1
	const val DAPPCenter = 2
}