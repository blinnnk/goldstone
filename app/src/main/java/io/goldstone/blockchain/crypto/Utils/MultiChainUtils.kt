package io.goldstone.blockchain.crypto.utils

import io.goldstone.blockchain.common.utils.AddressUtils
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.bitcoin.AddressType
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.bitcoin.MultiChainAddresses
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.ethereum.Address
import io.goldstone.blockchain.crypto.ethereum.ECKeyPair
import io.goldstone.blockchain.crypto.ethereum.getAddress
import io.goldstone.blockchain.crypto.ethereum.isValid
import io.goldstone.blockchain.crypto.ethereum.walletfile.WalletUtil
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
import io.goldstone.blockchain.crypto.litecoin.LitecoinNetParams
import io.goldstone.blockchain.crypto.multichain.CryptoSymbol
import io.goldstone.blockchain.crypto.multichain.PrivateKeyType
import org.bitcoinj.core.DumpedPrivateKey
import org.bitcoinj.core.ECKey
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/07
 */

object MultiChainUtils {

	fun getMultiChainAddressesByRootKey(rootKey: BigInteger): MultiChainAddresses {
		val ethAddress = ECKeyPair.create(rootKey).getAddress().prepend0xPrefix()
		val mainnet = MainNetParams.get()
		val btcMainnetAddress = ECKey.fromPrivate(rootKey).toAddress(mainnet).toBase58()
		val testnet = TestNet3Params.get()
		val allBtcSeriesTestAddress = ECKey.fromPrivate(rootKey).toAddress(testnet).toBase58()
		val ltcAddress = ECKey.fromPrivate(rootKey).toAddress(LitecoinNetParams()).toBase58()
		val bchAddress = BCHWalletUtils.getAddressByPrivateKey(rootKey)
		val eosAddress = EOSWalletUtils.generateKeyPairByPrivateKey(rootKey).address
		return MultiChainAddresses(
			ethAddress,
			ethAddress,
			btcMainnetAddress,
			allBtcSeriesTestAddress,
			ltcAddress,
			bchAddress,
			eosAddress
		)
	}

	fun getRootPrivateKey(privateKey: String): BigInteger {
		return when (detectPrivateKeyType(privateKey)) {
			PrivateKeyType.ETHERCAndETC -> ECKeyPair.getPrivateKey(privateKey)
			PrivateKeyType.BTCEOSAndBCH ->
				DumpedPrivateKey.fromBase58(MainNetParams.get(), privateKey).key.privKey
			PrivateKeyType.LTC ->
				DumpedPrivateKey.fromBase58(LitecoinNetParams(), privateKey).key.privKey
			else -> // 如果都不是那么是测试 `BTC` 系列网络的私钥
				DumpedPrivateKey.fromBase58(TestNet3Params.get(), privateKey).key.privKey
		}
	}

	private fun detectPrivateKeyType(privateKey: String): PrivateKeyType? {
		return when {
			WalletUtil.isValidPrivateKey(privateKey) -> PrivateKeyType.ETHERCAndETC
			BTCUtils.isValidMainnetPrivateKey(privateKey) -> PrivateKeyType.BTCEOSAndBCH
			BTCUtils.isValidTestnetPrivateKey(privateKey) -> PrivateKeyType.AllBTCSeriesTest
			LTCWalletUtils.isValidPrivateKey(privateKey) -> PrivateKeyType.LTC
			else -> null
		}
	}

	fun isValidMultiChainAddress(address: String, symbol: String): AddressType? {
		return when {
			Address(address).isValid() -> AddressType.ETHERCOrETC
			BTCUtils.isValidMainnetAddress(address)
				&& symbol.equals(CryptoSymbol.btc(), true) -> AddressType.BTC
			BTCUtils.isValidTestnetAddress(address) -> {
				when {
					symbol.equals(CryptoSymbol.bch, true) -> {
						if (Config.isTestEnvironment()) AddressType.BCH
						else null
					}
					symbol.equals(CryptoSymbol.ltc, true) -> {
						if (Config.isTestEnvironment()) AddressType.LTC
						else null
					}
					else -> AddressType.BTCSeriesTest
				}
			}
			LTCWalletUtils.isValidAddress(address) -> AddressType.LTC
			BCHWalletUtils.isValidAddress(address) -> AddressType.BCH
			else -> null
		}
	}

	fun getAddressBySymbol(symbol: String?): String {
		return when {
			symbol.equals(CryptoSymbol.btc(), true) ->
				AddressUtils.getCurrentBTCAddress()
			symbol.equals(CryptoSymbol.ltc, true) ->
				AddressUtils.getCurrentLTCAddress()
			symbol.equals(CryptoSymbol.bch, true) ->
				AddressUtils.getCurrentBCHAddress()
			symbol.equals(CryptoSymbol.etc, true) ->
				Config.getCurrentETCAddress()
			symbol.equals(CryptoSymbol.eos, true) ->
				Config.getCurrentEOSAddress()
			else ->
				Config.getCurrentEthereumAddress()
		}
	}
}