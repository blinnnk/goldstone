package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.presenter

import android.view.View
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.replaceFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.component.UnlimitedAvatar
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.common.passcode.view.PasscodeFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.view.MnemonicBackupFragment
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.view.WalletListCardCell
import io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.view.AddressManagerFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.hint.view.HintFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.view.PasswordSettingsFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletnameeditor.view.WalletNameEditorFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsHeader
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.view.WalletSettingsListFragment

/**
 * @date 25/03/2018 6:30 PM
 * @author KaySaith
 * @rewriteDate 26/07/2018 3:30 PM
 * @reWriter wcx
 * @description 修改获取头像方法 UnlimitedAvatar
 */
class WalletSettingsPresenter(
	override val fragment: WalletSettingsFragment
) : BaseOverlayPresenter<WalletSettingsFragment>() {

	override fun onFragmentViewCreated() {
		showCurrentWalletInfo()
	}

	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		// 页面销毁的时候更新钱包首页, 刷新余额以及更新钱包地址的可能
		fragment.getMainActivity()?.getWalletDetailFragment()?.presenter?.updateData()
	}

	fun showTargetFragmentByTitle(title: String) {
		when (title) {
			WalletSettingsText.passwordSettings -> showPasswordSettingsFragment()
			WalletSettingsText.walletName -> showWalletNameEditorFragment()
			WalletSettingsText.viewAddresses -> showAllMyAddressesFragment()
			WalletSettingsText.hint -> showHintEditorFragment()
			WalletSettingsText.walletSettings -> showWalletSettingListFragment()
			WalletSettingsText.backUpMnemonic -> showMnemonicBackUpFragment()
		}
	}

	fun showWalletSettingListFragment() {
		setCustomHeader()
		fragment.replaceFragmentAndSetArgument<WalletSettingsListFragment>(ContainerID.content)
	}

	private fun setCustomHeader() {
		fragment.apply {
			customHeader = {
				layoutParams.height = 160.uiPX()
				if (header.isNull()) {
					header = WalletSettingsHeader(context)
					addView(header)
				} else {
					overlayView.header.apply {
						showCloseButton(true) {
							presenter.removeSelfFromActivity()
						}
						showAddButton(false) {}
					}
					header?.visibility = View.VISIBLE
				}
			}
		}
	}

	private fun showHintEditorFragment() {
		fragment.apply {
			// 判断是否是只读钱包
			if (!SharedWallet.isWatchOnlyWallet()) {
				// 恢复 `Header` 样式
				recoveryHeaderStyle()
				// 属于私密修改行为, 判断是否开启了 `Pin Code` 验证
				AppConfigTable.getAppConfig {
					it?.apply {
						// 如果有私密验证首先要通过 `Pin Code`
						showPincode.isTrue {
							activity?.addFragmentAndSetArguments<PasscodeFragment>(ContainerID.main)
						}
						// 加载 `Hint` 编辑界面
						replaceFragmentAndSetArgument<HintFragment>(ContainerID.content)
					}
				}
			} else context.alert(WalletText.watchOnly)
		}
	}

	private fun showMnemonicBackUpFragment() {
		fragment.apply {
			if (!SharedWallet.isWatchOnlyWallet()) {
				WalletTable.getCurrentWallet {
					encryptMnemonic?.let {
						recoveryHeaderStyle()
						val mnemonicCode = JavaKeystoreUtil()
							.decryptData(it)
						replaceFragmentAndSetArgument<MnemonicBackupFragment>(ContainerID.content) {
							putString(ArgumentKey.mnemonicCode, mnemonicCode)
						}
					}
				}
			} else context.alert(WalletText.watchOnly)
		}
	}

	private fun showAllMyAddressesFragment() {
		fragment.apply {
			recoveryHeaderStyle()
			replaceFragmentAndSetArgument<AddressManagerFragment>(ContainerID.content)
		}
	}

	private fun showWalletNameEditorFragment() {
		fragment.apply {
			recoveryHeaderStyle()
			replaceFragmentAndSetArgument<WalletNameEditorFragment>(ContainerID.content)
		}
	}

	private fun showPasswordSettingsFragment() {
		fragment.apply {
			if (!SharedWallet.isWatchOnlyWallet()) {
				recoveryHeaderStyle()
				replaceFragmentAndSetArgument<PasswordSettingsFragment>(ContainerID.content)
			} else context.alert(WalletText.watchOnly)
		}
	}

	private fun WalletSettingsFragment.recoveryHeaderStyle() {
		recoveryOverlayHeader()
		header?.visibility = View.GONE
		overlayView.apply {
			header.showBackButton(true) {
				showWalletSettingListFragment()
			}
			header.showCloseButton(false) {}
		}
	}

	fun showCurrentWalletInfo() {
		fragment.header?.apply {
			walletInfo.apply {
				title.text = WalletListCardCell.getFixedTitleLength(SharedWallet.getCurrentName())
				WalletTable.getWalletAddressCount { count ->
					val description = if (count == 1) "" else WalletSettingsText.containsBTCTest
					subtitle.text = WalletSettingsText.addressCountSubtitle(count, description)
					isCenter = false
				}
			}
			avatarImage.glideImage(
				UnlimitedAvatar(SharedWallet.getCurrentWalletID(), context).getBitmap()
			)
		}
	}
}