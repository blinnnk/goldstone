package io.goldstone.blinnnk.module.home.wallet.walletsettings.walletsettings.presenter

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.isNull
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blinnnk.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blinnnk.common.language.WalletSettingsText
import io.goldstone.blinnnk.common.language.WalletText
import io.goldstone.blinnnk.common.sharedpreference.SharedValue
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.*
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.common.value.ContainerID
import io.goldstone.blinnnk.crypto.utils.JavaKeystoreUtil
import io.goldstone.blinnnk.crypto.utils.KeystoreInfo
import io.goldstone.blinnnk.module.common.passcode.view.PasscodeFragment
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blinnnk.module.common.walletgeneration.mnemonicbackup.view.MnemonicBackupFragment
import io.goldstone.blinnnk.module.home.wallet.walletmanagement.walletlist.view.WalletListCardCell
import io.goldstone.blinnnk.module.home.wallet.walletsettings.addressmanager.view.AddressManagerFragment
import io.goldstone.blinnnk.module.home.wallet.walletsettings.hint.view.HintFragment
import io.goldstone.blinnnk.module.home.wallet.walletsettings.passwordsettings.view.PasswordSettingsFragment
import io.goldstone.blinnnk.module.home.wallet.walletsettings.walletnameeditor.view.WalletNameEditorFragment
import io.goldstone.blinnnk.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import io.goldstone.blinnnk.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsHeader
import io.goldstone.blinnnk.module.home.wallet.walletsettings.walletsettingslist.view.WalletSettingsListFragment
import kotlinx.coroutines.Dispatchers

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

	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		// 页面销毁的时候更新钱包首页, 刷新余额以及更新钱包地址的可能
		fragment.getMainActivity()?.getWalletDetailFragment()?.presenter?.start()
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

	private fun showWalletSettingListFragment() {
		fragment.addFragmentAndSetArgument<WalletSettingsListFragment>(ContainerID.content)
	}

	fun setCustomHeader() {
		fragment.apply {
			customHeader = {
				layoutParams.height = 160.uiPX()
				if (header.isNull()) {
					header = WalletSettingsHeader(context)
					addView(header)
				} else {
					showCloseButton(true) {
						presenter.removeSelfFromActivity()
					}
					showAddButton(false) {}
					header?.visibility = View.VISIBLE
				}
			}
			showCurrentWalletInfo()
		}
	}

	private fun showHintEditorFragment() {
		// 判断是否是只读钱包
		if (!SharedWallet.isWatchOnlyWallet()) {
			// 恢复 `Header` 样式
			fragment.recoveryHeaderStyle()
			// 属于私密修改行为, 判断是否开启了 `Pin Code` 验证
			// 如果有私密验证首先要通过 `Pin Code`
			if (SharedValue.getPincodeDisplayStatus())
				fragment.activity?.addFragmentAndSetArguments<PasscodeFragment>(ContainerID.main)
			// 加载 `Hint` 编辑界面
			showTargetFragment<HintFragment>()
		} else fragment.context.alert(WalletText.watchOnly)
	}

	private fun showMnemonicBackUpFragment() {
		fragment.apply {
			if (!SharedWallet.isWatchOnlyWallet()) {
				if (!SharedWallet.hasBackUpMnemonic()) WalletTable.getCurrent(Dispatchers.Main) {
					encryptMnemonic?.let {
						recoveryHeaderStyle()
						val mnemonicCode = JavaKeystoreUtil(KeystoreInfo.isMnemonic()).decryptData(it)
						val data = Bundle().apply {
							putString(ArgumentKey.mnemonicCode, mnemonicCode)
						}
						showTargetFragment<MnemonicBackupFragment>(data)
					}
				}
			} else context.alert(WalletText.watchOnly)
		}
	}

	private fun showAllMyAddressesFragment() {
		fragment.apply {
			recoveryHeaderStyle()
			showTargetFragment<AddressManagerFragment>()
		}
	}

	private fun showWalletNameEditorFragment() {
		fragment.apply {
			recoveryHeaderStyle()
			showTargetFragment<WalletNameEditorFragment>()
		}
	}

	private fun showPasswordSettingsFragment() {
		fragment.apply {
			if (!SharedWallet.isWatchOnlyWallet()) {
				recoveryHeaderStyle()
				showTargetFragment<PasswordSettingsFragment>()
			} else context.alert(WalletText.watchOnly)
		}
	}

	private fun WalletSettingsFragment.recoveryHeaderStyle() {
		recoveryOverlayHeader()
		header?.visibility = View.GONE
		showBackButton(true) {
			showWalletSettingListFragment()
		}
		showCloseButton(false) {}
	}

	fun showCurrentWalletInfo() {
		fragment.header?.apply {
			walletInfo.apply {
				title.text = WalletListCardCell.getFixedTitleLength(SharedWallet.getCurrentName())
				WalletTable.getWalletAddressCount { count ->
					launchUI {
						val description = if (count == 1) "" else WalletSettingsText.containsBTCTest
						subtitle.text = WalletSettingsText.addressCountSubtitle(count, description)
						isCenter = false
					}
				}
			}
			avatarImage.glideImage(
				AvatarManager.getAvatarPath(SharedWallet.getCurrentWalletID())
			)
		}
	}
}