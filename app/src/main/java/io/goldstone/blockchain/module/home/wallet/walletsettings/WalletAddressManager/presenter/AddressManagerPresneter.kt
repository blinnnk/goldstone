package io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.presenter

import android.content.Context
import android.os.Bundle
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ChainID
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WalletType
import io.goldstone.blockchain.crypto.*
import io.goldstone.blockchain.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blockchain.crypto.bitcoin.storeBase58PrivateKey
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.litecoin.ChainPrefix
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
import io.goldstone.blockchain.crypto.litecoin.storeLTCBase58PrivateKey
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.AddressCommissionModel
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.model.ContactModel
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view.ChainAddressesFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.view.KeystoreExportFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view.PrivateKeyExportFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view.QRCodeFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.view.AddressManagerFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.doAsync

/**
 * @date 2018/7/11 12:44 AM
 * @author KaySaith
 */
class AddressManagerPresenter(
	override val fragment: AddressManagerFragment
) : BasePresenter<AddressManagerFragment>() {

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		getMultiChainAddresses()
	}

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		setBackEvent()
		if (Config.getCurrentWalletType().equals(WalletType.MultiChain.content, true)) {
			fragment.showCreatorDashboard()
		}
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

	private fun getMultiChainAddresses() {
		WalletTable.getCurrentWallet {
			val addresses =
				arrayListOf<Pair<String, String>>().apply {
					// 如果是测试环境展示 `BTCSeriesTest Address`. Bip44 规则, 目前多数 `比特币` 系列的测试网是公用的
					if (currentBTCAddress.isNotEmpty() && !Config.isTestEnvironment()) {
						add(Pair(currentBTCAddress, CryptoSymbol.btc()))
					} else if (currentBTCSeriesTestAddress.isNotEmpty() && Config.isTestEnvironment()) {
						add(Pair(currentBTCSeriesTestAddress, CryptoSymbol.btc()))
					}
					// Litecoin Mainnet and Testnet Addresses
					if (currentLTCAddress.isNotEmpty() && !Config.isTestEnvironment()) {
						add(Pair(currentLTCAddress, CryptoSymbol.ltc))
					} else if (currentBTCSeriesTestAddress.isNotEmpty() && Config.isTestEnvironment()) {
						add(Pair(currentBTCSeriesTestAddress, CryptoSymbol.ltc))
					}
					// Bitcoin Cash Mainnet and Testnet Addresses
					if (currentBCHAddress.isNotEmpty() && !Config.isTestEnvironment()) {
						add(Pair(currentBCHAddress, CryptoSymbol.bch))
					} else if (currentBTCSeriesTestAddress.isNotEmpty() && Config.isTestEnvironment()) {
						add(Pair(currentBTCSeriesTestAddress, CryptoSymbol.bch))
					}
					// Ethereum & Ethereum Classic Mainnet and Testnet Addresses
					if (currentETHAndERCAddress.isNotEmpty()) {
						add(Pair(currentETHAndERCAddress, CryptoSymbol.erc))
						add(Pair(currentETHAndERCAddress, CryptoSymbol.eth))
						add(Pair(currentETCAddress, CryptoSymbol.etc))
					}
				}
			fragment.setMultiChainAddresses(addresses)
		}
	}

	fun getEthereumAddresses() {
		WalletTable.getCurrentWallet {
			fragment.setEthereumAddressesModel(convertToChildAddresses(ethAddresses))
		}
	}

	fun getBitcoinCashAddresses() {
		WalletTable.getCurrentWallet {
			fragment.setBitcoinCashAddressesModel(convertToChildAddresses(bchAddresses))
		}
	}

	fun getBitcoinCashTestAddresses() {
		WalletTable.getCurrentWallet {
			fragment.setBitcoinCashAddressesModel(convertToChildAddresses(btcSeriesTestAddresses))
		}
	}

	fun getEthereumClassicAddresses() {
		WalletTable.getCurrentWallet {
			fragment.setEthereumClassicAddressesModel(convertToChildAddresses(etcAddresses))
		}
	}

	fun getBitcoinAddresses() {
		WalletTable.getCurrentWallet {
			fragment.setBitcoinAddressesModel(convertToChildAddresses(btcAddresses))
		}
	}

	fun getBitcoinTestAddresses() {
		WalletTable.getCurrentWallet {
			fragment.setBitcoinAddressesModel(convertToChildAddresses(btcSeriesTestAddresses))
		}
	}

	fun getLitecoinTestAddresses() {
		WalletTable.getCurrentWallet {
			fragment.setLitecoinAddressesModel(convertToChildAddresses(btcSeriesTestAddresses))
		}
	}

	fun getLitecoinAddresses() {
		WalletTable.getCurrentWallet {
			fragment.setLitecoinAddressesModel(convertToChildAddresses(ltcAddresses))
		}
	}

	fun getAddressCreatorMenu(): List<Pair<Int, String>> {
		return listOf(
			Pair(R.drawable.eth_creator_icon, WalletSettingsText.newETHAndERCAddress),
			Pair(R.drawable.etc_creator_icon, WalletSettingsText.newETCAddress),
			Pair(R.drawable.btc_creator_icon, WalletSettingsText.newBTCAddress),
			Pair(R.drawable.ltc_creator_icon, WalletSettingsText.newLTCAddress),
			Pair(R.drawable.bch_creator_icon, WalletSettingsText.newBCHAddress)
		)
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
				WalletSettingsText.allBtcAddresses,
				WalletSettingsText.viewAddresses,
				Bundle().apply { putInt(ArgumentKey.coinType, ChainType.BTC.id) }
			)
		}
	}

	fun showAllLTCAddresses(): Runnable {
		return Runnable {
			showTargetFragment<ChainAddressesFragment, WalletSettingsFragment>(
				WalletSettingsText.allLTCAddresses,
				WalletSettingsText.viewAddresses,
				Bundle().apply { putInt(ArgumentKey.coinType, ChainType.LTC.id) }
			)
		}
	}

	fun showAllBCHAddresses(): Runnable {
		return Runnable {
			showTargetFragment<ChainAddressesFragment, WalletSettingsFragment>(
				WalletSettingsText.allBCHAddresses,
				WalletSettingsText.viewAddresses,
				Bundle().apply { putInt(ArgumentKey.coinType, ChainType.BCH.id) }
			)
		}
	}

	companion object {

		fun showPrivateKeyExportFragment(
			address: String,
			chainType: Int,
			walletSettingsFragment: WalletSettingsFragment
		) {
			walletSettingsFragment.apply {
				WalletTable.isWatchOnlyWalletShowAlertOrElse(context!!) {
					AddressManagerFragment.removeDashboard(context)
					presenter.showTargetFragment<PrivateKeyExportFragment>(
						WalletSettingsText.exportPrivateKey,
						WalletSettingsText.viewAddresses,
						Bundle().apply {
							putString(ArgumentKey.address, address)
							putInt(ArgumentKey.chainType, chainType)
						}
					)
				}
			}

		}

		fun showQRCodeFragment(addressModel: ContactModel, walletSettingsFragment: WalletSettingsFragment) {
			walletSettingsFragment.apply {
				// 这个页面不限时 `Header` 上的加号按钮
				showAddButton(false)
				AddressManagerFragment.removeDashboard(context)
				presenter.showTargetFragment<QRCodeFragment>(
					WalletText.showQRCode,
					WalletSettingsText.viewAddresses,
					Bundle().apply { putSerializable(ArgumentKey.addressModel, addressModel) }
				)
			}
		}

		fun showKeystoreExportFragment(address: String, walltSettingsFragment: WalletSettingsFragment) {
			walltSettingsFragment.apply {
				// 这个页面不限时 `Header` 上的加号按钮
				showAddButton(false)
				WalletTable.isWatchOnlyWalletShowAlertOrElse(context!!) {
					AddressManagerFragment.removeDashboard(context)
					presenter.showTargetFragment<KeystoreExportFragment>(
						WalletSettingsText.exportKeystore,
						WalletSettingsText.viewAddresses,
						Bundle().apply { putString(ArgumentKey.address, address) }
					)
				}
			}
		}

		fun createETHAndERCAddress(
			context: Context,
			password: String,
			hold: (ArrayList<Pair<String, String>>) -> Unit
		) {
			WalletTable.getETHAndERCWalletLatestChildAddressIndex { wallet, childAddressIndex ->
				wallet.encryptMnemonic?.let { seed ->
					val mnemonic = JavaKeystoreUtil().decryptData(seed)
					val newAddressIndex = childAddressIndex + 1
					val newChildPath = wallet.ethPath.substringBeforeLast("/") + "/" + newAddressIndex
					context.getEthereumWalletByMnemonic(mnemonic, newChildPath, password) { address ->
						// 新创建的账号插入所有对应的链的默认 `Token`
						ChainID.getAllEthereumChainID().forEach {
							insertNewAddressToMyToken(
								CryptoSymbol.eth,
								CryptoValue.ethContract,
								address,
								it
							)
						}
						// 注册新增的子地址
						XinGePushReceiver.registerSingleAddress(
							AddressCommissionModel(
								address,
								ChainType.ETH.id,
								1,
								wallet.id
							)
						)
						WalletTable.updateETHAndERCAddresses(address, newAddressIndex) {
							hold(convertToChildAddresses(it).toArrayList())
						}
					}
				}
			}
		}

		fun createETCAddress(
			context: Context,
			password: String,
			hold: (ArrayList<Pair<String, String>>) -> Unit
		) {
			WalletTable.getETCWalletLatestChildAddressIndex { wallet, childAddressIndex ->
				wallet.encryptMnemonic?.let { seed ->
					val mnemonic = JavaKeystoreUtil().decryptData(seed)
					val newAddressIndex = childAddressIndex + 1
					val newChildPath = wallet.etcPath.substringBeforeLast("/") + "/" + newAddressIndex
					context.getEthereumWalletByMnemonic(mnemonic, newChildPath, password) { address ->
						// 新创建的账号插入所有对应的链的默认 `Token`
						// 在 `MyToken` 里面注册新地址, 用于更换 `DefaultAddress` 的时候做准备
						ChainID.getAllETCChainID().forEach {
							insertNewAddressToMyToken(
								CryptoSymbol.etc,
								CryptoValue.etcContract,
								address,
								it
							)
						}
						// 注册新增的子地址
						XinGePushReceiver.registerSingleAddress(
							AddressCommissionModel(
								address,
								ChainType.ETC.id,
								1,
								wallet.id
							)
						)
						WalletTable.updateETCAddresses(address, newAddressIndex) {
							hold(convertToChildAddresses(it).toArrayList())
						}
					}
				}
			}
		}

		fun createBTCAddress(
			context: Context,
			password: String,
			hold: (ArrayList<Pair<String, String>>) -> Unit
		) {
			context.verifyKeystorePassword(
				password,
				Config.getCurrentBTCAddress(),
				true,
				false
			) { isCorrect ->
				if (isCorrect) {
					WalletTable.getBTCWalletLatestChildAddressIndex { wallet, childAddressIndex ->
						wallet.encryptMnemonic?.let { encryptoMnemonic ->
							val mnemonic = JavaKeystoreUtil().decryptData(encryptoMnemonic)
							val newAddressIndex = childAddressIndex + 1
							val newChildPath = wallet.btcPath.substringBeforeLast("/") + "/" + newAddressIndex
							BTCWalletUtils.getBitcoinWalletByMnemonic(
								mnemonic,
								newChildPath
							) { address, secret ->
								// 存入 `KeyStore`
								context.storeBase58PrivateKey(
									secret,
									address,
									password,
									false,
									false
								)
								// 在 `MyToken` 里面注册新地址, 用于更换 `DefaultAddress` 的时候做准备
								insertNewAddressToMyToken(
									CryptoSymbol.btc(),
									CryptoValue.btcContract,
									address,
									ChainID.BTCMain.id
								)
								// 注册新增的子地址
								XinGePushReceiver.registerSingleAddress(
									AddressCommissionModel(
										address,
										ChainType.BTC.id,
										1,
										wallet.id
									)
								)
								WalletTable.updateBTCAddresses(address, newAddressIndex) {
									hold(convertToChildAddresses(it).toArrayList())
								}
							}
						}
					}
				} else {
					context.alert(CommonText.wrongPassword)
				}
			}
		}

		fun createBTCTestAddress(
			context: Context,
			password: String,
			hold: (ArrayList<Pair<String, String>>) -> Unit
		) {
			context.verifyKeystorePassword(
				password,
				Config.getCurrentBTCSeriesTestAddress(),
				true,
				false
			) { isCorrect ->
				if (!isCorrect) {
					context.alert(CommonText.wrongPassword)
					return@verifyKeystorePassword
				}

				WalletTable.getBTCTestWalletLatestChildAddressIndex { wallet, childAddressIndex ->
					wallet.encryptMnemonic?.let { encryptoMnemonic ->
						val mnemonic = JavaKeystoreUtil().decryptData(encryptoMnemonic)
						val newAddressIndex = childAddressIndex + 1
						val newChildPath = wallet.btcTestPath.substringBeforeLast("/") + "/" + newAddressIndex
						BTCWalletUtils.getBitcoinWalletByMnemonic(mnemonic, newChildPath) { address, secret ->
							context.storeBase58PrivateKey(
								secret,
								address,
								password,
								true,
								false
							)
							// 在 `MyToken` 里面注册新地址, 用于更换 `DefaultAddress` 的时候做准备
							// `BTCTest` 是 `BTCSeries` 公用的地址
							insertNewAddressToMyToken(
								CryptoSymbol.btc(),
								CryptoValue.btcContract,
								address,
								ChainID.BTCTest.id
							)
							// 插入 LTC 账号
							insertNewAddressToMyToken(
								CryptoSymbol.ltc,
								CryptoValue.ltcContract,
								address,
								ChainID.LTCTest.id
							)
							// 插入 BCH 账号
							insertNewAddressToMyToken(
								CryptoSymbol.bch,
								CryptoValue.bchContract,
								address,
								ChainID.BCHTest.id
							)
							// 注册新增的子地址
							XinGePushReceiver.registerSingleAddress(
								AddressCommissionModel(
									address,
									ChainType.AllTest.id,
									1,
									wallet.id
								)
							)
							WalletTable.updateBTCTestAddresses(address, newAddressIndex) {
								hold(convertToChildAddresses(it).toArrayList())
							}
						}
					}
				}
			}
		}

		fun createBCHAddress(
			context: Context,
			password: String,
			hold: (ArrayList<Pair<String, String>>) -> Unit
		) {
			context.verifyKeystorePassword(
				password,
				Config.getCurrentBCHAddress(),
				true,
				false
			) { isCorrect ->
				if (!isCorrect) {
					context.alert(CommonText.wrongPassword)
					return@verifyKeystorePassword
				}

				WalletTable.getBCHWalletLatestChildAddressIndex { wallet, childAddressIndex ->
					wallet.encryptMnemonic?.let { encryptoMnemonic ->
						val mnemonic = JavaKeystoreUtil().decryptData(encryptoMnemonic)
						val newAddressIndex = childAddressIndex + 1
						val newChildPath = wallet.bchPath.substringBeforeLast("/") + "/" + newAddressIndex
						BCHWalletUtils.generateBCHKeyPair(mnemonic, newChildPath).let { bchKeyPair ->
							context.storeBase58PrivateKey(
								bchKeyPair.privateKey,
								bchKeyPair.address,
								password,
								false,
								false
							)
							// 在 `MyToken` 里面注册新地址, 用于更换 `DefaultAddress` 的时候做准备
							insertNewAddressToMyToken(
								CryptoSymbol.bch,
								CryptoValue.bchContract,
								bchKeyPair.address,
								ChainID.BCHMain.id
							)
							// 注册新增的子地址
							XinGePushReceiver.registerSingleAddress(
								AddressCommissionModel(
									bchKeyPair.address,
									ChainType.BCH.id,
									1,
									wallet.id
								)
							)
							WalletTable.updateBCHAddresses(bchKeyPair.address, newAddressIndex) {
								hold(convertToChildAddresses(it).toArrayList())
							}
						}
					}
				}
			}
		}

		fun createLTCAddress(
			context: Context,
			password: String,
			hold: (ArrayList<Pair<String, String>>) -> Unit
		) {
			context.verifyKeystorePassword(
				password,
				Config.getCurrentLTCAddress(),
				true,
				false
			) { isCorrect ->
				if (!isCorrect) {
					context.alert(CommonText.wrongPassword)
					return@verifyKeystorePassword
				}

				WalletTable.getLTCWalletLatestChildAddressIndex { wallet, childAddressIndex ->
					wallet.encryptMnemonic?.let { encryptMnemonic ->
						val mnemonic = JavaKeystoreUtil().decryptData(encryptMnemonic)
						val newAddressIndex = childAddressIndex + 1
						val newChildPath = wallet.ltcPath.substringBeforeLast("/") + "/" + newAddressIndex
						LTCWalletUtils.generateBase58Keypair(
							mnemonic,
							newChildPath,
							ChainPrefix.Litecoin,
							true
						).let { ltcKeyPair ->
							context.storeLTCBase58PrivateKey(
								ltcKeyPair.privateKey,
								ltcKeyPair.address,
								password,
								false
							)
							// 在 `MyToken` 里面注册新地址, 用于更换 `DefaultAddress` 的时候做准备
							insertNewAddressToMyToken(
								CryptoSymbol.ltc,
								CryptoValue.ltcContract,
								ltcKeyPair.address,
								ChainID.LTCMain.id
							)
							// 注册新增的子地址
							XinGePushReceiver.registerSingleAddress(
								AddressCommissionModel(
									ltcKeyPair.address,
									ChainType.LTC.id,
									1,
									wallet.id
								)
							)
							WalletTable.updateLTCAddresses(ltcKeyPair.address, newAddressIndex) {
								hold(convertToChildAddresses(it).toArrayList())
							}
						}
					}
				}
			}
		}

		fun setDefaultAddress(
			chainType: Int,
			defaultAddress: String,
			callback: () -> Unit
		) {
			WalletTable.updateCurrentAddressByChainType(chainType, defaultAddress, callback)
		}

		fun getCellDashboardMenu(
			hasDefaultCell: Boolean = true,
			isBCH: Boolean = false
		): List<Pair<Int, String>> {
			return arrayListOf(
				Pair(R.drawable.default_icon, WalletText.setDefaultAddress),
				Pair(R.drawable.qr_code_icon, WalletText.showQRCode),
				Pair(R.drawable.keystore_icon, WalletSettingsText.exportKeystore),
				Pair(R.drawable.private_key_icon, WalletSettingsText.exportPrivateKey),
				Pair(R.drawable.bch_address_convert_icon, WalletText.getBCHLegacyAddress)
			).apply {
				// 如果当前 `Cell` 就是默认地址, 不限时设置默认地址的选项
				if (!hasDefaultCell) remove(find { it.second == WalletText.setDefaultAddress })
				// 如果当前 `Cell` 不是 `BCH` 那么不限时转换 `BCH` 地址的选项
				if (!isBCH) remove(find { it.second == WalletText.getBCHLegacyAddress })
			}
		}

		fun convertToChildAddresses(seriesAddress: String): List<Pair<String, String>> {
			return when {
				seriesAddress.contains(",") -> seriesAddress.split(",").map {
					Pair(
						it.substringBeforeLast("|"),
						it.substringAfterLast("|")
					)
				}
				seriesAddress.contains("|") -> listOf(
					Pair(
						seriesAddress.substringBeforeLast("|"),
						seriesAddress.substringAfterLast("|")
					)
				)
				else -> listOf(Pair(seriesAddress, ""))
			}
		}

		fun getCurrentAddressIndexByChainType(chainType: Int, hold: (String) -> Unit) {
			fun getTargetAddressIndex(address: String, targetAddress: String): String {
				return if (address.contains(",")) {
					address.split(",").find {
						it.contains(targetAddress)
					}?.substringAfterLast("|").orEmpty()
				} else address.substringAfterLast("|")
			}
			WalletTable.getCurrentWallet {
				when (chainType) {
					ChainType.ETH.id -> hold(getTargetAddressIndex(ethAddresses, currentETHAndERCAddress))
					ChainType.ETC.id -> hold(getTargetAddressIndex(etcAddresses, currentETCAddress))
					ChainType.LTC.id -> hold(getTargetAddressIndex(ltcAddresses, currentLTCAddress))
					ChainType.BCH.id -> hold(getTargetAddressIndex(bchAddresses, currentBCHAddress))
					ChainType.BTC.id ->
						if (Config.isTestEnvironment())
							hold(getTargetAddressIndex(btcSeriesTestAddresses, currentBTCSeriesTestAddress))
						else hold(getTargetAddressIndex(btcAddresses, currentBTCAddress))
				}
			}
		}

		private fun insertNewAddressToMyToken(
			symbol: String,
			contract: String,
			address: String,
			chainID: String
		) {
			DefaultTokenTable.getTokenBySymbolAndContractFromAllChains(symbol, contract) { it ->
				it?.let {
					doAsync {
						MyTokenTable.insert(MyTokenTable(it.apply { chain_id = chainID }, address))
					}
				}
			}
		}
	}
}