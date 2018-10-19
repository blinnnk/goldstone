package io.goldstone.blockchain.crypto.multichain

import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.utils.AddressUtils
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/14
 */
data class TokenContract(
	@SerializedName("code")
	val contract: String?,
	@SerializedName("symbol")
	val symbol: String
) : Serializable {
	constructor(contract: String) : this(
		contract,
		when {
			contract.equals(TokenContract.etcContract, true) -> CoinSymbol.etc
			contract.equals(TokenContract.btcContract, true) -> CoinSymbol.pureBTCSymbol
			contract.equals(TokenContract.ltcContract, true) -> CoinSymbol.ltc
			contract.equals(TokenContract.bchContract, true) -> CoinSymbol.bch
			contract.equals(TokenContract.eosContract, true) -> CoinSymbol.eos
			contract.equals(TokenContract.ethContract, true) -> CoinSymbol.eth
			/** 以下两个通常用作燃气费的基础手续费的显示 `Symbol` */
			// 因为 `Ethereum` 的子合约地址的数量, 顾做 `Else` 判断
			contract.length == CryptoValue.contractAddressLength -> CoinSymbol.eth
			// `EOS` 的 `Contract` 是 对应的 `CodeName` 例如 `eosio.token`
			else -> CoinSymbol.eos
		}
	)

	companion object {
		val ETH = TokenContract(TokenContract.ethContract, CoinSymbol.ETH.symbol!!)
		val ETC = TokenContract(TokenContract.etcContract, CoinSymbol.ETC.symbol!!)
		val BTC = TokenContract(TokenContract.btcContract, CoinSymbol.BTC.symbol!!)
		val LTC = TokenContract(TokenContract.ltcContract, CoinSymbol.LTC.symbol!!)
		val BCH = TokenContract(TokenContract.bchContract, CoinSymbol.BCH.symbol!!)
		val EOS = TokenContract(TokenContract.eosContract, CoinSymbol.EOS.symbol!!)
		// GoldStone 业务约定的值
		const val ethContract = "0x60"
		const val etcContract = "0x61"
		const val btcContract = "0x0"
		const val ltcContract = "0x2"
		const val bchContract = "0x145"
		const val eosContract = "eosio.token"
		@JvmStatic
		val isBTCSeries: (contract: String?) -> Boolean = { contract ->
			listOf(btcContract, ltcContract, bchContract).any { it.equals(contract, true) }
		}
	}
}

fun TokenContract?.orEmpty() = if (isNull()) TokenContract("") else this!!

fun TokenContract?.isEOS(): Boolean {
	return this?.contract.equals(TokenContract.eosContract, true) && this?.symbol.equals(CoinSymbol.EOS.symbol, true)
}

fun TokenContract?.isEOSSeries(): Boolean {
	return isEOS() || isEOSToken()
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
	return !isEOS() && !isETC() && !isBCH() && !isLTC() && !isETH() && !isBTC() && this?.contract?.length != CryptoValue.contractAddressLength && this?.contract?.length.orZero() > 0
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
		this.isEOSSeries() -> ChainType.EOS
		this?.contract.equals(TokenContract.ethContract, true) -> ChainType.ETH
		// 因为 `Ethereum` 的子合约地址的数量, 顾做 `Else` 判断
		this?.contract?.length == CryptoValue.contractAddressLength -> ChainType.ETH
		else -> ChainType.EOS // `EOS` 的 `Contract` 是 对应的 `CodeName` 例如 `eosio.token`
	}
}

// 这个方法是用来获取链 `Symbol` 用的, 所以 `Token` 的 `Symbol` 都对应到链的核心 `Symbol`
fun TokenContract?.getSymbol(): CoinSymbol {
	return when {
		this?.contract.equals(TokenContract.etcContract, true) -> CoinSymbol.ETC
		this?.contract.equals(TokenContract.btcContract, true) -> CoinSymbol.BTC
		this?.contract.equals(TokenContract.ltcContract, true) -> CoinSymbol.LTC
		this?.contract.equals(TokenContract.bchContract, true) -> CoinSymbol.BCH
		this.isEOSSeries() -> CoinSymbol.EOS
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
		TokenContract(this?.contract.orEmpty()).isBTC() ->
			AddressUtils.getCurrentBTCAddress()
		TokenContract(this?.contract.orEmpty()).isLTC() ->
			AddressUtils.getCurrentLTCAddress()
		TokenContract(this?.contract.orEmpty()).isBCH() ->
			AddressUtils.getCurrentBCHAddress()
		TokenContract(this?.contract.orEmpty()).isETC() ->
			SharedAddress.getCurrentETC()
		TokenContract(this?.contract.orEmpty()).isEOSSeries() ->
			if (isEOSAccountName) SharedAddress.getCurrentEOSAccount().accountName
			else SharedAddress.getCurrentEOS()
		TokenContract(this?.contract.orEmpty()).isETH() -> SharedAddress.getCurrentEthereum()
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
		this.isEOSSeries() -> SharedChain.getEOSCurrent()
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
		this.isEOSSeries() -> SharedChain.getEOSCurrentName()
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
		this.isEOSSeries() -> ChainID.eosMain
		this?.contract?.length == CryptoValue.contractAddressLength -> ChainID.ethMain
		else -> ChainID.eosMain
	}
}