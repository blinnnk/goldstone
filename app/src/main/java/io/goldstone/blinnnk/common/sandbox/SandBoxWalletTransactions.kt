package io.goldstone.blinnnk.common.sandbox


import android.content.Context
import com.blinnnk.extension.isNotNull
import io.goldstone.blinnnk.GoldStoneApp
import io.goldstone.blinnnk.common.component.overlay.Dashboard
import io.goldstone.blinnnk.common.component.overlay.LoadingView
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.language.FingerprintPaymentText
import io.goldstone.blinnnk.common.thread.launchDefault
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.ErrorDisplayManager
import io.goldstone.blinnnk.crypto.keystore.getBigIntegerPrivateKeyByWalletID
import io.goldstone.blinnnk.crypto.multichain.*
import io.goldstone.blinnnk.crypto.utils.*
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.Bip44Address
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import kotlinx.coroutines.*

/**
 * @date: 2018-12-25.
 * @author: yangLiHai
 * @description:
 */

fun recoveryMnemonicWallet(
	context: Context,
	walletModel: WalletBackUpModel,
	callback: (success: Boolean) -> Unit
) {
	launchUI {
		Dashboard(context) {
			showAlertView(
				FingerprintPaymentText.usePassword,
				"please input the password of the ${walletModel.name}",
				true,
				cancelAction = {
					callback(false)
				}) { passwordInput ->
				val password = passwordInput?.text?.toString()
				if (password.isNullOrEmpty()) {
					recoveryMnemonicWallet(context, walletModel, callback)
					launchUI {
						ErrorDisplayManager(Throwable(CommonText.wrongPassword)).show(context)
					}
				} else {
					val loadingView = LoadingView(context)
					loadingView.show()
					launchDefault {
						GoldStoneApp.appContext.getBigIntegerPrivateKeyByWalletID(password, walletModel.id) { privateKey, error ->
							if (privateKey.isNotNull() && error.isNone()) {
								recoveryMnemonicWallet(context, walletModel, password) {
									callback(true)
									launchUI { loadingView.remove() }
								}
							} else {
								recoveryMnemonicWallet(context, walletModel, callback)
								launchUI {
									loadingView.remove()
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

private fun recoveryMnemonicWallet(
	context: Context,
	walletModel: WalletBackUpModel,
	password: String,
	callback: () -> Unit
) {
	val mnemonic = JavaKeystoreUtil(KeystoreInfo.isMnemonic()).decryptData(walletModel.encryptMnemonic!!)
	GenerateMultiChainWallet.import(
		context, mnemonic, password, ChainPath(
			ethPath = walletModel.ethPath,
			etcPath = walletModel.etcPath,
			btcPath = walletModel.btcPath,
			testPath = walletModel.btcTestPath,
			ltcPath = walletModel.ltcPath,
			bchPath = walletModel.bchPath,
			eosPath = walletModel.eosPath
		), true
	) {
		insertWallet(walletModel, it)
		CreateWalletPresenter.insertNewAccount(it, callback)
	}
	
	
}

fun recoveryKeystoreWallet(
	context: Context,
	walletModel: WalletBackUpModel,
	callback: (success: Boolean) -> Unit
) {
	launchUI {
		Dashboard(context) {
			showAlertView(FingerprintPaymentText.usePassword,
				"please input the password of the ${walletModel.name}",
				true,
				cancelAction = {
					callback(false)
				}) { passwordInput ->
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
							if (privateKey.isNotNull() && error.isNone()) {
								val multiChainAddresses = MultiChainUtils.getMultiChainAddressesByRootKey(privateKey)
								insertWallet(walletModel, multiChainAddresses)
								CreateWalletPresenter.insertNewAccount(multiChainAddresses) {
									callback(true)
									launchUI { loadingView.remove() }
								}
							} else {
								recoveryKeystoreWallet(context, walletModel, callback)
								launchUI {
									ErrorDisplayManager(error).show(context)
									loadingView.remove()
								}
							}
						}
						
					}
				}
				
			}
		}
	}
	
}

fun recoveryWatchOnlyWallet(
	walletModel: WalletBackUpModel,
	callback: () -> Unit
) {
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
		CreateWalletPresenter.insertNewAccount(
			ChainAddresses(
				Bip44Address(walletModel.currentETHSeriesAddress, ChainType.ETH.id),
				Bip44Address(walletModel.currentETCAddress, ChainType.ETC.id),
				Bip44Address(walletModel.currentBTCAddress, ChainType.BTC.id),
				Bip44Address(walletModel.currentBTCSeriesTestAddress, ChainType.AllTest.id),
				Bip44Address(walletModel.currentLTCAddress, ChainType.LTC.id),
				Bip44Address(walletModel.currentBCHAddress, ChainType.BCH.id),
				Bip44Address(walletModel.currentEOSAddress, ChainType.EOS.id)
			)
		) { }
		callback()
	}
}


private fun insertWallet(
	walletModel: WalletBackUpModel,
	chainAddresses: ChainAddresses
) {
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
		hasBackUpMnemonic = walletModel.hasBackUpMnemonic
	).apply {
		WalletTable.dao.insert(this)
	}
}