package io.goldstone.blockchain.crypto

import io.goldstone.blockchain.common.utils.TinyNumberUtils
import io.goldstone.blockchain.common.value.Config

/**
 * @date 08/04/2018 12:23 AM
 * @author KaySaith
 */
object SolidityCode {

	const val contractTransfer = "0xa9059cbb"
	const val ethTransfer = "0x"
	const val ethCall = "0x95d89b41000000000000000000000000"
	const val getTokenBalance = "0x70a08231000000000000000000000000"
	const val getTotalSupply =
		"0x18160ddd0000000000000000000000000000000000000000000000000000000000000005"
	const val getDecimal =
		"0x313ce5670000000000000000000000000000000000000000000000000000000000000005"
	const val getTokenName =
		"0x06fdde030000000000000000000000000000000000000000000000000000000000000005"
	const val logTransferFilter = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef"
}

object CryptoValue {
	const val bip39AddressLength = 42 // 包含 `0x`
	private const val bitcoinAddressLength = 34
	const val bitcoinAddressClassicLength = 33
	const val eosAddressLength = 53
	const val bitcoinPrivateKeyLength = 52
	const val contractAddressLength = 42 // 包含 `0x`
	const val taxHashLength = 66
	// Bitcoin 转账前测算 `SignedSize` 需要用到私钥, 这里随便写一个紧用于提前预估 `SignedSize`
	const val signedSecret = "cRKRm6mvfVrxDoStKhRETVZ91gcN13EBgCKhgCkVRw2DaWSByN94"
	const val signedBTCMainnetSecret = "KxJQeWFnRuTv7HtECL85ytaxQAyFxspzWs9RuY1Fa1oqXFGh6gJC"
	const val ltcMainnetSignedSecret = "T99eF7JUK83YfnCBqcdsUP7pBPeLqYLmAW477PHdRX67g82MgQLk"
	const val keystoreFilename = "keystore"
	const val singleChainFilename = "singleChain"
	// GoldStone 业务约定的值
	const val ethContract = "0x60"
	const val etcContract = "0x61"
	const val btcContract = "0x0"
	const val ltcContract = "0x2"
	const val bchContract = "0x145"
	const val eosContract = "0x194"
	const val ethMinGasLimit = 21000L
	const val confirmBlockNumber = 6
	const val ethDecimal = 18.0
	const val eosDecimal = 4
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
			isSingleChainWallet -> CryptoValue.singleChainFile(walletAddress)
			else -> CryptoValue.keystoreFilename
		}
	}
	val chainID: (contract: String) -> String = {
		when {
			it.equals(CryptoValue.etcContract, true) -> Config.getETCCurrentChain()
			it.equals(CryptoValue.ethContract, true) -> Config.getCurrentChain()
			it.equals(CryptoValue.ltcContract, true) -> Config.getLTCCurrentChain()
			it.equals(CryptoValue.btcContract, true) -> Config.getBTCCurrentChain()
			it.equals(CryptoValue.bchContract, true) -> Config.getBCHCurrentChain()
			else -> Config.getCurrentChain()
		}
	}
	val isToken: (contract: String) -> Boolean = {
		(!it.equals(ethContract, true)
			&& !it.equals(etcContract, true))
	}
	val pathCoinType: (path: String) -> Int = {
		it.replace("'", "").split("/")[2].toInt()
	}

	val isBTCSeriesAddress: (address: String) -> Boolean = {
		CryptoValue.isBitcoinAddressLength(it) || it.contains(":")
	}
	// 比特的 `Bip44` 的比特币测试地址的  `CoinType` 为 `1`
	val isBTCTest: (pathCoinType: Int) -> Boolean = {
		it == 1
	}

	enum class PrivateKeyType(val content: String) {
		ETHERCAndETC("ETH, ERC20 And ETC"),
		BTC("BTC"),
		BCH("BCH"),
		BTCTest("BTC Test"),
		EOS("EOS"),
		LTC("LTC");

		companion object {
			fun getTypeByContent(content: String): PrivateKeyType {
				return when (content) {
					ETHERCAndETC.content -> ETHERCAndETC
					LTC.content -> LTC
					BCH.content -> BCH
					BTC.content -> BTC
					EOS.content -> EOS
					else -> BTCTest
				}
			}
		}
	}
}

