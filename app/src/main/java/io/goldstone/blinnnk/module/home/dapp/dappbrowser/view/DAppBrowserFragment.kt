package io.goldstone.blinnnk.module.home.dapp.dappbrowser.view

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.blinnnk.extension.hideStatusBar
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.removeFragment
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.base.gsfragment.GSFragment
import io.goldstone.blinnnk.common.component.dragbutton.DragButtonModel
import io.goldstone.blinnnk.common.component.dragbutton.DragFloatingLayout
import io.goldstone.blinnnk.common.utils.getMainActivity
import io.goldstone.blinnnk.common.utils.isEmptyThen
import io.goldstone.blinnnk.common.utils.safeShowError
import io.goldstone.blinnnk.common.utils.transparentStatus
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.module.common.contract.GoldStonePresenter
import io.goldstone.blinnnk.module.home.dapp.common.DAPPBrowser
import io.goldstone.blinnnk.module.home.dapp.dappbrowser.contract.DAppBrowserContract
import io.goldstone.blinnnk.module.home.dapp.dappbrowser.presenter.DAppBrowserPresenter
import io.goldstone.blinnnk.module.home.dapp.dapplist.event.DAPPListDisplayEvent
import io.goldstone.blinnnk.module.home.dapp.dappoverlay.event.DAPPExplorerDisplayEvent
import io.goldstone.blinnnk.module.home.dapp.dappoverlay.view.DAPPOverlayFragment
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI


/**
 * @author KaySaith
 * @date  2018/11/29
 */
class DAppBrowserFragment : GSFragment(), DAppBrowserContract.GSView {

	override val pageTitle: String = "DApp Browser"
	override lateinit var presenter: GoldStonePresenter
	private lateinit var browser: DAPPBrowser
	private lateinit var floatingButton: DragFloatingLayout
	private lateinit var progressView: ProgressBar

	private val url by lazy {
		arguments?.getString(ArgumentKey.webViewUrl)
	}

	private val webColor by lazy {
		var color = arguments?.getString(ArgumentKey.webColor)?.isEmptyThen("FFFFFF") ?: "FFFFFF"
		color = if (color.contains("#")) color.substringAfter("#") else color
		Color.parseColor("#FF$color")
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
				lparams(matchParent, matchParent)
				backgroundColor = webColor
				floatingButton = DragFloatingLayout(context)
				floatingButton.addSubButton(
					DragButtonModel(
						R.drawable.refresh_icon,
						event = { browser.refresh() },
						color = Spectrum.green
					),
					DragButtonModel(
						R.drawable.close_web_icon,
						event = { removeSelfFromActivity() },
						color = Spectrum.lightRed
					)
				)
				addView(floatingButton)
				progressView = horizontalProgressBar {
					layoutParams = RelativeLayout.LayoutParams(matchParent, wrapContent)
					z = 1f
				}
				browser = DAPPBrowser(context, url.orEmpty()) {
					progressView.progress = it
					if (it == 100) {
						removeView(progressView)
						browser.alpha = 1f
					}
				}
				browser.alpha = 0f
				addView(browser)
			}
		}.view
	}

	private fun removeSelfFromActivity() {
		getMainActivity()?.removeFragment(this)
		getMainActivity()?.showHomeFragment()
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		if (browser.canGoBack()) browser.goBack()
		else browser.backEvent {
			activity?.removeFragment(this)
			activity?.supportFragmentManager?.apply {
				val dappOverlayFragment = fragments.find { it is DAPPOverlayFragment }
				if (dappOverlayFragment.isNotNull()) {
					beginTransaction().show(dappOverlayFragment).commitNow()
				} else activity.showHomeFragment()
			}
		}
	}
}

object PreviousView {
	const val DAPPList = 0
	const val DAPPExplorer = 1
	const val DAPPCenter = 2
}