package io.goldstone.blockchain.module.home.dapp.dappbrowser.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blinnnk.extension.alignParentRight
import com.blinnnk.extension.removeFragment
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.gsfragment.GSFragment
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.home.dapp.common.DAppBrowser
import io.goldstone.blockchain.module.home.dapp.dappbrowser.contract.DAppBrowserContract
import io.goldstone.blockchain.module.home.dapp.dappbrowser.presenter.DAppBrowserPresenter
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.button
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI


/**
 * @author KaySaith
 * @date  2018/11/29
 */
class DAppBrowserFragment : GSFragment(), DAppBrowserContract.GSView {
	override val pageTitle: String = "DApp Browser"
	override lateinit var presenter: GoldStonePresenter
	private lateinit var browser: DAppBrowser

	override fun showError(error: Throwable) {
		safeShowError(error)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter = DAppBrowserPresenter()
		presenter.start()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
			relativeLayout {
				lparams(matchParent, matchParent)
				browser = DAppBrowser(context)
				addView(browser)
				button {
					x += 200.uiPX()
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
}