object CryptoSymbol {
	const val eth = "ETH"
	const val etc = "ETC"
	const val pureBTCSymbol = "BTC"
	const val ltc = "LTC"
	const val bch = "BCH"
	const val erc = "ERC"
	const val eos = "EOS"
	val btc: () -> String = {
		if (Config.getYingYongBaoInReviewStatus()) "B.C." else "BTC"
	}

	val allBTCSeriesSymbol: () -> List<String> = {
		listOf(ltc, btc(), bch)
	}

	fun isBTCSeriesSymbol(symbol: String?): Boolean {
		return allBTCSeriesSymbol().any { it.equals(symbol, true) }
	}

	fun updateSymbolIfInReview(symbol: String, isTest: Boolean = false): String {
		return if (
			symbol.contains("BTC", true) &&
			Config.getYingYongBaoInReviewStatus()
		) "B.C." + if (isTest) " Test" else ""
		else symbol
	}

	fun updateNameIfInReview(name: String): String {
		return if (
			name.contains("Bitcoin", true) &&
			Config.getYingYongBaoInReviewStatus()
		) "Bitc."
		else name
	}
}

object CryptoName {
	const val eth = "Ethereum"
	const val etc = "Ethereum Classic"
	const val btc = "Bitcoin"
	const val ltc = "Litecoin"
	const val bch = "Bitcoin Cash"
	const val eos = "Eos.io"
	val allChainName = listOf(
		etc.replace(" ", ""),
		eth,
		btc,
		ltc,
		bch.replace(" ", ""),
		eos
	)

	fun getBTCSeriesChainIDByName(name: String): String? {
		return listOf(
			Pair(ltc, Config.getLTCCurrentChain()),
			Pair(bch, Config.getBCHCurrentChain()),
			Pair(btc, Config.getBTCCurrentChain())
		).firstOrNull {
			it.first
				.replace(" ", "")
				.equals(name, true)
		}?.second
	}

	fun getBTCSeriesContractByChainName(name: String): String? {
		return listOf(
			Pair(ltc, CryptoValue.ltcContract),
			Pair(bch, CryptoValue.bchContract),
			Pair(btc, CryptoValue.btcContract)
		).firstOrNull {
			it.first
				.replace(" ", "")
				.equals(name, true)
		}?.second
	}
}

enum class ChainType(val id: Int) {
	BTC(0),
	AllTest(1),
	LTC(2),
	BCH(145),
	EOS(194),
	ETH(60),
	ETC(61),
	ERC(100); // 需要调大不然可能会和自然 `Type` 冲突

	companion object {
		private fun getAllBTCSeriesType(): List<Int> =
			listOf(LTC.id, AllTest.id, BTC.id, BCH.id)

		fun isBTCSeriesChainType(id: Int): Boolean =
			getAllBTCSeriesType().any { it == id }

		fun getChainTypeBySymbol(symbol: String?): Int = when (symbol) {
			CryptoSymbol.btc() -> BTC.id
			CryptoSymbol.ltc -> LTC.id
			CryptoSymbol.eth -> ETH.id
			CryptoSymbol.etc -> ETC.id
			CryptoSymbol.bch -> BCH.id
			CryptoSymbol.eos -> EOS.id
			else -> ETH.id
		}
	}
}

object DefaultPath {
	// Path
	const val ethPath = "m/44'/60'/0'/0/0"
	const val etcPath = "m/44'/61'/0'/0/0"
	const val btcPath = "m/44'/0'/0'/0/0"
	const val testPath = "m/44'/1'/0'/0/0"
	const val ltcPath = "m/44'/2'/0'/0/0"
	const val bchPath = "m/44'/145'/0'/0/0"
	const val eosPath = "m/44'/194'/0'/0/0"
	val allPaths: () -> List<String> = {
		listOf(
			ethPath,
			etcPath,
			btcPath,
			testPath,
			ltcPath,
			bchPath,
			eosPath
		)
	}
	// Header Value
	const val ethPathHeader = "m/44'/60'/"
	const val etcPathHeader = "m/44'/61'/"
	const val btcPathHeader = "m/44'/0'/"
	const val testPathHeader = "m/44'/1'/"
	const val ltcPathHeader = "m/44'/2'/"
	const val bchPathHeader = "m/44'/145'/"
	const val eosPathHeader = "m/44'/194'/"
	const val default = "0'/0/0"
}