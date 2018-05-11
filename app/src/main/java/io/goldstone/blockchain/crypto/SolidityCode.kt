package io.goldstone.blockchain.crypto

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
	const val contractAddressLength = 42 // 包含 `0x`
	const val taxHashLength = 66
}

object CryptoSymbol {
	const val eth = "ETH"
}