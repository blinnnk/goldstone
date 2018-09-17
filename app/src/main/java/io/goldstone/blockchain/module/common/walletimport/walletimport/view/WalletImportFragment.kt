package io.goldstone.blockchain.module.common.walletimport.walletimport.view

import android.view.ViewGroup
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.common.walletimport.walletimport.presenter.WalletImportPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimportcenter.view.WalletImportCenterFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity

/**
 * @date 23/03/2018 12:54 AM
 * @author KaySaith
 */
class WalletImportFragment : BaseOverlayFragment<WalletImportPresenter>() {
	override val presenter = WalletImportPresenter(this)
	override fun ViewGroup.initView() {
		headerTitle = ImportWalletText.importWallet
		addFragmentAndSetArgument<WalletImportCenterFragment>(ContainerID.content)
	}

	override fun setTransparentStatus() {
		if (activity !is MainActivity) {
			super.setTransparentStatus()
		}
	}
}