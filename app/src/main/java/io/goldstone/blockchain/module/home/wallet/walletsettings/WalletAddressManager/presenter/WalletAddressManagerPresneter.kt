package io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.presenter

import android.os.Bundle
import com.blinnnk.extension.getParentFragment
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.common.value.WalletText
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.getEthereumWalletByMnemonic
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
import io.goldstone.blockchain.crypto.verifyKeystorePassword
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
		getEthereumClassicAddresses()
		getBitcoinAddresses()
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
				fragment.setEthereumAddressesModel(convertToChildAddresses(ethAddresses))
			}
		}
	}
	
	private fun getEthereumClassicAddresses() {
		WalletTable.getCurrentWallet {
			it?.apply {
				fragment.setEthereumClassicAddressesModel(convertToChildAddresses(etcAddresses))
			}
		}
	}
	
	private fun getBitcoinAddresses() {
		WalletTable.getCurrentWallet {
			it?.apply {
				fragment.setBitcoinAddressesModel(convertToChildAddresses(btcAddresses))
			}
		}
	}
	
	fun getCellDashboardMenu(isBTC: Boolean = false): List<Pair<Int, String>> {
		return if (isBTC) {
			listOf(
				Pair(R.drawable.default_icon, WalletText.setDefaultAddress),
				Pair(R.drawable.qr_code_icon, WalletText.showQRCode),
				Pair(R.drawable.private_key_icon, WalletSettingsText.exportPrivateKey)
			)
		} else {
			listOf(
				Pair(R.drawable.default_icon, WalletText.setDefaultAddress),
				Pair(R.drawable.qr_code_icon, WalletText.showQRCode),
				Pair(R.drawable.keystore_icon, WalletSettingsText.exportKeystore),
				Pair(R.drawable.private_key_icon, WalletSettingsText.exportPrivateKey)
			)
		}
	}
	
	fun getAddressCreatorMenu(): List<Pair<Int, String>> {
		return listOf(
			Pair(R.drawable.eth_creator_icon, WalletSettingsText.newETHAndERCAddress),
			Pair(R.drawable.etc_creator_icon, WalletSettingsText.newETCAddress),
			Pair(R.drawable.btc_creator_icon, WalletSettingsText.newBTCAddress)
		)
	}
	
	fun showPrivateKeyExportFragment(address: String) {
		WalletTable.isWatchOnlyWalletShowAlertOrElse(fragment.context!!) {
			fragment.creatorDashBoard?.removeSelf()
			showTargetFragment<PrivateKeyExportFragment, WalletSettingsFragment>(
				WalletSettingsText.exportPrivateKey,
				WalletSettingsText.viewAddresses,
				Bundle().apply { putString(ArgumentKey.address, address) }
			)
		}
	}
	
	fun showBTCPrivateKeyExportFragment(address: String) {
		WalletTable.isWatchOnlyWalletShowAlertOrElse(fragment.context!!) {
			fragment.creatorDashBoard?.removeSelf()
			showTargetFragment<PrivateKeyExportFragment, WalletSettingsFragment>(
				WalletSettingsText.exportPrivateKey,
				WalletSettingsText.viewAddresses,
				Bundle().apply {
					putString(ArgumentKey.address, address)
					putBoolean(ArgumentKey.isBTCAddress, true)
				}
			)
		}
	}
	
	fun showKeystoreExportFragment(address: String) {
		WalletTable.isWatchOnlyWalletShowAlertOrElse(fragment.context!!) {
			fragment.creatorDashBoard?.removeSelf()
			showTargetFragment<KeystoreExportFragment, WalletSettingsFragment>(
				WalletSettingsText.exportKeystore,
				WalletSettingsText.viewAddresses,
				Bundle().apply { putString(ArgumentKey.address, address) }
			)
		}
	}
	
	fun showQRCodeFragment(address: String) {
		fragment.creatorDashBoard?.removeSelf()
		showTargetFragment<QRCodeFragment, WalletSettingsFragment>(
			WalletText.showQRCode,
			WalletSettingsText.viewAddresses,
			Bundle().apply { putString(ArgumentKey.address, address) }
		)
	}
	
	fun createNewETHAndERCChildAddress(password: String, hold: (List<String>) -> Unit) {
		WalletTable.getETHAndERCWalletLatestChildAddressIndex() { wallet, childAddressIndex ->
			wallet.encryptMnemonic?.let {
				val mnemonic = JavaKeystoreUtil().decryptData(it)
				val newAddressIndex = childAddressIndex + 1
				val newChildPath = wallet.ethPath.substringBeforeLast("/") + "/" + newAddressIndex
				fragment.context?.getEthereumWalletByMnemonic(mnemonic, newChildPath, password) {
					WalletTable.updateETHAndERCAddresses(it, newAddressIndex) {
						hold(convertToChildAddresses(it))
					}
				}
			}
		}
	}
	
	fun createNewETCChildAddress(password: String, hold: (List<String>) -> Unit) {
		WalletTable.getETCWalletLatestChildAddressIndex() { wallet, childAddressIndex ->
			wallet.encryptMnemonic?.let {
				val mnemonic = JavaKeystoreUtil().decryptData(it)
				val newAddressIndex = childAddressIndex + 1
				val newChildPath = wallet.etcPath.substringBeforeLast("/") + "/" + newAddressIndex
				fragment.context?.getEthereumWalletByMnemonic(mnemonic, newChildPath, password) {
					WalletTable.updateETCAddresses(it, newAddressIndex) {
						hold(convertToChildAddresses(it))
					}
				}
			}
		}
	}
	
	fun createNewBTCChildAddress(password: String, hold: (List<String>) -> Unit) {
		fragment.context?.verifyKeystorePassword(password) {
			if (it) {
				WalletTable.getBTCWalletLatestChildAddressIndex { wallet, childAddressIndex ->
					wallet.encryptMnemonic?.let {
						val mnemonic = JavaKeystoreUtil().decryptData(it)
						val newAddressIndex = childAddressIndex + 1
						val newChildPath = wallet.btcPath.substringBeforeLast("/") + "/" + newAddressIndex
						BTCUtils.getBitcoinWalletByMnemonic(mnemonic, newChildPath) { address, _ ->
							WalletTable.updateBTCAddresses(address, newAddressIndex) {
								hold(convertToChildAddresses(it))
							}
						}
					}
				}
			} else {
				fragment.context.alert(CommonText.wrongPassword)
			}
		}
	}
	
	fun createNewBTCTestChildAddress(password: String, hold: (List<String>) -> Unit) {
		fragment.context?.verifyKeystorePassword(password) {
			if (it) {
				WalletTable.getBTCTestWalletLatestChildAddressIndex { wallet, childAddressIndex ->
					wallet.encryptMnemonic?.let {
						val mnemonic = JavaKeystoreUtil().decryptData(it)
						val newAddressIndex = childAddressIndex + 1
						val newChildPath = wallet.btcTestPath.substringBeforeLast("/") + "/" + newAddressIndex
						BTCUtils.getBitcoinWalletByMnemonic(mnemonic, newChildPath) { address, _ ->
							WalletTable.updateBTCTestAddresses(address, newAddressIndex) {
								hold(convertToChildAddresses(it))
							}
						}
					}
				}
			} else {
				fragment.context.alert(CommonText.wrongPassword)
			}
		}
	}
	
	private fun convertToChildAddresses(seriesAddress: String): List<String> {
		return if (seriesAddress.contains(",")) {
			seriesAddress.split(",").map { it.substringBeforeLast("|") }
		} else {
			listOf(seriesAddress.substringBeforeLast("|"))
		}
	}
}