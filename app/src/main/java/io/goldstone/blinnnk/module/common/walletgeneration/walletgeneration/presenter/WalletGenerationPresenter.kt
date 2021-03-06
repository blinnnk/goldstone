package io.goldstone.blinnnk.module.common.walletgeneration.walletgeneration.presenter

import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blinnnk.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blinnnk.common.language.CreateWalletText
import io.goldstone.blinnnk.common.value.ContainerID
import io.goldstone.blinnnk.common.value.FragmentTag
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.view.CreateWalletFragment
import io.goldstone.blinnnk.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment

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