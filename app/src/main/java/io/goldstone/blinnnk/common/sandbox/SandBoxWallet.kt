package io.goldstone.blinnnk.common.sandbox


import android.content.Context
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import io.goldstone.blinnnk.common.component.overlay.Dashboard
import io.goldstone.blinnnk.common.component.overlay.LoadingView
import io.goldstone.blinnnk.common.error.AccountError
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.language.FingerprintPaymentText
import io.goldstone.blinnnk.common.thread.launchDefault
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.ErrorDisplayManager
import io.goldstone.blinnnk.crypto.keystore.getBigIntegerPrivateKeyByWalletID
import io.goldstone.blinnnk.crypto.multichain.ChainAddresses
import io.goldstone.blinnnk.crypto.multichain.ChainPath
import io.goldstone.blinnnk.crypto.multichain.ChainType
import io.goldstone.blinnnk.crypto.multichain.GenerateMultiChainWallet
import io.goldstone.blinnnk.crypto.utils.JavaKeystoreUtil
import io.goldstone.blinnnk.crypto.utils.KeystoreInfo
import io.goldstone.blinnnk.crypto.utils.MultiChainUtils
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.Bip44Address
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import java.math.BigInteger

/**
 * @date: 2018-12-25.
 * @author: yangLiHai
 * @description:
 */

fun showRecoveryMnemonicWalletDashboard(
	context: Context,
	walletModel: WalletBackUpModel,
	@WorkerThread callback: () -> Unit
) {
	launchUI {
		getPrivateKeyOrPassword<String>(context, walletModel.name, walletModel.id) { password, error ->
			if (password.isNotNull() && error.isNone())
				recoveryMnemonicWallet(context, walletModel, password, callback)
			else ErrorDisplayManager(error).show(context)
		}
	}
}

private fun recoveryMnemonicWallet(
	context: Context,
	walletModel: WalletBackUpModel,
	password: String,
	@WorkerThread callback: () -> Unit
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
	@WorkerThread callback: () -> Unit
) {
	launchUI {
		getPrivateKeyOrPassword<BigInteger>(context, walletModel.name, walletModel.id) { privateKey, error ->
			if (privateKey.isNotNull() && error.isNone()) {
				val multiChainAddresses = MultiChainUtils.getMultiChainAddressesByRootKey(privateKey)
				insertWallet(walletModel, multiChainAddresses)
				CreateWalletPresenter.insertNewAccount(multiChainAddresses, callback)
			} else ErrorDisplayManager(error).show(context)
		}
	}
}

fun recoveryWatchOnlyWallet(
	walletModel: WalletBackUpModel,
	@WorkerThread callback: () -> Unit
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
			),
			callback
		)
	}
}

@UiThread
private inline fun <reified T> getPrivateKeyOrPassword(
	context: Context,
	walletName: String,
	walletID: Int,
	@WorkerThread crossinline hold: (result: T?, error: AccountError) -> Unit
) {
	Dashboard(context) {
		showAlertView(
			FingerprintPaymentText.usePassword,
			"please input the password of the $walletName",
			true,
			cancelAction = {
				hold(null, AccountError.None)
			}
		) { passwordInput ->
			val password = passwordInput?.text?.toString()
			if (password.isNullOrEmpty()) {
				hold(null, AccountError("Empty Password"))
				ErrorDisplayManager(Throwable(CommonText.wrongPassword)).show(context)
			} else {
				val loadingView = LoadingView(context)
				loadingView.setCancelable(false)
				loadingView.show()
				launchDefault {
					context.getBigIntegerPrivateKeyByWalletID(password, walletID) { privateKey, error ->
						if (privateKey.isNotNull() && error.isNone()) {
							hold(privateKey as T, error)
							launchUI {
								loadingView.remove()
							}
						} else {
							hold(null, error)
							launchUI {
								loadingView.remove()
							}
						}
					}
				}
			}
		}
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