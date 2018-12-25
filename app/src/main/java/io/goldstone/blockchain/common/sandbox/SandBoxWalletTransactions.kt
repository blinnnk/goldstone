package io.goldstone.blockchain.common.sandbox

import io.goldstone.blockchain.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.ethereum.getAddress
import io.goldstone.blockchain.crypto.keystore.generateETHSeriesAddress
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
import io.goldstone.blockchain.crypto.utils.KeystoreInfo
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.*

/**
 * @date: 2018-12-25.
 * @author: yangLiHai
 * @description:
 */

fun recoveryMnemonicWallet(walletModel: WalletModel) {
	val mnemonic = JavaKeystoreUtil(KeystoreInfo.isMnemonic()).decryptData(walletModel.encryptMnemonic!!)
	val ethAddress = Bip44Address(
		generateETHSeriesAddress(
			mnemonic,
			walletModel.ethPath
		).getAddress(),
		GenerateMultiChainWallet.getAddressIndexFromPath(walletModel.ethPath),
		ChainType.ETH.id
	)
	val etcAddress = Bip44Address(
		generateETHSeriesAddress(
			mnemonic,
			walletModel.etcPath
		).getAddress(),
		GenerateMultiChainWallet.getAddressIndexFromPath(walletModel.etcPath),
		ChainType.ETC.id
	)
	val btcAddress = Bip44Address(
		BTCWalletUtils.getBitcoinWalletAddressByMnemonic(
			mnemonic,
			walletModel.btcPath
		),
		GenerateMultiChainWallet.getAddressIndexFromPath(walletModel.btcPath),
		ChainType.BTC.id
	)
	val btcTestAddress = Bip44Address(
		BTCWalletUtils.getBitcoinWalletAddressByMnemonic(
			mnemonic,
			walletModel.btcTestPath
		),
		GenerateMultiChainWallet.getAddressIndexFromPath(walletModel.btcTestPath),
		ChainType.AllTest.id
	)
	val ltcAddress = Bip44Address(
		LTCWalletUtils.generateBase58Keypair(
			mnemonic,
			walletModel.ltcPath
		).address,
		GenerateMultiChainWallet.getAddressIndexFromPath(walletModel.ltcPath),
		ChainType.LTC.id
	)
	val bchAddress = Bip44Address(
		BCHWalletUtils.generateBCHKeyPair(
			mnemonic,
			walletModel.bchPath
		).address,
		GenerateMultiChainWallet.getAddressIndexFromPath(walletModel.bchPath),
		ChainType.BCH.id
	)
	val eosAddress = Bip44Address(
		EOSWalletUtils.generateKeyPair(
			mnemonic,
			walletModel.eosPath
		).address,
		GenerateMultiChainWallet.getAddressIndexFromPath(walletModel.eosPath),
		ChainType.EOS.id
	)
	WalletTable(
		id = walletModel.id,
		avatarID = walletModel.avatarID,
		name = walletModel.name,
		currentETHSeriesAddress = ethAddress.address,
		currentETCAddress = etcAddress.address,
		currentBTCAddress = btcAddress.address,
		currentBTCSeriesTestAddress = btcTestAddress.address,
		currentLTCAddress = ltcAddress.address,
		currentBCHAddress = bchAddress.address,
		currentEOSAddress = eosAddress.address,
		currentEOSAccountName = EOSDefaultAllChainName(eosAddress.address, eosAddress.address, eosAddress.address),
		ethAddresses = listOf(ethAddress),
		etcAddresses = listOf(etcAddress),
		btcAddresses = listOf(btcAddress),
		btcSeriesTestAddresses = listOf(btcTestAddress),
		ltcAddresses = listOf(ltcAddress),
		bchAddresses = listOf(bchAddress),
		eosAddresses = listOf(eosAddress),
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
		isWatchOnly = false,
		encryptMnemonic = walletModel.encryptMnemonic,
		encryptFingerPrinterKey = walletModel.encryptFingerPrinterKey,
		hasBackUpMnemonic = true
	).insert {  }
}

fun recoveryKeystoreWallet(walletModel: WalletModel) {

}

fun recoveryWatchOnlyWallet(walletModel: WalletModel) {

}