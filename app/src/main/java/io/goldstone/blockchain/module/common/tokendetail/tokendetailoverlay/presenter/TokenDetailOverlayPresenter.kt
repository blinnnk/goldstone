package io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.presenter

import android.support.v4.app.Fragment
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.isFalse
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.language.EOSAccountText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.view.EOSAccountSelectionFragment
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.activationmode.view.EOSActivationModeFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailcenter.view.TokenDetailCenterFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.addressselection.view.AddressSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.deposit.view.DepositFragment
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

	fun showEOSActivationModeFragment(token: WalletDetailCellModel?) {
		fragment.apply {
			headerTitle = EOSAccountText.activationMethod
			addFragmentAndSetArgument<EOSActivationModeFragment>(ContainerID.content) {
				putSerializable(ArgumentKey.tokenDetail, token)
			}
		}
	}

	fun showEOSAccountSelectionFragment(token: WalletDetailCellModel?) {
		fragment.apply {
			addFragmentAndSetArgument<EOSAccountSelectionFragment>(ContainerID.content) {
				putSerializable(ArgumentKey.tokenDetail, token)
			}
		}
	}

	fun showAddressSelectionFragment(isFromQuickTransfer: Boolean = false) {
		if (SharedWallet.isWatchOnlyWallet()) fragment.safeShowError(Throwable(AlertText.watchOnly))
		else {
			if (!SharedWallet.hasBackUpMnemonic())
				GoldStoneDialog(fragment.context!!).showBackUpMnemonicStatus {
					TokenDetailOverlayPresenter.showMnemonicBackupFragment(fragment)
				} else {
				if (isFromQuickTransfer) {
					fragment.addFragmentAndSetArgument<AddressSelectionFragment>(ContainerID.content)
				} else showTargetFragment<AddressSelectionFragment>()
			}
		}
	}

	fun showDepositFragment(isFromQuickTransfer: Boolean = false) {
		if (SharedWallet.isWatchOnlyWallet()) fragment.safeShowError(Throwable(AlertText.watchOnly))
		else {
			if (!SharedWallet.hasBackUpMnemonic())
				GoldStoneDialog(fragment.context!!).showBackUpMnemonicStatus {
					TokenDetailOverlayPresenter.showMnemonicBackupFragment(fragment)
				} else {
				if (isFromQuickTransfer)
					fragment.addFragmentAndSetArgument<DepositFragment>(ContainerID.content)
				else showTargetFragment<DepositFragment>()
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