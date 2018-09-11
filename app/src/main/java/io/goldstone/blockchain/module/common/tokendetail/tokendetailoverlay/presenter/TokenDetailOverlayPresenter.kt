package io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.presenter

import android.support.v4.app.Fragment
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.isFalse
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailcenter.view.TokenDetailCenterFragment
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.view.AddressSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.deposit.view.DepositFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.findIsItExist
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment

/**ø
 * @date 27/03/2018 3:41 PM
 * @author KaySaith
 */
class TokenDetailOverlayPresenter(
	override val fragment: TokenDetailOverlayFragment
) : BaseOverlayPresenter<TokenDetailOverlayFragment>() {

	fun showTokenDetailCenterFragment(token: WalletDetailCellModel?) {
		fragment.apply {
			addFragmentAndSetArgument<TokenDetailCenterFragment>(ContainerID.content) {
				putSerializable(ArgumentKey.tokenDetail, token)
			}
		}
	}

	fun showAddressSelectionFragment(isFromQuickTransfer: Boolean = false) {
		WalletTable.checkIsWatchOnlyAndHasBackupOrElse(
			fragment.context!!,
			{
				// Click Dialog Confirm Button Event
				TokenDetailOverlayPresenter.showMnemonicBackupFragment(fragment)
			}
		) {
			if (isFromQuickTransfer) {
				fragment.apply {
					addFragmentAndSetArgument<AddressSelectionFragment>(ContainerID.content)
					headerTitle = TokenDetailText.address
				}
			} else {
				showTargetFragment<AddressSelectionFragment>(
					TokenDetailText.address,
					TokenDetailText.tokenDetail
				)
			}
		}
	}

	fun showDepositFragment(isFromQuickTransfer: Boolean = false) {
		WalletTable.checkIsWatchOnlyAndHasBackupOrElse(
			fragment.context!!,
			{
				// Click Dialog Confirm Button Event
				TokenDetailOverlayPresenter.showMnemonicBackupFragment(fragment)
			}
		) {
			if (isFromQuickTransfer) {
				fragment.apply {
					addFragmentAndSetArgument<DepositFragment>(ContainerID.content)
					headerTitle = TokenDetailText.deposit
				}
			} else {
				showTargetFragment<DepositFragment>(
					TokenDetailText.deposit,
					TokenDetailText.tokenDetail
				)
			}
		}
	}

	companion object {
		fun showMnemonicBackupFragment(fragment: Fragment) {
			if (fragment is TokenDetailOverlayFragment) {
				fragment.presenter.removeSelfFromActivity()
			}
			fragment.activity?.apply {
				findIsItExist(FragmentTag.walletSettings) isFalse {
					addFragmentAndSetArguments<WalletSettingsFragment>(
						ContainerID.main, FragmentTag.walletSettings
					) {
						putString(
							ArgumentKey.walletSettingsTitle, WalletSettingsText.walletSettings
						)
					}
				}
			}
		}
	}
}