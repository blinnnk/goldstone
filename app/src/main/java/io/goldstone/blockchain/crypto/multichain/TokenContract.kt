package io.goldstone.blockchain.crypto.multichain

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.utils.AddressUtils
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/14
 */
class TokenContract(val contract: String?, val symbol: String = "") : Serializable {
	constructor(contract: String?) : this(
		contract,
		when {
			contract.equals(TokenContract.etcContract, true) -> CoinSymbol.etc
			contract.equals(TokenContract.btcContract, true) -> CoinSymbol.pureBTCSymbol
			contract.equals(TokenContract.ltcContract, true) -> CoinSymbol.ltc
			contract.equals(TokenContract.bchContract, true) -> CoinSymbol. bch
			contract.equals(TokenContract.eosContract, true) -> CoinSymbol.eos
			contract.equals(TokenContract.ethContract, true) -> CoinSymbol.eth
			/** 以下两个通常用作燃气费的基础手续费的显示 `Symbol` */
			// 因为 `Ethereum` 的子合约地址的数量, 顾做 `Else` 判断
			contract?.length == CryptoValue.contractAddressLength -> CoinSymbol.eth
			// `EOS` 的 `Contract` 是 对应的 `CodeName` 例如 `eosio.token`
			else -> CoinSymbol.eos
		}
	)

	companion object {
		val ETH = TokenContract(TokenContract.ethContract)
		val ETC = TokenContract(TokenContract.etcContract)
		val BTC = TokenContract(TokenContract.btcContract)
		val LTC = TokenContract(TokenContract.ltcContract)
		val BCH = TokenContract(TokenContract.bchContract)
		val EOS = TokenContract(TokenContract.eosContract)
		// GoldStone 业务约定的值
		const val ethContract = "0x60"
		const val etcContract = "0x61"
		const val btcContract = "0x0"
		const val ltcContract = "0x2"
		const val bchContract = "0x145"
		const val eosContract = "0x194"
		@JvmStatic
		val isBTCSeries: (contract: String?) -> Boolean = { contract ->
			listOf(btcContract, ltcContract, bchContract).any { it.equals(contract, true) }
		}
	}
}

fun TokenContract?.orEmpty() = if (isNull()) TokenContract("") else this!!

fun TokenContract?.isEOS(): Boolean {
	return this?.contract.equals(TokenContract.eosContract, true)
}

fun TokenContract?.isEOSSeries(): Boolean {
	return isEOS() || isEOSToken()
}

fun TokenContract?.isEOSCode(): Boolean {
	return this?.contract.equals(EOSCodeName.EOSIO.value, true)
}

fun TokenContract?.isETH(): Boolean {
	return this?.contract.equals(TokenContract.ethContract, true)
}

fun TokenContract?.isBTC(): Boolean {
	return this?.contract.equals(TokenContract.btcContract, true)
}

fun TokenContract?.isLTC(): Boolean {
	return this?.contract.equals(TokenContract.ltcContract, true)
}

fun TokenContract?.isBCH(): Boolean {
	return this?.contract.equals(TokenContract.bchContract, true)
}

fun TokenContract?.isETC(): Boolean {
	return this?.contract.equals(TokenContract.etcContract, true)
}

// 在 `Ethereum` 或 `Ethereum Classic` 的链下使用
fun TokenContract?.isERC20Token(): Boolean {
	return (!isETH() && !isETC() && !isBTC() && !isBCH() && !isLTC() && !isEOSToken())
}

fun TokenContract?.isEOSToken(): Boolean {
	return (!isEOS() && !isETC() && !isBCH() && !isLTC() && !isETH() && !isBTC() && this?.contract?.length != CryptoValue.contractAddressLength)
}

fun TokenContract?.isBTCSeries(): Boolean {
	return TokenContract.isBTCSeries(this?.contract)
}

fun TokenContract?.getChainType(): ChainType {
	return when {
		this?.contract.equals(TokenContract.etcContract, true) -> ChainType.ETC
		this?.contract.equals(TokenContract.btcContract, true) -> ChainType.BTC
		this?.contract.equals(TokenContract.ltcContract, true) -> ChainType.LTC
		this?.contract.equals(TokenContract.bchContract, true) -> ChainType.BCH
		this?.contract.equals(TokenContract.eosContract, true) -> ChainType.EOS
		this?.contract.equals(TokenContract.ethContract, true) -> ChainType.ETH
		// 因为 `Ethereum` 的子合约地址的数量, 顾做 `Else` 判断
		this?.contract?.length == CryptoValue.contractAddressLength -> ChainType.ETH
		else -> ChainType.EOS // `EOS` 的 `Contract` 是 对应的 `CodeName` 例如 `eosio.token`
	}
}

