package io.goldstone.blockchain.crypto

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
	const val bitcoinAddressLength = 34 // 包含 `0x`
	const val contractAddressLength = 42 // 包含 `0x`
	const val taxHashLength = 66
	// GoldStone 业务约定的值
	const val ethContract = "0x60"
	const val etcContract = "0x61"
	const val ethMinGasLimit = 21000L
	const val confirmBlockNumber = 6
	const val ethDecimal = 18.0
	val chainID: (contract: String) -> String = {
		when {
			it.equals(CryptoValue.etcContract, true) -> Config.getETCCurrentChain()
			it.equals(CryptoValue.ethContract, true) -> Config.getCurrentChain()
			else -> Config.getCurrentChain()
		}
	}
	val isToken: (contract: String) -> Boolean = {
		(!it.equals(ethContract, true)
		 && !it.equals(etcContract, true))
	}
	val chainType: (path: String) -> Int = {
		it.replace("'", "").split("/")[2].toInt()
	}
	val isBtcTest: (chainType: Int) -> Boolean = {
		it == ChainType.BTCTest.id
	}
}

object CryptoSymbol {
	const val eth = "ETH"
	const val etc = "ETC"
	const val btc = "BTC"
	const val ltc = "LTC"
	const val erc = "ERC20"
}

object CryptoName {
	const val eth = "Ethereum"
	const val etc = "Ethereum Classic"
}

enum class ChainType(val id: Int) {
	BTC(0),
	BTCTest(1),
	LTC(2),
	ETH(60),
	ETC(61)
}

object DefaultPath {
	// Path
	const val ethPath = "m/44'/60'/0'/0/0"
	const val etcPath = "m/44'/61'/0'/0/0"
	const val btcPath = "m/44'/0'/0'/0/0"
	const val btcTestPath = "m/44'/1'/0'/0/0"
	// Header Value
	const val ethPathHeader = "m/44'/60'"
	const val btcPathHeader = "m/44'/0'"
	const val default = "0'/0/0"
}