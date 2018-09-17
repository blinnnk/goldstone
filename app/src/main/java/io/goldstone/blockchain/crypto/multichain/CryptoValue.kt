package io.goldstone.blockchain.crypto.multichain

import com.blinnnk.util.TinyNumberUtils


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
	const val ethDecimal = 18
	const val etcDecimal = 18
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
}