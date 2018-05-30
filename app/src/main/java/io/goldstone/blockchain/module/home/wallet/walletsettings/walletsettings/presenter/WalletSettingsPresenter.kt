package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.presenter

import android.view.View
import com.blinnnk.animation.updateHeightAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.replaceFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.crypto.JavaKeystoreUtil
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.common.passcode.view.PasscodeFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.view.MnemonicBackupFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.hint.view.HintFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.view.KeystoreExportFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.view.PasswordSettingsFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view.PrivateKeyExportFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view.QRCodeFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletnameeditor.view.WalletNameEditorFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsHeader
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.view.WalletSettingsListFragment

/**
 * @date 25/03/2018 6:30 PM
 * @author KaySaith
 */

class WalletSettingsPresenter(
	override val fragment: WalletSettingsFragment
) : BaseOverlayPresenter<WalletSettingsFragment>() {

	override fun onFragmentViewCreated() {
		showCurrentWalletInfo()
	}

	fun showTargetFragmentByTitle(title: String) {
		when (title) {
			WalletSettingsText.passwordSettings -> showPasswordSettingsFragment()
			WalletSettingsText.walletName -> showWalletNameEditorFragment()
			WalletSettingsText.exportPrivateKey -> showPrivateKeyExportFragment()
			WalletSettingsText.exportKeystore -> showKeystoreExportFragment()
			WalletSettingsText.checkQRCode -> showQRCodeFragment()
			WalletSettingsText.hint -> showHintEditorFragment()
			WalletSettingsText.walletSettings -> showWalletSettingListFragment()
			WalletSettingsText.backUpMnemonic -> showMnemonicBackUpFragment()
		}
	}

	fun showWalletSettingListFragment() {
		fragment.apply {
			customHeader = {
				layoutParams.height = 200.uiPX()
				header.isNull() isTrue {
					header = WalletSettingsHeader(context)
					addView(header)
				} otherwise {
					overlayView.header.apply {
						showBackButton(false)
						showCloseButton(true)
					}
					header?.visibility = View.VISIBLE
				}
			}
			replaceFragmentAndSetArgument<WalletSettingsListFragment>(ContainerID.content) {
				// Send Arguments
			}
		}
	}

	private fun showHintEditorFragment() {
		fragment.apply {
			// 判断是否是只读钱包
			WalletTable.isWatchOnlyWalletShowAlertOrElse(context!!) {
				// 恢复 `Header` 样式
				setNormalHeaderWithHeight(context?.getRealScreenHeight().orZero())
				// 属于私密修改行为, 判断是否开启了 `Pin Code` 验证
				AppConfigTable.getAppConfig {
					it?.apply {
						// 如果有私密验证首先要通过 `Pin Code`
						showPincode.isTrue {
							activity?.addFragmentAndSetArguments<PasscodeFragment>(ContainerID.main) {
								// Send Argument
							}
						}
						// 加载 `Hint` 编辑界面
						replaceFragmentAndSetArgument<HintFragment>(ContainerID.content) {
							// Send Arguments
						}
					}
				}
			}
		}
	}

	private fun showMnemonicBackUpFragment() {
		fragment.apply {
			WalletTable.isWatchOnlyWalletShowAlertOrElse(context!!) {
				WalletTable.getCurrentWallet {
					it?.apply {
						encryptMnemonic?.let {
							setNormalHeaderWithHeight(context?.getRealScreenHeight().orZero())
							val mnemonicCode = JavaKeystoreUtil().decryptData(it)
							replaceFragmentAndSetArgument<MnemonicBackupFragment>(ContainerID.content) {
								putString(ArgumentKey.mnemonicCode, mnemonicCode)
							}
						}
					}
				}
			}
		}
	}

	private fun showPrivateKeyExportFragment() {
		fragment.apply {
			WalletTable.isWatchOnlyWalletShowAlertOrElse(context!!) {
				setNormalHeaderWithHeight(context?.getRealScreenHeight().orZero())
				replaceFragmentAndSetArgument<PrivateKeyExportFragment>(ContainerID.content) {
					// Send Arguments
				}
			}
		}
	}

	private fun showKeystoreExportFragment() {
		fragment.apply {
			WalletTable.isWatchOnlyWalletShowAlertOrElse(context!!) {
				setNormalHeaderWithHeight(context?.getRealScreenHeight().orZero())
				replaceFragmentAndSetArgument<KeystoreExportFragment>(ContainerID.content) {
					// Send Arguments
				}
			}
		}
	}

	private fun showQRCodeFragment() {
		fragment.apply {
			setNormalHeaderWithHeight(fragment.context?.getRealScreenHeight().orZero())
			replaceFragmentAndSetArgument<QRCodeFragment>(ContainerID.content) {
				// Send Arguments
			}
		}
	}

	private fun showWalletNameEditorFragment() {
		fragment.apply {
			setNormalHeaderWithHeight(300.uiPX())
			replaceFragmentAndSetArgument<WalletNameEditorFragment>(ContainerID.content) {
				// Send Arguments
			}
		}
	}

	private fun showPasswordSettingsFragment() {
		fragment.apply {
			WalletTable.isWatchOnlyWalletShowAlertOrElse(context!!) {
				setNormalHeaderWithHeight(420.uiPX())
				replaceFragmentAndSetArgument<PasswordSettingsFragment>(ContainerID.content) {
					// Send Arguments
				}
			}
		}
	}

	private fun WalletSettingsFragment.setNormalHeaderWithHeight(contentHeight: Int) {
		recoveryOverlayHeader()
		header?.visibility = View.GONE
		overlayView.apply {
			header.showBackButton(true) { showWalletSettingListFragment() }
			header.showCloseButton(false)
			contentLayout.updateHeightAnimation(contentHeight) {
				if (contentHeight >= ScreenSize.Height) {
					fragment.getMainActivity()?.hideHomeFragment()
				} else {
					fragment.getMainActivity()?.showHomeFragment()
				}
			}
		}
	}

	private fun showCurrentWalletInfo() {
		fragment.header?.apply {
			walletInfo.apply {
				title.text = WalletTable.current.name
				subtitle.text = WalletTable.current.address
			}
			avatarImage.glideImage(UIUtils.generateAvatar(WalletTable.current.id))
		}
	}

}