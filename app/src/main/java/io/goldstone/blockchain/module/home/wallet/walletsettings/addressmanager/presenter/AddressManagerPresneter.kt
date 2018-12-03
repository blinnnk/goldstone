package io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.presenter

import android.os.Bundle
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.isNull
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.component.cell.GraySquareCellWithButtons
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.ethereum.getAddress
import io.goldstone.blockchain.crypto.keystore.generateETHSeriesAddress
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.multichain.node.ChainNodeTable
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.AddressCommissionModel
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.Bip44Address
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.getTargetKeyName
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.model.ContactModel
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.view.AddressManagerFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.chainaddresses.view.ChainAddressesFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.view.KeystoreExportFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view.PrivateKeyExportFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view.QRCodeFragment
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
		if (SharedWallet.getCurrentWalletType().isBIP44())
			fragment.setAddButtonEvent()
	}

	fun showEOSPublickeyDescription(cell: GraySquareCellWithButtons, key: String, wallet: WalletTable?) {
		if (wallet?.eosAccountNames?.getTargetKeyName(key).isNull())
			EOSAPI.getAccountNameByPublicKey(key) { accountNames, error ->
				if (accountNames.isNotNull() && error.isNone()) {
					val description =
						if (accountNames.isNotEmpty()) WalletSettingsText.activatedPublicKey
						else WalletSettingsText.unactivatedPublicKey
					launchUI {
						cell.showDescriptionTitle(description)
					}
				}
			}
	}

	fun setBackEvent() {
		fragment.getParentFragment<WalletSettingsFragment>()?.apply {
			showBackButton(true) {
				presenter.popFragmentFrom<AddressManagerFragment>()
			}
		}
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

	fun showAllBTCSeriesTestAddresses(): Runnable {
		return Runnable {
			showTargetFragment<ChainAddressesFragment, WalletSettingsFragment>(
				Bundle().apply { putInt(ArgumentKey.coinType, ChainType.AllTest.id) }
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
					presenter.showTargetFragment<PrivateKeyExportFragment>(
						Bundle().apply {
							putString(ArgumentKey.address, address)
							putInt(ArgumentKey.chainType, chainType.id)
						}
					)
				} else safeShowError(Throwable(WalletText.watchOnly))
			}
		}

		fun showQRCodeFragment(addressModel: ContactModel, walletSettingsFragment: WalletSettingsFragment) {
			walletSettingsFragment.apply {
				presenter.showTargetFragment<QRCodeFragment>(
					Bundle().apply { putSerializable(ArgumentKey.addressModel, addressModel) }
				)
			}
		}

		fun showKeystoreExportFragment(
			address: String,
			chainType: ChainType,
			walletSettingsFragment: WalletSettingsFragment
		) {
			walletSettingsFragment.apply {
				// 这个页面不限时 `Header` 上的加号按钮
				showAddButton(false) {}
				if (!SharedWallet.isWatchOnlyWallet()) {
					presenter.showTargetFragment<KeystoreExportFragment>(
						Bundle().apply {
							putString(ArgumentKey.address, address)
							putInt(ArgumentKey.coinType, chainType.id)
						}
					)
				} else context.alert(WalletText.watchOnly)
			}
		}

		@WorkerThread
		fun createETHSeriesAddress(hold: (addresses: List<Bip44Address>) -> Unit) {
			WalletTable.getLatestAddressIndex(ChainType.ETH) { wallet, childAddressIndex ->
				wallet.encryptMnemonic?.let { seed ->
					val mnemonic = JavaKeystoreUtil().decryptData(seed)
					val newAddressIndex = childAddressIndex + 1
					val newChildPath = wallet.ethPath.substringBeforeLast("/") + "/" + newAddressIndex
					val address = generateETHSeriesAddress(mnemonic, newChildPath).getAddress()
					// 新创建的账号插入所有对应的链的默认 `Token`
					// 因为数据库存储的 Nodes 可能在同一个 ChainID 下存在多条, 例如 Infura Mainnet, GoldStone Mainnet
					// 所以这里拉取回来的数据做一次去重复处理
					val ethNodes =
						ChainNodeTable.dao.getETHNodes().distinctBy { it.chainID }
					ethNodes.forEach {
						insertNewToMyToken(
							TokenContract.ethContract,
							CoinSymbol.eth,
							address,
							it.chainID
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
					wallet.updateETHSeriesAddresses(Bip44Address(address, newAddressIndex, ChainType.ETH.id), hold)
				}
			}
		}

		@WorkerThread
		fun createETCAddress(hold: (addresses: List<Bip44Address>) -> Unit) {
			WalletTable.getLatestAddressIndex(ChainType.ETC) { wallet, childAddressIndex ->
				wallet.encryptMnemonic?.let { seed ->
					val mnemonic = JavaKeystoreUtil().decryptData(seed)
					val newAddressIndex = childAddressIndex + 1
					val newChildPath = wallet.etcPath.substringBeforeLast("/") + "/" + newAddressIndex
					val address = generateETHSeriesAddress(mnemonic, newChildPath).getAddress()
					// 新创建的账号插入所有对应的链的默认 `Token`
					// 在 `MyToken` 里面注册新地址, 用于更换 `DefaultAddress` 的时候做准备
					val etcNodes =
						GoldStoneDataBase.database.chainNodeDao().getETCNodes().distinctBy { it.chainID }
					etcNodes.forEach {
						insertNewToMyToken(
							TokenContract.etcContract,
							CoinSymbol.etc,
							address,
							it.chainID
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
					wallet.updateETCAddresses(Bip44Address(address, newAddressIndex, ChainType.ETC.id), hold)
				}
			}
		}

		@WorkerThread
		fun createEOSAddress(hold: (addresses: List<Bip44Address>) -> Unit) {
			WalletTable.getLatestAddressIndex(ChainType.EOS) { wallet, childAddressIndex ->
				wallet.encryptMnemonic?.let { encryptMnemonic ->
					val mnemonic = JavaKeystoreUtil().decryptData(encryptMnemonic)
					val newAddressIndex = childAddressIndex + 1
					val newChildPath = wallet.eosPath.substringBeforeLast("/") + "/" + newAddressIndex
					EOSWalletUtils.generateKeyPair(mnemonic, newChildPath).let { eosKeyPair ->
						// 在 `MyToken` 里面注册新地址, 用于更换 `DefaultAddress` 的时候做准备
						val eosNodes =
							GoldStoneDataBase.database.chainNodeDao().getEOSNodes().distinctBy { it.chainID }
						eosNodes.forEach {
							insertNewToMyToken(
								TokenContract.eosContract,
								CoinSymbol.eos,
								eosKeyPair.address,
								it.chainID
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
						wallet.updateEOSAddresses(Bip44Address(eosKeyPair.address, newAddressIndex, ChainType.EOS.id), hold)
					}
				}
			}
		}

		@WorkerThread
		fun createBTCAddress(hold: (addresses: List<Bip44Address>) -> Unit) {
			WalletTable.getLatestAddressIndex(ChainType.BTC) { wallet, childAddressIndex ->
				wallet.encryptMnemonic?.let { encryptMnemonic ->
					val mnemonic = JavaKeystoreUtil().decryptData(encryptMnemonic)
					val newAddressIndex = childAddressIndex + 1
					val newChildPath = wallet.btcPath.substringBeforeLast("/") + "/" + newAddressIndex
					BTCWalletUtils.getBitcoinWalletByMnemonic(mnemonic, newChildPath) { address, _ ->
						// 在 `MyToken` 里面注册新地址, 用于更换 `DefaultAddress` 的时候做准备
						insertNewToMyToken(
							TokenContract.btcContract,
							CoinSymbol.pureBTCSymbol,
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
						wallet.updateBTCAddresses(Bip44Address(address, newAddressIndex, ChainType.BTC.id), hold)
					}
				}
			}
		}

		@WorkerThread
		fun createBTCTestAddress(hold: (addresses: List<Bip44Address>) -> Unit) {
			WalletTable.getLatestAddressIndex(ChainType.AllTest) { wallet, childAddressIndex ->
				wallet.encryptMnemonic?.let { encryptMnemonic ->
					val mnemonic = JavaKeystoreUtil().decryptData(encryptMnemonic)
					val newAddressIndex = childAddressIndex + 1
					val newChildPath = wallet.btcTestPath.substringBeforeLast("/") + "/" + newAddressIndex
					BTCWalletUtils.getBitcoinWalletByMnemonic(mnemonic, newChildPath) { address, _ ->
						// 在 `MyToken` 里面注册新地址, 用于更换 `DefaultAddress` 的时候做准备
						// `BTCTest` 是 `BTCSeries` 公用的地址
						insertNewToMyToken(
							TokenContract.btcContract,
							CoinSymbol.pureBTCSymbol,
							address,
							ChainID.btcTest
						)
						// 插入 LTC 账号
						insertNewToMyToken(
							TokenContract.ltcContract,
							CoinSymbol.ltc,
							address,
							ChainID.ltcTest
						)
						// 插入 BCH 账号
						insertNewToMyToken(
							TokenContract.bchContract,
							CoinSymbol.bch,
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
						wallet.updateBTCSeriesTestAddresses(Bip44Address(address, newAddressIndex, ChainType.AllTest.id), hold)
					}
				}
			}
		}

		fun createBCHAddress(@UiThread hold: (addresses: List<Bip44Address>) -> Unit) {
			WalletTable.getLatestAddressIndex(ChainType.BCH) { wallet, childAddressIndex ->
				wallet.encryptMnemonic?.let { encryptMnemonic ->
					val mnemonic = JavaKeystoreUtil().decryptData(encryptMnemonic)
					val newAddressIndex = childAddressIndex + 1
					val newChildPath = wallet.bchPath.substringBeforeLast("/") + "/" + newAddressIndex
					BCHWalletUtils.generateBCHKeyPair(mnemonic, newChildPath).let { bchKeyPair ->
						// 在 `MyToken` 里面注册新地址, 用于更换 `DefaultAddress` 的时候做准备
						insertNewToMyToken(
							TokenContract.bchContract,
							CoinSymbol.bch,
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
						wallet.updateBCHAddresses(Bip44Address(bchKeyPair.address, newAddressIndex, ChainType.BCH.id), hold)
					}
				}
			}
		}

		@WorkerThread
		fun createLTCAddress(hold: (addresses: List<Bip44Address>) -> Unit) {
			WalletTable.getLatestAddressIndex(ChainType.LTC) { wallet, childAddressIndex ->
				wallet.encryptMnemonic?.let { encryptMnemonic ->
					val mnemonic = JavaKeystoreUtil().decryptData(encryptMnemonic)
					val newAddressIndex = childAddressIndex + 1
					val newChildPath = wallet.ltcPath.substringBeforeLast("/") + "/" + newAddressIndex
					LTCWalletUtils.generateBase58Keypair(
						mnemonic,
						newChildPath
					).let { ltcKeyPair ->

						// 在 `MyToken` 里面注册新地址, 用于更换 `DefaultAddress` 的时候做准备
						insertNewToMyToken(
							TokenContract.ltcContract,
							CoinSymbol.ltc,
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
						wallet.updateLTCAddresses(Bip44Address(ltcKeyPair.address, newAddressIndex, ChainType.LTC.id), hold)
					}
				}
			}
		}

		private fun insertNewToMyToken(
			contract: String,
			symbol: String,
			address: String,
			chainID: String
		) {
			val default =
				DefaultTokenTable.dao.getTokenFromAllChains(contract, symbol)
			default.firstOrNull()?.let {
				MyTokenTable.dao.insert(
					MyTokenTable(it.apply { this.chainID = chainID }, address)
				)
			}
		}
	}
}