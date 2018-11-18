package io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view

import android.content.Context
import android.view.ViewGroup
import com.blinnnk.extension.isNotNull
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.presenter.WalletGenerationPresenter
import io.goldstone.blockchain.module.home.home.view.MainActivity

/**
 * @date 22/03/2018 9:37 PM
 * @author KaySaith
 */
class WalletGenerationFragment : BaseOverlayFragment<WalletGenerationPresenter>() {

	private var mainActivity: MainActivity? = null
	override val presenter = WalletGenerationPresenter(this)

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		mainActivity = getMainActivity()
	}

	override fun ViewGroup.initView() {
		presenter.showCreateWalletFragment()
	}

	override fun setTransparentStatus() {
		if (activity !is MainActivity) {
			super.setTransparentStatus()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		mainActivity?.recoveryOverlayFragment(FragmentTag.profileOverlay)
	}
}

fun MainActivity.recoveryOverlayFragment(tag: String) {
	supportFragmentManager?.apply {
		val overlayFragment = findFragmentByTag(tag)
		if (overlayFragment.isNotNull()) {
			beginTransaction().show(overlayFragment).commit()
		}
	}
}