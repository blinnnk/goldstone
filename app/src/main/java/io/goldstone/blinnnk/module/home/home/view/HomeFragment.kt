package io.goldstone.blinnnk.module.home.home.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import com.blinnnk.extension.alignParentBottom
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blinnnk.common.base.basefragment.BaseFragment
import io.goldstone.blinnnk.common.component.TabBarView
import io.goldstone.blinnnk.common.component.TabItem
import io.goldstone.blinnnk.common.value.ContainerID
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.UMengEvent
import io.goldstone.blinnnk.kernel.receiver.VersionManager
import io.goldstone.blinnnk.module.home.home.presneter.HomePresenter
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 23/03/2018 12:59 PM
 * @author KaySaith
 */
class HomeFragment : BaseFragment<HomePresenter>() {

	override val pageTitle: String = "Home"
	private lateinit var tabBar: TabBarView
	override val presenter = HomePresenter(this)
	private val versionManager = VersionManager(this)

	override fun AnkoContext<Fragment>.initView() {
		relativeLayout {
			lparams(matchParent, matchParent)
			backgroundColor = Spectrum.backgroundBlue
			verticalLayout {
				lparams(matchParent, matchParent)
				gravity = Gravity.CENTER_HORIZONTAL
				id = ContainerID.home
			}

			tabBar = TabBarView(context)
			tabBar.apply {
				walletButton.onClick {
					presenter.showWalletDetailFragment()
					walletButton.preventDuplicateClicks()
				}
				dAppCenterButton.onClick {
					UMengEvent.add(context, UMengEvent.Click.dappCenter)
					presenter.showDAPPCenterFragment()
					dAppCenterButton.preventDuplicateClicks()
				}
				marketButton.onClick {
					presenter.showQuotationFragment()
					preventDuplicateClicks()
				}
				settingsButton.onClick {
					presenter.showProfileFragment()
					preventDuplicateClicks()
				}
			}.into(this)

			tabBar.alignParentBottom()
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter.showWalletDetailFragment()
		versionManager.checkVersion { error ->
			if (error.isNone()) {
				versionManager.showUpgradeDialog()
			}
		}
	}

	fun showUpgradeDialog() {
		versionManager.showUpgradeDialog()
	}

	override fun onDetach() {
		super.onDetach()
		versionManager.removeHandler()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		versionManager.onActivityResult(requestCode, resultCode)
	}

	private fun TabItem.setStyleAndClick(callback: () -> Unit) {
		tabBar.apply {
			walletButton.resetStyle()
			dAppCenterButton.resetStyle()
			marketButton.resetStyle()
			settingsButton.resetStyle()
		}
		callback()
		setSelectedStyle()
	}

	fun selectWalletDetail(callback: () -> Unit) {
		tabBar.walletButton.setStyleAndClick(callback)
	}

	fun selectDAppCenter(callback: () -> Unit) {
		tabBar.dAppCenterButton.setStyleAndClick(callback)
	}

	fun selectQuotation(callback: () -> Unit) {
		tabBar.marketButton.setStyleAndClick(callback)
	}

	fun setProfile(callback: () -> Unit) {
		tabBar.settingsButton.setStyleAndClick(callback)
	}

	fun hideTabBarView() {
		tabBar.alpha = 0f
	}

	fun showTabBarView() {
		tabBar.alpha = 1f
	}
}