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
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.home.dapp.common.DAPPBrowser
import io.goldstone.blockchain.module.home.dapp.dappbrowser.contract.DAppBrowserContract
import io.goldstone.blockchain.module.home.dapp.dappbrowser.presenter.DAppBrowserPresenter
import io.goldstone.blockchain.module.home.home.view.MainActivity
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
		arguments?.getString("webURL")
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
		activity?.transparentStatus()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
			relativeLayout {
				progressView = horizontalProgressBar {
					layoutParams = RelativeLayout.LayoutParams(matchParent, wrapContent)
					z = 1f
				}
				lparams(matchParent, matchParent)
				browser = DAPPBrowser(context, url.orEmpty()) {
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

	private fun removeSelfFromActivity() {
		getMainActivity()?.removeFragment(this)
		getMainActivity()?.showHomeFragment()
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		browser.backEvent {
			activity?.removeFragment(this)
			activity?.showHomeFragment()
		}
	}
}