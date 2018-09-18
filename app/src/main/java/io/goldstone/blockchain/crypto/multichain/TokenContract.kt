package io.goldstone.blockchain.crypto.multichain

import io.goldstone.blockchain.common.utils.AddressUtils
import io.goldstone.blockchain.common.value.Config
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/14
 */
class TokenContract(val contract: String?) : Serializable {
	companion object {
		fun getETH(): TokenContract = TokenContract(TokenContract.ethContract)
		fun getETC(): TokenContract = TokenContract(TokenContract.etcContract)
		fun getBTC(): TokenContract = TokenContract(TokenContract.btcContract)
		fun getLTC(): TokenContract = TokenContract(TokenContract.ltcContract)
		fun getBCH(): TokenContract = TokenContract(TokenContract.bchContract)
		fun getEOS(): TokenContract = TokenContract(TokenContract.eosContract)
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

fun TokenContract?.orEmpty() = TokenContract("")

fun TokenContract?.isEOS(): Boolean {
	return this?.contract.equals(TokenContract.eosContract, true)
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
	return (!isETH() && !isETC())
}

fun TokenContract?.isBTCSeries(): Boolean {
	return TokenContract.isBTCSeries(this?.contract)
}

fun TokenContract?.getChainType(): MultiChainType {
	return when {
		this?.contract.equals(TokenContract.etcContract, true) -> MultiChainType.ETC
		this?.contract.equals(TokenContract.btcContract, true) -> MultiChainType.BTC
		this?.contract.equals(TokenContract.ltcContract, true) -> MultiChainType.LTC
		this?.contract.equals(TokenContract.bchContract, true) -> MultiChainType.BCH
		this?.contract.equals(TokenContract.eosContract, true) -> MultiChainType.EOS
		this?.contract.equals(TokenContract.ethContract, true) -> MultiChainType.ETH
		else -> MultiChainType.ETH // 因为 `Ethereum` 的子合约地址的数量, 顾做 `Else` 判断
	}
}

fun TokenContract?.getSymbol(): CoinSymbol {
	return when {
		this?.contract.equals(TokenContract.etcContract, true) -> CoinSymbol.getETC()
		this?.contract.equals(TokenContract.btcContract, true) -> CoinSymbol.getBTC()
		this?.contract.equals(TokenContract.ltcContract, true) -> CoinSymbol.getLTC()
		this?.contract.equals(TokenContract.bchContract, true) -> CoinSymbol.getBCH()
		this?.contract.equals(TokenContract.eosContract, true) -> CoinSymbol.getEOS()
		this?.contract.equals(TokenContract.ethContract, true) -> CoinSymbol.getETH()
		else -> CoinSymbol.getETH() // 因为 `Ethereum` 的子合约地址的数量, 顾做 `Else` 判断
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
			Config.getCurrentETCAddress()
		TokenContract(this?.contract).isEOS() ->
			if (isEOSAccountName) Config.getCurrentEOSName()
			else Config.getCurrentEOSAddress()
		else ->
			Config.getCurrentEthereumAddress()
	}
}

fun TokenContract?.getCurrentChainID(): ChainID {
	return when {
		this?.contract.equals(TokenContract.etcContract, true) -> Config.getETCCurrentChain()
		this?.contract.equals(TokenContract.btcContract, true) -> Config.getBTCCurrentChain()
		this?.contract.equals(TokenContract.ltcContract, true) -> Config.getLTCCurrentChain()
		this?.contract.equals(TokenContract.bchContract, true) -> Config.getBCHCurrentChain()
		this?.contract.equals(TokenContract.eosContract, true) -> Config.getEOSCurrentChain()
		this?.contract.equals(TokenContract.ethContract, true) -> Config.getCurrentChain()
		else -> Config.getCurrentChain() // 因为 `Ethereum` 的子合约地址的数量, 顾做 `Else` 判断
	}
}

fun TokenContract?.getCurrentChainName(): String {
	return when {
		this?.contract.equals(TokenContract.etcContract, true) -> Config.getETCCurrentChainName()
		this?.contract.equals(TokenContract.btcContract, true) -> Config.getBTCCurrentChainName()
		this?.contract.equals(TokenContract.ltcContract, true) -> Config.getLTCCurrentChainName()
		this?.contract.equals(TokenContract.bchContract, true) -> Config.getBCHCurrentChainName()
		this?.contract.equals(TokenContract.eosContract, true) -> Config.getEOSCurrentChainName()
		this?.contract.equals(TokenContract.ethContract, true) -> Config.getCurrentChainName()
		else -> Config.getCurrentChainName() // 因为 `Ethereum` 的子合约地址的数量, 顾做 `Else` 判断
	}
}

fun TokenContract?.getMainnetChainID(): String {
	return when {
		this?.contract.equals(TokenContract.etcContract, true) -> ChainID.etcMain
		this?.contract.equals(TokenContract.btcContract, true) -> ChainID.btcMain
		this?.contract.equals(TokenContract.ltcContract, true) -> ChainID.ltcMain
		this?.contract.equals(TokenContract.bchContract, true) -> ChainID.bchMain
		this?.contract.equals(TokenContract.eosContract, true) -> ChainID.eosMain
		else -> ChainID.ethMain
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