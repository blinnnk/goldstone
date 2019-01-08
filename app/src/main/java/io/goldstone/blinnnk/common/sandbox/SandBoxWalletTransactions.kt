package io.goldstone.blinnnk.common.sandbox


import android.content.Context
import com.blinnnk.extension.isNotNull
import io.goldstone.blinnnk.GoldStoneApp
import io.goldstone.blinnnk.common.component.overlay.Dashboard
import io.goldstone.blinnnk.common.component.overlay.LoadingView
import io.goldstone.blinnnk.common.language.*
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.ErrorDisplayManager
import io.goldstone.blinnnk.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blinnnk.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blinnnk.crypto.eos.EOSWalletUtils
import io.goldstone.blinnnk.crypto.ethereum.getAddress
import io.goldstone.blinnnk.crypto.keystore.generateETHSeriesAddress
import io.goldstone.blinnnk.crypto.keystore.getBigIntegerPrivateKeyByWalletID
import io.goldstone.blinnnk.crypto.litecoin.LTCWalletUtils
import io.goldstone.blinnnk.crypto.multichain.*
import io.goldstone.blinnnk.crypto.utils.*
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.Bip44Address
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import kotlinx.coroutines.*

/**
 * @date: 2018-12-25.
 * @author: yangLiHai
 * @description:
 */

fun recoveryMnemonicWallet(context: Context, walletModel: WalletModel, callback: (success: Boolean) -> Unit) {
	launchUI {
		Dashboard(context) {
			showAlertView(
				FingerprintPaymentText.usePassword,
				WalletText.walletPasswordInputTip(walletModel.name),
				true,
				cancelAction = {
					callback(false)
				}
			) { passwordInput ->
				val password = passwordInput?.text?.toString()
				if (password.isNullOrEmpty()) {
					recoveryMnemonicWallet(context, walletModel, callback)
					launchUI {
						ErrorDisplayManager(Throwable(CommonText.wrongPassword)).show(context)
					}
				} else {
					val loadingView = LoadingView(context)
					loadingView.show()
					GlobalScope.launch(Dispatchers.Default) {
						GoldStoneApp.appContext.getBigIntegerPrivateKeyByWalletID(password, walletModel.id) { privateKey, error ->
							if (privateKey.isNotNull() && error.isNone()) {
								recoveryMnemonicWallet(walletModel)
								callback(true)
							} else {
								recoveryMnemonicWallet(context, walletModel, callback)
								launchUI {
									ErrorDisplayManager(error).show(context)
								}
							}
							launchUI { loadingView.remove() }
						}
						
					}
				}
				
			}
		}
	}
}

private fun recoveryMnemonicWallet(walletModel: WalletModel) {
	val mnemonic = JavaKeystoreUtil(KeystoreInfo.isMnemonic()).decryptData(walletModel.encryptMnemonic!!)
	val ethAddress = Bip44Address(
		generateETHSeriesAddress(mnemonic, walletModel.ethPath).getAddress(),
		GenerateMultiChainWallet.getAddressIndexFromPath(walletModel.ethPath),
		ChainType.ETH.id
	)
	val etcAddress = Bip44Address(
		generateETHSeriesAddress(mnemonic, walletModel.etcPath).getAddress(),
		GenerateMultiChainWallet.getAddressIndexFromPath(walletModel.etcPath),
		ChainType.ETC.id
	)
	val btcAddress = Bip44Address(
		BTCWalletUtils.getBitcoinWalletAddressByMnemonic(mnemonic, walletModel.btcPath),
		GenerateMultiChainWallet.getAddressIndexFromPath(walletModel.btcPath),
		ChainType.BTC.id
	)
	val btcTestAddress = Bip44Address(
		BTCWalletUtils.getBitcoinWalletAddressByMnemonic(mnemonic, walletModel.btcTestPath),
		GenerateMultiChainWallet.getAddressIndexFromPath(walletModel.btcTestPath),
		ChainType.AllTest.id
	)
	val ltcAddress = Bip44Address(
		LTCWalletUtils.generateBase58Keypair(mnemonic, walletModel.ltcPath).address,
		GenerateMultiChainWallet.getAddressIndexFromPath(walletModel.ltcPath),
		ChainType.LTC.id
	)
	val bchAddress = Bip44Address(
		BCHWalletUtils.generateBCHKeyPair(mnemonic, walletModel.bchPath).address,
		GenerateMultiChainWallet.getAddressIndexFromPath(walletModel.bchPath),
		ChainType.BCH.id
	)
	val eosAddress = Bip44Address(
		EOSWalletUtils.generateKeyPair(mnemonic, walletModel.eosPath).address,
		GenerateMultiChainWallet.getAddressIndexFromPath(walletModel.eosPath),
		ChainType.EOS.id
	)
	insertWallet(walletModel, ChainAddresses(
		ethAddress,
		etcAddress,
		btcAddress,
		btcTestAddress,
		ltcAddress,
		bchAddress,
		eosAddress
	))
}

