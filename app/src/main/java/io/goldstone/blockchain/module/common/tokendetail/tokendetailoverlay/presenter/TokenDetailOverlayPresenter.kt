package io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.presenter

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.view.View
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
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
	
	fun showTokenDetailFragment(token: WalletDetailCellModel?) {
		fragment.apply {
			addFragmentAndSetArgument<TokenDetailFragment>(ContainerID.content) {
				putSerializable(ArgumentKey.tokenDetail, token)
			}
		}
	}
	
	@SuppressLint("SetTextI18n")
	fun setValueHeader(token: WalletDetailCellModel?) {
		fragment.apply {
			overlayView.header.title.isHidden()
			valueHeader.isNull() isTrue {
				customHeader = {
					valueHeader = TwoLineTitles(context)
					valueHeader?.apply {
						title.text = "${WalletText.tokenDetailHeaderText} ${token?.symbol}"
						subtitle.text =
							CryptoUtils.scaleTo28(
								"${token?.count} ${token?.symbol} ≈ ${token?.currency?.formatCurrency()} " +
								"(${Config.getCurrencyCode
								()})"
							)
						setBlackTitles()
						isCenter = true
					}?.into(this)
					valueHeader?.apply {
						setCenterInHorizontal()
						y += 15.uiPX()
					}
				}
			} otherwise {
				valueHeader?.visibility = View.VISIBLE
			}
		}
	}
	
	fun recoverHeader() {
		fragment.apply {
			overlayView.header.title.visibility = View.VISIBLE
			valueHeader?.visibility = View.GONE
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
					setValueHeader(token)
					addFragmentAndSetArgument<AddressSelectionFragment>(ContainerID.content) {}
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
					setValueHeader(token)
					addFragmentAndSetArgument<DepositFragment>(ContainerID.content) {}
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