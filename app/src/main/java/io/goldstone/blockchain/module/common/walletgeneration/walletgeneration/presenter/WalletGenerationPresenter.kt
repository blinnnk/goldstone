package io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.presenter

import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.Language.CreateWalletText
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.view.CreateWalletFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment

/**
 * @date 22/03/2018 9:38 PM
 * @author KaySaith
 */
class WalletGenerationPresenter(
	override val fragment: WalletGenerationFragment
) : BaseOverlayPresenter<WalletGenerationFragment>() {
	
	fun showCreateWalletFragment() {
		fragment.apply {
			addFragmentAndSetArgument<CreateWalletFragment>(
				ContainerID.content,
				FragmentTag.walletCreation
			) {
				// Send Argument
			}
			headerTitle = CreateWalletText.create
		}
	}
}