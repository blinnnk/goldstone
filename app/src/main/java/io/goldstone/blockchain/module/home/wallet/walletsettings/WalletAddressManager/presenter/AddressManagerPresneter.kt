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
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.getEthereumWalletByMnemonic
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
import io.goldstone.blockchain.crypto.verifyKeystorePassword
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view.ChainAddressesFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.view.KeystoreExportFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view.PrivateKeyExportFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view.QRCodeFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.view.AddressManagerFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment

/**
 * @date 2018/7/11 12:44 AM
 * @author KaySaith
 */
class AddressManagerPresneter(
	override val fragment: AddressManagerFragment
) : BasePresenter<AddressManagerFragment>() {
	
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
	
	fun getEthereumAddresses() {
		WalletTable.getCurrentWallet {
			it?.apply {
				fragment.setEthereumAddressesModel(convertToChildAddresses(ethAddresses))
			}
		}
	}
	
	fun getEthereumClassicAddresses() {
		WalletTable.getCurrentWallet {
			it?.apply {
				fragment.setEthereumClassicAddressesModel(convertToChildAddresses(etcAddresses))
			}
		}
	}
	
	fun getBitcoinAddresses() {
		WalletTable.getCurrentWallet {
			it?.apply {
				fragment.setBitcoinAddressesModel(convertToChildAddresses(btcAddresses))
			}
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
			AddressManagerFragment.removeDashboard(fragment)
			showTargetFragment<PrivateKeyExportFragment, WalletSettingsFragment>(
				WalletSettingsText.exportPrivateKey,
				WalletSettingsText.viewAddresses,
				Bundle().apply { putString(ArgumentKey.address, address) }
			)
		}
	}
	
	fun showBTCPrivateKeyExportFragment(address: String) {
		WalletTable.isWatchOnlyWalletShowAlertOrElse(fragment.context!!) {
			AddressManagerFragment.removeDashboard(fragment)
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
			AddressManagerFragment.removeDashboard(fragment)
			showTargetFragment<KeystoreExportFragment, WalletSettingsFragment>(
				WalletSettingsText.exportKeystore,
				WalletSettingsText.viewAddresses,
				Bundle().apply { putString(ArgumentKey.address, address) }
			)
		}
	}
	
	fun showQRCodeFragment(address: String) {
		AddressManagerFragment.removeDashboard(fragment)
		showTargetFragment<QRCodeFragment, WalletSettingsFragment>(
			WalletText.showQRCode,
			WalletSettingsText.viewAddresses,
			Bundle().apply { putString(ArgumentKey.address, address) }
		)
	}
	
	fun createNewETHAndERCChildAddress(password: String, hold: (List<Pair<String, String>>) -> Unit) {
		WalletTable.getETHAndERCWalletLatestChildAddressIndex { wallet, childAddressIndex ->
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
	
	fun createNewETCChildAddress(password: String, hold: (List<Pair<String, String>>) -> Unit) {
		WalletTable.getETCWalletLatestChildAddressIndex { wallet, childAddressIndex ->
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
	
	fun createNewBTCChildAddress(password: String, hold: (List<Pair<String, String>>) -> Unit) {
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
	
	fun createNewBTCTestChildAddress(password: String, hold: (List<Pair<String, String>>) -> Unit) {
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
	
	fun showAllETHAndERCAddresses(): Runnable {
		return Runnable {
			showTargetFragment<ChainAddressesFragment, WalletSettingsFragment>(
				WalletSettingsText.allETHAndERCAddresses,
				WalletSettingsText.viewAddresses,
				Bundle().apply { putInt(ArgumentKey.coinType, ChainType.ETH.id) }
			)
		}
	}
	
	fun showAllETCAddresses(): Runnable {
		return Runnable {
			showTargetFragment<ChainAddressesFragment, WalletSettingsFragment>(
				WalletSettingsText.allETCAddresses,
				WalletSettingsText.viewAddresses,
				Bundle().apply { putInt(ArgumentKey.coinType, ChainType.ETC.id) }
			)
		}
	}
	
	fun showAllBTCAddresses(): Runnable {
		return Runnable {
			showTargetFragment<ChainAddressesFragment, WalletSettingsFragment>(
				WalletSettingsText.allBtCAddresses,
				WalletSettingsText.viewAddresses,
				Bundle().apply { putInt(ArgumentKey.coinType, ChainType.BTC.id) }
			)
		}
	}
	
	fun showAllBTCTestAddresses(): Runnable {
		return Runnable {
			showTargetFragment<ChainAddressesFragment, WalletSettingsFragment>(
				WalletSettingsText.allBtCTestAddresses,
				WalletSettingsText.viewAddresses,
				Bundle().apply { putInt(ArgumentKey.coinType, ChainType.BTCTest.id) }
			)
		}
	}
	
	companion object {
		
		fun setDefaultAddress(
			chainType: Int,
			defaultAddress: String,
			callback: () -> Unit
		) {
			WalletTable.updateCurrentAddressByChainType(chainType, defaultAddress, callback)
		}
		
		fun getCellDashboardMenu(
			isBTC: Boolean = false,
			hasDefaultCell: Boolean = true
		): ArrayList<Pair<Int, String>> {
			return arrayListOf(
				Pair(R.drawable.default_icon, WalletText.setDefaultAddress),
				Pair(R.drawable.qr_code_icon, WalletText.showQRCode),
				Pair(R.drawable.keystore_icon, WalletSettingsText.exportKeystore),
				Pair(R.drawable.private_key_icon, WalletSettingsText.exportPrivateKey)
			).apply {
				if (isBTC) remove(find { it.second == WalletSettingsText.exportKeystore })
				if (!hasDefaultCell) remove(find { it.second == WalletText.setDefaultAddress })
			}
		}
		
		fun convertToChildAddresses(seriesAddress: String): List<Pair<String, String>> {
			return if (seriesAddress.contains(",")) {
				seriesAddress.split(",").map {
					Pair(
						it.substringBeforeLast("|"),
						it.substringAfterLast("|")
					)
				}
			} else {
				listOf(
					Pair(
						seriesAddress.substringBeforeLast("|"),
						seriesAddress.substringAfterLast("|")
					)
				)
			}
		}
		
		fun getCurrentAddressIndexByChainType(chainType: Int, hold: (String) -> Unit) {
			fun getTargetAddressIndex(address: String, targetAddress: String): String {
				return if (address.contains(",")) {
					address.split(",").find {
						it.contains(targetAddress)
					}?.substringAfterLast("|").orEmpty()
				} else {
					address.substringAfterLast("|")
				}
			}
			WalletTable.getCurrentWallet {
				it?.apply {
					when (chainType) {
						ChainType.ETH.id -> {
							hold(getTargetAddressIndex(ethAddresses, currentETHAndERCAddress))
						}
						
						ChainType.ETC.id -> {
							hold(getTargetAddressIndex(etcAddresses, currentETCAddress))
						}
						
						ChainType.BTC.id -> {
							hold(getTargetAddressIndex(btcAddresses, currentBTCAddress))
						}
						
						ChainType.BTCTest.id -> {
							hold(getTargetAddressIndex(btcTestAddresses, currentBTCTestAddress))
						}
					}
				}
			}
		}
	}
}