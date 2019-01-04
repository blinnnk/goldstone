package io.goldstone.blinnnk.module.home.wallet.walletsettings.walletsettings.view

import android.view.ViewGroup
import io.goldstone.blinnnk.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.module.home.wallet.walletsettings.walletsettings.presenter.WalletSettingsPresenter

/**
 * @date 25/03/2018 6:30 PM
 * @author KaySaith
 */
class WalletSettingsFragment : BaseOverlayFragment<WalletSettingsPresenter>() {

	var header: WalletSettingsHeader? = null
	private val titles by lazy {
		arguments?.getString(ArgumentKey.walletSettingsTitle)
	}
	override val presenter = WalletSettingsPresenter(this)

	override fun ViewGroup.initView() {
		presenter.showTargetFragmentByTitle(titles ?: headerTitle)
	}
}