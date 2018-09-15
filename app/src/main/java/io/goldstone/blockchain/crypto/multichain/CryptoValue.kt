package io.goldstone.blockchain.crypto.multichain

import com.blinnnk.util.TinyNumberUtils
import io.goldstone.blockchain.common.value.Config


/**
 * @author KaySaith
 * @date  2018/09/07
 */

object CryptoValue {
	const val bip39AddressLength = 42 // 包含 `0x`
	private const val bitcoinAddressLength = 34
	const val bitcoinAddressClassicLength = 33
	const val eosAddressLength = 53
	const val contractAddressLength = 42 // 包含 `0x`
	const val taxHashLength = 66
	// Bitcoin 转账前测算 `SignedSize` 需要用到私钥, 这里随便写一个仅用于提前预估 `SignedSize`
	const val signedSecret = "cRKRm6mvfVrxDoStKhRETVZ91gcN13EBgCKhgCkVRw2DaWSByN94"
	const val signedBTCMainnetSecret = "KxJQeWFnRuTv7HtECL85ytaxQAyFxspzWs9RuY1Fa1oqXFGh6gJC"
	const val ltcMainnetSignedSecret = "T99eF7JUK83YfnCBqcdsUP7pBPeLqYLmAW477PHdRX67g82MgQLk"
	const val keystoreFilename = "keystore"
	const val singleChainFilename = "singleChain"
	const val ethMinGasLimit = 21000L
	const val confirmBlockNumber = 6
	const val ethDecimal = 18.0
	const val eosDecimal = 4
	const val btcSeriesDecimal = 8
	val singleChainFile: (btcAddress: String) -> String = {
		singleChainFilename + it
	}
	val isBitcoinAddressLength: (address: String) -> Boolean = {
		TinyNumberUtils.hasTrue(
			it.length == bitcoinAddressLength,
			it.length == bitcoinAddressClassicLength
		)
	}
	val filename: (
		walletAddress: String,
		isBTCSeriesWallet: Boolean,
		isSingleChainWallet: Boolean
	) -> String = { walletAddress, isBTCSeriesWallet, isSingleChainWallet ->
		when {
			isBTCSeriesWallet && !isSingleChainWallet -> walletAddress
			isSingleChainWallet -> singleChainFile(walletAddress)
			else -> keystoreFilename
		}
	}
	val chainID: (contract: String) -> String = {
		when {
			it.equals(TokenContract.etcContract, true) -> Config.getETCCurrentChain()
			it.equals(TokenContract.ethContract, true) -> Config.getCurrentChain()
			it.equals(TokenContract.ltcContract, true) -> Config.getLTCCurrentChain()
			it.equals(TokenContract.btcContract, true) -> Config.getBTCCurrentChain()
			it.equals(TokenContract.bchContract, true) -> Config.getBCHCurrentChain()
			it.equals(TokenContract.eosContract, true) -> Config.getEOSCurrentChain()
			else -> Config.getCurrentChain()
		}
	}
	val isToken: (contract: String) -> Boolean = {
		(!it.equals(TokenContract.ethContract, true)
			&& !it.equals(TokenContract.etcContract, true))
	}
	val pathCoinType: (path: String) -> Int = {
		it.replace("'", "").split("/")[2].toInt()
	}

	val isBTCSeriesAddress: (address: String) -> Boolean = {
		isBitcoinAddressLength(it) || it.contains(":")
	}
	// 比特的 `Bip44` 的比特币测试地址的  `CoinType` 为 `1`
	val isBTCTest: (pathCoinType: Int) -> Boolean = {
		it == 1
	}
}