package io.goldstone.blockchain.common.sandbox

import android.content.Context
import com.blinnnk.extension.isNotNull
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.Language.SandBoxText
import io.goldstone.blockchain.common.component.overlay.Dashboard
import io.goldstone.blockchain.common.component.overlay.LoadingView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.FingerprintPaymentText
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.ethereum.getAddress
import io.goldstone.blockchain.crypto.keystore.generateETHSeriesAddress
import io.goldstone.blockchain.crypto.keystore.getBigIntegerPrivateKeyByWalletID
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.*
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.Bip44Address
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import kotlinx.coroutines.*

/**
 * @date: 2018-12-25.
 * @author: yangLiHai
 * @description:
 */

fun recoveryMnemonicWallet(context: Context, walletModel: WalletModel, callback: () -> Unit) {
	launchUI {
		Dashboard(context) {
			showAlertView(
				FingerprintPaymentText.usePassword,
				"请输入${walletModel.name} 的密码",
				true,
				cancelAction = {
					callback()
				}
			) { passwordInput ->
				val password = passwordInput?.text?.toString()
				if (password.isNullOrEmpty()) {
					recoveryMnemonicWallet(context, walletModel,callback)
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
								recoveryMnemonicWallet(walletModel, callback)
							} else {
								recoveryMnemonicWallet(context, walletModel, callback)
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

private fun recoveryMnemonicWallet(walletModel: WalletModel, callback: () -> Unit) {
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
		),
		callback)
}

fun recoveryKeystoreWallet(context: Context, walletModel: WalletModel, callback: () -> Unit) {
	launchUI {
		Dashboard(context) {
			showAlertView(
				FingerprintPaymentText.usePassword,
				SandBoxText.walletPasswordInputTip(walletModel.name),
				true,
				cancelAction = {
					callback()
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
								insertWallet(walletModel, multiChainAddresses, callback)
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
		isUsing = walletModel.isUsing,
		hint = walletModel.hint,
		isWatchOnly = walletModel.isWatchOnly,
		hasBackUpMnemonic = walletModel.hasBackUpMnemonic
	).apply {
		WalletTable.dao.insert(this)
		callback()
	}
}


private fun insertWallet(walletModel: WalletModel, chainAddresses: ChainAddresses, callback: () -> Unit) {
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
		isUsing = walletModel.isUsing,
		hint = walletModel.hint,
		isWatchOnly = walletModel.isWatchOnly,
		encryptMnemonic = walletModel.encryptMnemonic,
		encryptFingerPrinterKey = walletModel.encryptFingerPrinterKey,
		hasBackUpMnemonic = walletModel.hasBackUpMnemonic
	).apply {
		WalletTable.dao.insert(this)
		callback()
	}
}