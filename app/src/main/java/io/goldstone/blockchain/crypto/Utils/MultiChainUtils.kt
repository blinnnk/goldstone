package io.goldstone.blockchain.crypto.utils

import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.ethereum.Address
import io.goldstone.blockchain.crypto.ethereum.ECKeyPair
import io.goldstone.blockchain.crypto.ethereum.getAddress
import io.goldstone.blockchain.crypto.ethereum.isValid
import io.goldstone.blockchain.crypto.ethereum.walletfile.WalletUtil
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
import io.goldstone.blockchain.crypto.litecoin.LitecoinNetParams
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.Bip44Address
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

	fun getMultiChainAddressesByRootKey(rootKey: BigInteger): ChainAddresses {
		val ethAddress = ECKeyPair.create(rootKey).getAddress().prepend0xPrefix()
		val mainnet = MainNetParams.get()
		val btcMainnetAddress = ECKey.fromPrivate(rootKey).toAddress(mainnet).toBase58()
		val testnet = TestNet3Params.get()
		val allBtcSeriesTestAddress = ECKey.fromPrivate(rootKey).toAddress(testnet).toBase58()
		val ltcAddress = ECKey.fromPrivate(rootKey).toAddress(LitecoinNetParams()).toBase58()
		val bchAddress = BCHWalletUtils.getAddressByPrivateKey(rootKey)
		val eosAddress = EOSWalletUtils.generateKeyPairByPrivateKey(rootKey).address
		// 根私钥创建的钱包不是 `Bip44` 所以不用传递 `Address Index`
		return ChainAddresses(
			Bip44Address(ethAddress, ChainType.ETH.id),
			Bip44Address(ethAddress, ChainType.ETC.id),
			Bip44Address(btcMainnetAddress, ChainType.BTC.id),
			Bip44Address(allBtcSeriesTestAddress, ChainType.AllTest.id),
			Bip44Address(ltcAddress, ChainType.LTC.id),
			Bip44Address(bchAddress, ChainType.BCH.id),
			Bip44Address(eosAddress, ChainType.EOS.id)
		)
	}

	fun getRootPrivateKey(privateKey: String): BigInteger {
		return when (detectPrivateKeyType(privateKey)) {
			PrivateKeyType.ETHSeries -> ECKeyPair.getPrivateKey(privateKey)
			PrivateKeyType.BTCEOSAndBCH ->
				DumpedPrivateKey.fromBase58(MainNetParams.get(), privateKey).key.privKey
			PrivateKeyType.LTC ->
				DumpedPrivateKey.fromBase58(LitecoinNetParams(), privateKey).key.privKey
			else -> // 如果都不是那么是测试 `BTC` 系列网络的私钥
				DumpedPrivateKey.fromBase58(TestNet3Params.get(), privateKey).key.privKey
		}
	}

	fun detectPrivateKeyType(privateKey: String): PrivateKeyType? {
		return when {
			WalletUtil.isValidPrivateKey(privateKey) -> PrivateKeyType.ETHSeries
			BTCUtils.isValidMainnetPrivateKey(privateKey) -> PrivateKeyType.BTCEOSAndBCH
			BTCUtils.isValidTestnetPrivateKey(privateKey) -> PrivateKeyType.AllBTCSeriesTest
			LTCWalletUtils.isValidPrivateKey(privateKey) -> PrivateKeyType.LTC
			else -> null
		}
	}

	fun isValidMultiChainAddress(address: String, symbol: CoinSymbol): AddressType? {
		return when {
			Address(address).isValid() -> AddressType.ETHSeries
			BTCUtils.isValidMainnetAddress(address)
				&& symbol.isBTC() -> AddressType.BTC
			BTCUtils.isValidTestnetAddress(address) -> {
				when {
					symbol.isBCH() -> {
						if (SharedValue.isTestEnvironment()) AddressType.BCH
						else null
					}
					symbol.isLTC() -> {
						if (SharedValue.isTestEnvironment()) AddressType.LTC
						else null
					}
					else -> AddressType.BTCSeriesTest
				}
			}
			LTCWalletUtils.isValidAddress(address) -> AddressType.LTC
			BCHWalletUtils.isValidAddress(address) && !EOSWalletUtils.isValidAddress(address) -> AddressType.BCH
			EOSWalletUtils.isValidAddress(address) -> AddressType.EOS
			EOSAccount(address).isValid(false) -> AddressType.EOSAccountName
			else -> null
		}
	}
}