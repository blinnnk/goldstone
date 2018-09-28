package io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.presenter

import android.content.Context
import android.os.Bundle
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isNull
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.cell.GraySquareCellWithButtons
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blockchain.crypto.bitcoin.storeBase58PrivateKey
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.keystore.getEthereumWalletByMnemonic
import io.goldstone.blockchain.crypto.keystore.verifyKeystorePassword
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
import io.goldstone.blockchain.crypto.litecoin.storeLTCBase58PrivateKey
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.AddressCommissionModel
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.getTargetKeyName
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.model.ContactModel
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
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
class AddressManagerPresenter(
	override val fragment: AddressManagerFragment
) : BasePresenter<AddressManagerFragment>() {

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		setBackEvent()
		if (SharedWallet.getCurrentWalletType().isBIP44()) fragment.showCreatorDashboard()
	}

	fun showEOSPublickeyDescription(cell: GraySquareCellWithButtons, key: String, wallet: WalletTable?) {
		if (wallet?.eosAccountNames?.getTargetKeyName(key).isNull())
			EOSAPI.getAccountNameByPublicKey(
				key,
				{ LogUtil.error("showEOSPublickeyDescription", it) }
			) { chainNames ->
				WalletTable.updateEOSAccountName(chainNames) {
					val description = if (it) "available publickey" else "inactivation publickey"
					cell.showDescriptionTitle(description)
				}
			} else cell.showDescriptionTitle("available publickey")
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

	fun getAddressCreatorMenu(): List<Pair<Int, String>> {
		return listOf(
			Pair(R.drawable.eth_creator_icon, WalletSettingsText.newETHSeriesAddress),
			Pair(R.drawable.etc_creator_icon, WalletSettingsText.newETCAddress),
			Pair(R.drawable.btc_creator_icon, WalletSettingsText.newBTCAddress),
			Pair(R.drawable.ltc_creator_icon, WalletSettingsText.newLTCAddress),
			Pair(R.drawable.bch_creator_icon, WalletSettingsText.newBCHAddress),
			Pair(R.drawable.eos_creator_icon, WalletSettingsText.newEOSAddress)
		)
	}

	fun showAllETHSeriesAddresses(): Runnable {
		return Runnable {
			showTargetFragment<ChainAddressesFragment, WalletSettingsFragment>(
				Bundle().apply { putInt(ArgumentKey.coinType, ChainType.ETH.id) }
			)
		}
	}

	fun showAllETCAddresses(): Runnable {
		return Runnable {
			showTargetFragment<ChainAddressesFragment, WalletSettingsFragment>(
				Bundle().apply { putInt(ArgumentKey.coinType, ChainType.ETC.id) }
			)
		}
	}

	fun showAllEOSAddresses(): Runnable {
		return Runnable {
			showTargetFragment<ChainAddressesFragment, WalletSettingsFragment>(
				Bundle().apply { putInt(ArgumentKey.coinType, ChainType.EOS.id) }
			)
		}
	}

	fun showAllBTCAddresses(): Runnable {
		return Runnable {
			showTargetFragment<ChainAddressesFragment, WalletSettingsFragment>(
				Bundle().apply { putInt(ArgumentKey.coinType, ChainType.BTC.id) }
			)
		}
	}

	fun showAllLTCAddresses(): Runnable {
		return Runnable {
			showTargetFragment<ChainAddressesFragment, WalletSettingsFragment>(
				Bundle().apply { putInt(ArgumentKey.coinType, ChainType.LTC.id) }
			)
		}
	}

	fun showAllBCHAddresses(): Runnable {
		return Runnable {
			showTargetFragment<ChainAddressesFragment, WalletSettingsFragment>(
				Bundle().apply { putInt(ArgumentKey.coinType, ChainType.BCH.id) }
			)
		}
	}

	companion object {

		fun showPrivateKeyExportFragment(
			address: String,
			chainType: ChainType,
			walletSettingsFragment: WalletSettingsFragment
		) {
			walletSettingsFragment.apply {
				if (!SharedWallet.isWatchOnlyWallet()) {
					AddressManagerFragment.removeDashboard(context)
					presenter.showTargetFragment<PrivateKeyExportFragment>(
						Bundle().apply {
							putString(ArgumentKey.address, address)
							putInt(ArgumentKey.chainType, chainType.id)
						}
					)
				} else context.alert(WalletText.watchOnly)
			}
		}

		fun showQRCodeFragment(addressModel: ContactModel, walletSettingsFragment: WalletSettingsFragment) {
			walletSettingsFragment.apply {
				// 这个页面不限时 `Header` 上的加号按钮
				showAddButton(false)
				AddressManagerFragment.removeDashboard(context)
				presenter.showTargetFragment<QRCodeFragment>(
					Bundle().apply { putSerializable(ArgumentKey.addressModel, addressModel) }
				)
			}
		}

		fun showKeystoreExportFragment(address: String, walletSettingsFragment: WalletSettingsFragment) {
			walletSettingsFragment.apply {
				// 这个页面不限时 `Header` 上的加号按钮
				showAddButton(false)
				if (!SharedWallet.isWatchOnlyWallet()) {
					AddressManagerFragment.removeDashboard(context)
					presenter.showTargetFragment<KeystoreExportFragment>(
						Bundle().apply { putString(ArgumentKey.address, address) }
					)
				} else context.alert(WalletText.watchOnly)
			}
		}

		fun createETHSeriesAddress(
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
								TokenContract.ethContract,
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
						wallet.updateETHSeriesAddresses(address, newAddressIndex) {
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
								TokenContract.etcContract,
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
						wallet.updateETCAddresses(address, newAddressIndex) {
							hold(convertToChildAddresses(it).toArrayList())
						}
					}
				}
			}
		}

		fun createEOSAddress(
			context: Context,
			password: String,
			hold: (ArrayList<Pair<String, String>>) -> Unit
		) {
			context.verifyKeystorePassword(
				password,
				SharedAddress.getCurrentEOS(),
				true
			) { isCorrect ->
				if (!isCorrect) context.alert(CommonText.wrongPassword)
				else WalletTable.getEOSWalletLatestChildAddressIndex { wallet, childAddressIndex ->
					wallet.encryptMnemonic?.let { encryptMnemonic ->
						val mnemonic = JavaKeystoreUtil().decryptData(encryptMnemonic)
						val newAddressIndex = childAddressIndex + 1
						val newChildPath = wallet.eosPath.substringBeforeLast("/") + "/" + newAddressIndex
						EOSWalletUtils.generateKeyPair(mnemonic, newChildPath).let { eosKeyPair ->
							context.storeBase58PrivateKey(
								eosKeyPair.privateKey,
								eosKeyPair.address,
								password,
								false
							)
							// 在 `MyToken` 里面注册新地址, 用于更换 `DefaultAddress` 的时候做准备
							ChainID.getAllEOSChainID().forEach { chainID ->
								insertNewAddressToMyToken(
									TokenContract.eosContract,
									eosKeyPair.address,
									chainID
								)
							}
							// 注册新增的子地址
							XinGePushReceiver.registerSingleAddress(
								AddressCommissionModel(
									eosKeyPair.address,
									ChainType.EOS.id,
									1,
									wallet.id
								)
							)
							wallet.updateEOSAddresses(eosKeyPair.address, newAddressIndex) {
								hold(convertToChildAddresses(it).toArrayList())
							}
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
				SharedAddress.getCurrentBTC(),
				true
			) { isCorrect ->
				if (isCorrect) WalletTable.getBTCWalletLatestChildAddressIndex { wallet, childAddressIndex ->
					wallet.encryptMnemonic?.let { encryptMnemonic ->
						val mnemonic = JavaKeystoreUtil().decryptData(encryptMnemonic)
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
								false
							)
							// 在 `MyToken` 里面注册新地址, 用于更换 `DefaultAddress` 的时候做准备
							insertNewAddressToMyToken(
								TokenContract.btcContract,
								address,
								ChainID.btcMain
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
							wallet.updateBTCAddresses(address, newAddressIndex) {
								hold(convertToChildAddresses(it).toArrayList())
							}
						}
					}
				}
				else context.alert(CommonText.wrongPassword)
			}
		}

		fun createBTCTestAddress(
			context: Context,
			password: String,
			hold: (ArrayList<Pair<String, String>>) -> Unit
		) {
			context.verifyKeystorePassword(
				password,
				SharedAddress.getCurrentBTCSeriesTest(),
				true
			) { isCorrect ->
				if (!isCorrect) context.alert(CommonText.wrongPassword)
				else WalletTable.getBTCTestWalletLatestChildAddressIndex { wallet, childAddressIndex ->
					wallet.encryptMnemonic?.let { encryptMnemonic ->
						val mnemonic = JavaKeystoreUtil().decryptData(encryptMnemonic)
						val newAddressIndex = childAddressIndex + 1
						val newChildPath = wallet.btcTestPath.substringBeforeLast("/") + "/" + newAddressIndex
						BTCWalletUtils.getBitcoinWalletByMnemonic(mnemonic, newChildPath) { address, secret ->
							context.storeBase58PrivateKey(
								secret,
								address,
								password,
								true
							)
							// 在 `MyToken` 里面注册新地址, 用于更换 `DefaultAddress` 的时候做准备
							// `BTCTest` 是 `BTCSeries` 公用的地址
							insertNewAddressToMyToken(
								TokenContract.btcContract,
								address,
								ChainID.btcTest
							)
							// 插入 LTC 账号
							insertNewAddressToMyToken(
								TokenContract.ltcContract,
								address,
								ChainID.ltcTest
							)
							// 插入 BCH 账号
							insertNewAddressToMyToken(
								TokenContract.bchContract,
								address,
								ChainID.bchTest
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
							wallet.updateBTCSeriesTestAddresses(address, newAddressIndex) {
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
				SharedAddress.getCurrentBCH(),
				true
			) { isCorrect ->
				if (!isCorrect) context.alert(CommonText.wrongPassword)
				else WalletTable.getBCHWalletLatestChildAddressIndex { wallet, childAddressIndex ->
					wallet.encryptMnemonic?.let { encryptMnemonic ->
						val mnemonic = JavaKeystoreUtil().decryptData(encryptMnemonic)
						val newAddressIndex = childAddressIndex + 1
						val newChildPath = wallet.bchPath.substringBeforeLast("/") + "/" + newAddressIndex
						BCHWalletUtils.generateBCHKeyPair(mnemonic, newChildPath).let { bchKeyPair ->
							context.storeBase58PrivateKey(
								bchKeyPair.privateKey,
								bchKeyPair.address,
								password,
								false
							)
							// 在 `MyToken` 里面注册新地址, 用于更换 `DefaultAddress` 的时候做准备
							insertNewAddressToMyToken(
								TokenContract.bchContract,
								bchKeyPair.address,
								ChainID.bchMain
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
							wallet.updateBCHAddresses(bchKeyPair.address, newAddressIndex) {
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
				SharedAddress.getCurrentLTC(),
				true
			) { isCorrect ->
				if (!isCorrect) context.alert(CommonText.wrongPassword)
				else WalletTable.getLTCWalletLatestChildAddressIndex { wallet, childAddressIndex ->
					wallet.encryptMnemonic?.let { encryptMnemonic ->
						val mnemonic = JavaKeystoreUtil().decryptData(encryptMnemonic)
						val newAddressIndex = childAddressIndex + 1
						val newChildPath = wallet.ltcPath.substringBeforeLast("/") + "/" + newAddressIndex
						LTCWalletUtils.generateBase58Keypair(
							mnemonic,
							newChildPath
						).let { ltcKeyPair ->
							context.storeLTCBase58PrivateKey(
								ltcKeyPair.privateKey,
								ltcKeyPair.address,
								password
							)
							// 在 `MyToken` 里面注册新地址, 用于更换 `DefaultAddress` 的时候做准备
							insertNewAddressToMyToken(
								TokenContract.ltcContract,
								ltcKeyPair.address,
								ChainID.ltcMain
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
							wallet.updateLTCAddresses(ltcKeyPair.address, newAddressIndex) {
								hold(convertToChildAddresses(it).toArrayList())
							}
						}
					}
				}
			}
		}

		fun getCellDashboardMenu(
			hasDefaultCell: Boolean = true,
			isBCH: Boolean = false
		): List<Pair<Int, String>> {
			return arrayListOf(
				Pair(R.drawable.default_icon, WalletText.setDefaultAddress),
				Pair(R.drawable.qr_code_icon, WalletText.qrCode),
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

		private fun insertNewAddressToMyToken(
			contract: String,
			address: String,
			chainID: String
		) {
			DefaultTokenTable.getTokenByContractFromAllChains(contract) { it ->
				it?.let {
					MyTokenTable(it.apply { this.chainID = chainID }, address).insert()
				}
			}
		}
	}
}