package io.goldstone.blinnnk.module.common.walletimport.walletimport.view

import android.content.Context
import android.view.ViewGroup
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blinnnk.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blinnnk.common.language.ImportWalletText
import io.goldstone.blinnnk.common.utils.getMainActivity
import io.goldstone.blinnnk.common.value.ContainerID
import io.goldstone.blinnnk.common.value.FragmentTag
import io.goldstone.blinnnk.module.common.walletgeneration.walletgeneration.view.recoveryOverlayFragment
import io.goldstone.blinnnk.module.common.walletimport.walletimport.presenter.WalletImportPresenter
import io.goldstone.blinnnk.module.common.walletimport.walletimportcenter.view.WalletImportCenterFragment
import io.goldstone.blinnnk.module.home.home.view.MainActivity

/**
 * @date 23/03/2018 12:54 AM
 * @author KaySaith
 */
class WalletImportFragment : BaseOverlayFragment<WalletImportPresenter>() {

	private var mainActivity: MainActivity? = null

	override val presenter = WalletImportPresenter(this)

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		mainActivity = getMainActivity()
	}

	override fun ViewGroup.initView() {
		headerTitle = ImportWalletText.importWallet
		addFragmentAndSetArgument<WalletImportCenterFragment>(ContainerID.content)
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