fun recoveryKeystoreWallet(context: Context, walletModel: WalletModel, callback: (success: Boolean) -> Unit) {
	launchUI {
		Dashboard(context) {
			showAlertView(
				FingerprintPaymentText.usePassword,
				WalletText.walletPasswordInputTip(walletModel.name),
				true,
				cancelAction = {
					callback(false)
				}
			) { passwordInput ->
				val password = passwordInput?.text?.toString()
				if (password.isNullOrEmpty()) {
					recoveryKeystoreWallet(context, walletModel, callback)
					launchUI {
						ErrorDisplayManager(Throwable(CommonText.wrongPassword)).show(context)
					}
				} else {
					val loadingView = LoadingView(context)
					loadingView.show()
					GlobalScope.launch(Dispatchers.Default) {
						GoldStoneApp.appContext.getBigIntegerPrivateKeyByWalletID(password, walletModel.id) { privateKey, error ->
							launchUI { loadingView.remove() }
							if (privateKey.isNotNull() && error.isNone()) {
								val multiChainAddresses = MultiChainUtils.getMultiChainAddressesByRootKey(privateKey)
								insertWallet(walletModel, multiChainAddresses)
								callback(true)
							} else {
								recoveryKeystoreWallet(context, walletModel, callback)
								launchUI {
									ErrorDisplayManager(error).show(context)
								}
							}
						}
						
					}
				}
				
			}
		}
	}
	
}

fun recoveryWatchOnlyWallet(walletModel: WalletModel, callback: () -> Unit) {
	WalletTable(
		id = walletModel.id,
		avatarID = walletModel.avatarID,
		name = walletModel.name,
		currentETHSeriesAddress = walletModel.currentETHSeriesAddress,
		currentETCAddress = walletModel.currentETCAddress,
		currentBTCAddress = walletModel.currentBTCAddress,
		currentBTCSeriesTestAddress = walletModel.currentBTCSeriesTestAddress,
		currentLTCAddress = walletModel.currentLTCAddress,
		currentBCHAddress = walletModel.currentBCHAddress,
		currentEOSAddress = walletModel.currentEOSAddress,
		currentEOSAccountName = walletModel.currentEOSAccountName,
		ethAddresses = listOf(),
		etcAddresses = listOf(),
		btcAddresses = listOf(),
		bchAddresses = listOf(),
		btcSeriesTestAddresses = listOf(),
		ltcAddresses = listOf(),
		eosAddresses = listOf(),
		eosAccountNames = listOf(),
		ethPath = "",
		btcPath = "",
		etcPath = "",
		btcTestPath = "",
		bchPath = "",
		ltcPath = "",
		eosPath = "",
		isUsing = false,
		hint = walletModel.hint,
		isWatchOnly = walletModel.isWatchOnly,
		hasBackUpMnemonic = walletModel.hasBackUpMnemonic
	).apply {
		WalletTable.dao.insert(this)
		callback()
	}
}


private fun insertWallet(walletModel: WalletModel, chainAddresses: ChainAddresses) {
	WalletTable(
		id = walletModel.id,
		avatarID = walletModel.avatarID,
		name = walletModel.name,
		currentETHSeriesAddress = chainAddresses.eth.address,
		currentETCAddress = chainAddresses.etc.address,
		currentBTCAddress = chainAddresses.btc.address,
		currentBTCSeriesTestAddress = chainAddresses.btcSeriesTest.address,
		currentLTCAddress = chainAddresses.ltc.address,
		currentBCHAddress = chainAddresses.bch.address,
		currentEOSAddress = chainAddresses.eos.address,
		currentEOSAccountName = walletModel.currentEOSAccountName,
		ethAddresses = listOf(chainAddresses.eth),
		etcAddresses = listOf(chainAddresses.etc),
		btcAddresses = listOf(chainAddresses.btc),
		btcSeriesTestAddresses = listOf(chainAddresses.btcSeriesTest),
		ltcAddresses = listOf(chainAddresses.ltc),
		bchAddresses = listOf(chainAddresses.bch),
		eosAddresses = listOf(chainAddresses.eos),
		eosAccountNames = listOf(),
		ethPath = walletModel.ethPath,
		btcPath = walletModel.btcPath,
		etcPath = walletModel.etcPath,
		btcTestPath = walletModel.btcTestPath,
		bchPath = walletModel.bchPath,
		ltcPath = walletModel.ltcPath,
		eosPath = walletModel.eosPath,
		isUsing = false,
		hint = walletModel.hint,
		isWatchOnly = walletModel.isWatchOnly,
		encryptMnemonic = walletModel.encryptMnemonic,
		encryptFingerPrinterKey = walletModel.encryptFingerPrinterKey,
		hasBackUpMnemonic = walletModel.hasBackUpMnemonic
	).apply {
		WalletTable.dao.insert(this)
	}
}