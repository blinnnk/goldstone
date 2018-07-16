package io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.presenter

import com.blinnnk.extension.getParentFragment
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.common.value.WalletText
import io.goldstone.blockchain.crypto.getEthereumWalletByMnemonic
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.view.KeystoreExportFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view.PrivateKeyExportFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view.QRCodeFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.view.WalletAddressManagerFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment

/**
 * @date 2018/7/11 12:44 AM
 * @author KaySaith
 */
class WalletAddressManagerPresneter(
	override val fragment: WalletAddressManagerFragment
) : BasePresenter<WalletAddressManagerFragment>() {
	
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		getEthereumAddresses()
	}
	
	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		setBackEvent()
	}
	
	fun setBackEvent() {
		fragment.getParentFragment<WalletSettingsFragment> {
			overlayView.header.apply {
				showBackButton(true) {
					presenter.showWalletSettingListFragment()
				}
				showCloseButton(false)
			}
		}
	}
	
	private fun getEthereumAddresses() {
		WalletTable.getCurrentWallet {
			it?.apply {
				fragment.setEthereumAddressModel(convertToChildAddresses(ethAddresses))
			}
		}
	}
	
	private fun getBitcoinAddresses() {
		WalletTable.getCurrentWallet {
			it?.apply {
				fragment.setEthereumAddressModel(convertToChildAddresses(btcAddresses))
			}
		}
	}
	
	private fun getEthereumClassicAddresses() {
		WalletTable.getCurrentWallet {
			it?.apply {
				fragment.setEthereumAddressModel(convertToChildAddresses(etcAddresses))
			}
		}
	}
	
	fun getCellDashboardMenu(): List<Pair<Int, String>> {
		return listOf(
			Pair(R.drawable.default_icon, WalletText.setDefaultAddress),
			Pair(R.drawable.qr_code_icon, WalletText.showQRCode),
			Pair(R.drawable.keystore_icon, WalletSettingsText.exportKeystore),
			Pair(R.drawable.private_key_icon, WalletSettingsText.exportPrivateKey)
		)
	}
	
	fun getAddressCreatorMenu(): List<Pair<Int, String>> {
		return listOf(
			Pair(R.drawable.eth_creator_icon, WalletSettingsText.newEthereumAddress),
			Pair(R.drawable.btc_creator_icon, WalletSettingsText.newBitcoinAddress)
		)
	}
	
	fun showPrivateKeyExportFragment() {
		WalletTable.isWatchOnlyWalletShowAlertOrElse(fragment.context!!) {
			fragment.creatorDashBoard?.removeSelf()
			showTargetFragment<PrivateKeyExportFragment, WalletSettingsFragment>(
				WalletSettingsText.exportPrivateKey, WalletSettingsText.viewAddresses
			)
		}
	}
	
	fun showKeystoreExportFragment() {
		WalletTable.isWatchOnlyWalletShowAlertOrElse(fragment.context!!) {
			fragment.creatorDashBoard?.removeSelf()
			showTargetFragment<KeystoreExportFragment, WalletSettingsFragment>(
				WalletSettingsText.exportKeystore, WalletSettingsText.viewAddresses
			)
		}
	}
	
	fun showQRCodeFragment() {
		fragment.creatorDashBoard?.removeSelf()
		showTargetFragment<QRCodeFragment, WalletSettingsFragment>(
			WalletText.showQRCode, WalletSettingsText.viewAddresses
		)
	}
	
	fun createNewEthereumChildAddress(password: String, hold: (List<String>) -> Unit) {
		WalletTable.getWalletWithLatestChildAddressIndex { wallet, ethereumChildAddressIndex ->
			wallet.encryptMnemonic?.let {
				val mnemonic = JavaKeystoreUtil().decryptData(it)
				val newAddressIndex = ethereumChildAddressIndex + 1
				val newChildPath = wallet.ethPath.substringBeforeLast("/") + "/" + newAddressIndex
				fragment.context?.getEthereumWalletByMnemonic(mnemonic, newChildPath, password) {
					WalletTable.updateEthereumSeriesAddresses(it, newAddressIndex) {
						hold(convertToChildAddresses(it))
					}
				}
			}
		}
	}
	
	fun createNewBitcoinChildAddress() {
		// TODO
	}
	
	private fun convertToChildAddresses(seriesAddress: String): List<String> {
		return if (seriesAddress.contains(",")) {
			seriesAddress.split(",").map { it.substringBeforeLast("|") }
		} else {
			listOf(seriesAddress.substringBeforeLast("|"))
		}
	}
}