fun TokenContract?.getSymbol(): CoinSymbol {
	return when {
		this?.contract.equals(TokenContract.etcContract, true) -> CoinSymbol.ETC
		this?.contract.equals(TokenContract.btcContract, true) -> CoinSymbol.BTC
		this?.contract.equals(TokenContract.ltcContract, true) -> CoinSymbol.LTC
		this?.contract.equals(TokenContract.bchContract, true) -> CoinSymbol.BCH
		this?.contract.equals(TokenContract.eosContract, true) -> CoinSymbol.EOS
		this?.contract.equals(TokenContract.ethContract, true) -> CoinSymbol.ETH
		/** 以下两个通常用作燃气费的基础手续费的显示 `Symbol` */
		// 因为 `Ethereum` 的子合约地址的数量, 顾做 `Else` 判断
		this?.contract?.length == CryptoValue.contractAddressLength -> CoinSymbol.ETH
		// `EOS` 的 `Contract` 是 对应的 `CodeName` 例如 `eosio.token`
		else -> CoinSymbol.EOS
	}
}

fun TokenContract?.getAddress(isEOSAccountName: Boolean = true): String {
	return when {
		TokenContract(this?.contract).isBTC() ->
			AddressUtils.getCurrentBTCAddress()
		TokenContract(this?.contract).isLTC() ->
			AddressUtils.getCurrentLTCAddress()
		TokenContract(this?.contract).isBCH() ->
			AddressUtils.getCurrentBCHAddress()
		TokenContract(this?.contract).isETC() ->
			SharedAddress.getCurrentETC()
		TokenContract(this?.contract).isEOSSeries() ->
			if (isEOSAccountName) SharedAddress.getCurrentEOSAccount().accountName
			else SharedAddress.getCurrentEOS()
		TokenContract(this?.contract).isETH() -> SharedAddress.getCurrentEthereum()
		this?.contract?.length == CryptoValue.contractAddressLength -> SharedAddress.getCurrentEthereum()
		else -> {
			if (isEOSAccountName) SharedAddress.getCurrentEOSAccount().accountName
			else SharedAddress.getCurrentEOS()
		}
	}
}

fun TokenContract?.getCurrentChainID(): ChainID {
	return when {
		this?.contract.equals(TokenContract.etcContract, true) -> SharedChain.getETCCurrent()
		this?.contract.equals(TokenContract.btcContract, true) -> SharedChain.getBTCCurrent()
		this?.contract.equals(TokenContract.ltcContract, true) -> SharedChain.getLTCCurrent()
		this?.contract.equals(TokenContract.bchContract, true) -> SharedChain.getBCHCurrent()
		this?.contract.equals(TokenContract.eosContract, true) -> SharedChain.getEOSCurrent()
		this?.contract.equals(TokenContract.ethContract, true) -> SharedChain.getCurrentETH()
		this?.contract?.length == CryptoValue.contractAddressLength -> SharedChain.getCurrentETH()
		else -> SharedChain.getEOSCurrent() // 因为 `Ethereum` 的子合约地址的数量, 顾做 `Else` 判断
	}
}

fun TokenContract?.getCurrentChainName(): String {
	return when {
		this?.contract.equals(TokenContract.etcContract, true) -> SharedChain.getETCCurrentName()
		this?.contract.equals(TokenContract.btcContract, true) -> SharedChain.getBTCCurrentName()
		this?.contract.equals(TokenContract.ltcContract, true) -> SharedChain.getLTCCurrentName()
		this?.contract.equals(TokenContract.bchContract, true) -> SharedChain.getBCHCurrentName()
		this?.contract.equals(TokenContract.eosContract, true) -> SharedChain.getEOSCurrentName()
		this?.contract.equals(TokenContract.ethContract, true) -> SharedChain.getCurrentETHName()
		this?.contract?.length == CryptoValue.contractAddressLength -> SharedChain.getCurrentETHName()
		else -> SharedChain.getEOSCurrentName()
	}
}

fun TokenContract?.getMainnetChainID(): String {
	return when {
		this?.contract.equals(TokenContract.etcContract, true) -> ChainID.etcMain
		this?.contract.equals(TokenContract.btcContract, true) -> ChainID.btcMain
		this?.contract.equals(TokenContract.ltcContract, true) -> ChainID.ltcMain
		this?.contract.equals(TokenContract.bchContract, true) -> ChainID.bchMain
		this?.contract.equals(TokenContract.eosContract, true) -> ChainID.eosMain
		this?.contract?.length == CryptoValue.contractAddressLength -> ChainID.ethMain
		else -> ChainID.eosMain
	}
}

fun TokenContract?.getDecimal(): Int? {
	return when {
		this?.contract.equals(TokenContract.etcContract, true) -> CryptoValue.etcDecimal
		this?.contract.equals(TokenContract.btcContract, true) -> CryptoValue.btcSeriesDecimal
		this?.contract.equals(TokenContract.ltcContract, true) -> CryptoValue.btcSeriesDecimal
		this?.contract.equals(TokenContract.bchContract, true) -> CryptoValue.btcSeriesDecimal
		this?.contract.equals(TokenContract.eosContract, true) -> CryptoValue.eosDecimal
		this?.contract.equals(TokenContract.ethContract, true) -> CryptoValue.ethDecimal
		else -> null // 因为 `Ethereum` 的子合约地址的数量, 顾做 `Else` 判断
	}
}