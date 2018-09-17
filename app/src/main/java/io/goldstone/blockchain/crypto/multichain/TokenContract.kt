package io.goldstone.blockchain.crypto.multichain

import io.goldstone.blockchain.common.utils.AddressUtils
import io.goldstone.blockchain.common.value.Config
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/14
 */
class TokenContract(val contract: String?) : Serializable {
	fun isEOS(): Boolean {
		return contract.equals(eosContract, true)
	}

	fun isETH(): Boolean {
		return contract.equals(ethContract, true)
	}

	fun isBTC(): Boolean {
		return contract.equals(btcContract, true)
	}

	fun isLTC(): Boolean {
		return contract.equals(ltcContract, true)
	}

	fun isBCH(): Boolean {
		return contract.equals(bchContract, true)
	}

	fun isETC(): Boolean {
		return contract.equals(etcContract, true)
	}

	// 在 `Ethereum` 或 `Ethereum Classic` 的链下使用
	fun isERC20Token(): Boolean {
		return (!TokenContract(contract).isETH() && !TokenContract(contract).isETC())
	}

	fun getCurrentChainID(): String {
		return when {
			contract.equals(etcContract, true) -> Config.getETCCurrentChain()
			contract.equals(btcContract, true) -> Config.getBTCCurrentChain()
			contract.equals(ltcContract, true) -> Config.getLTCCurrentChain()
			contract.equals(bchContract, true) -> Config.getBCHCurrentChain()
			contract.equals(eosContract, true) -> Config.getEOSCurrentChain()
			contract.equals(ethContract, true) -> Config.getCurrentChain()
			else -> Config.getCurrentChain() // 因为 `Ethereum` 的子合约地址的数量, 顾做 `Else` 判断
		}
	}

	fun getCurrentChainName(): String {
		return when {
			contract.equals(etcContract, true) -> Config.getETCCurrentChainName()
			contract.equals(btcContract, true) -> Config.getBTCCurrentChainName()
			contract.equals(ltcContract, true) -> Config.getLTCCurrentChainName()
			contract.equals(bchContract, true) -> Config.getBCHCurrentChainName()
			contract.equals(eosContract, true) -> Config.getEOSCurrentChainName()
			contract.equals(ethContract, true) -> Config.getCurrentChainName()
			else -> Config.getCurrentChainName() // 因为 `Ethereum` 的子合约地址的数量, 顾做 `Else` 判断
		}
	}

	fun getMainnetChainID(): String {
		return when {
			contract.equals(etcContract, true) -> ChainID.etcMain
			contract.equals(btcContract, true) -> ChainID.btcMain
			contract.equals(ltcContract, true) -> ChainID.ltcMain
			contract.equals(bchContract, true) -> ChainID.bchMain
			contract.equals(eosContract, true) -> ChainID.eosMain
			else -> ChainID.ethMain
		}
	}

	fun getCurrentChainType(): MultiChainType {
		return when {
			contract.equals(etcContract, true) -> MultiChainType.ETC
			contract.equals(btcContract, true) -> MultiChainType.BTC
			contract.equals(ltcContract, true) -> MultiChainType.LTC
			contract.equals(bchContract, true) -> MultiChainType.BCH
			contract.equals(eosContract, true) -> MultiChainType.EOS
			contract.equals(ethContract, true) -> MultiChainType.ETH
			else -> MultiChainType.ETH // 因为 `Ethereum` 的子合约地址的数量, 顾做 `Else` 判断
		}
	}

	fun getAddress(isEOSAccountName: Boolean = true): String {
		return when {
			TokenContract(contract).isBTC() ->
				AddressUtils.getCurrentBTCAddress()
			TokenContract(contract).isLTC() ->
				AddressUtils.getCurrentLTCAddress()
			TokenContract(contract).isBCH() ->
				AddressUtils.getCurrentBCHAddress()
			TokenContract(contract).isETC() ->
				Config.getCurrentETCAddress()
			TokenContract(contract).isEOS() ->
				if (isEOSAccountName) Config.getCurrentEOSName()
				else Config.getCurrentEOSAddress()
			else ->
				Config.getCurrentEthereumAddress()
		}
	}

	fun getDecimal(): Int? {
		return when {
			contract.equals(etcContract, true) -> CryptoValue.etcDecimal
			contract.equals(btcContract, true) -> CryptoValue.btcSeriesDecimal
			contract.equals(ltcContract, true) -> CryptoValue.btcSeriesDecimal
			contract.equals(bchContract, true) -> CryptoValue.btcSeriesDecimal
			contract.equals(eosContract, true) -> CryptoValue.eosDecimal
			contract.equals(ethContract, true) -> CryptoValue.ethDecimal
			else -> null // 因为 `Ethereum` 的子合约地址的数量, 顾做 `Else` 判断
		}
	}

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
	}
}