package io.goldstone.blockchain.crypto

import android.content.Context
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blockchain.crypto.bitcoin.MultiChainAddresses
import io.goldstone.blockchain.crypto.bitcoin.MultiChainPath
import io.goldstone.blockchain.crypto.bitcoin.storeBase58PrivateKey
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
import io.goldstone.blockchain.crypto.litecoin.storeLTCBase58PrivateKey

/**
 * @date 2018/7/14 12:20 PM
 * @author KaySaith
 */
object GenerateMultiChainWallet {

	fun create(
		context: Context,
		password: String,
		hold: (
			multiChainAddresses: MultiChainAddresses,
			mnemonic: String
		) -> Unit
	) {
		val path = MultiChainPath(
			DefaultPath.ethPath,
			DefaultPath.etcPath,
			DefaultPath.btcPath,
			DefaultPath.testPath,
			DefaultPath.ltcPath,
			DefaultPath.bchPath,
			DefaultPath.eosPath
		)
		val mnemonic = Mnemonic.generateMnemonic()
		import(context, mnemonic, password, path) {
			hold(it, mnemonic)
		}
	}

	fun import(
		context: Context,
		mnemonic: String,
		password: String,
		path: MultiChainPath,
		hold: (multiChainAddresses: MultiChainAddresses) -> Unit
	) {
		val addresses = MultiChainAddresses()
		object : ConcurrentAsyncCombine() {
			override var asyncCount: Int = DefaultPath.allPaths().size
			override fun concurrentJobs() {
				context.apply {
					// Ethereum
					getEthereumWalletByMnemonic(mnemonic, path.ethPath, password) { ethAddress ->
						addresses.ethAddress = ethAddress
						completeMark()
					}
					// Ethereum Classic
					getEthereumWalletByMnemonic(
						mnemonic,
						path.etcPath,
						password
					) { etcAddress ->
						addresses.etcAddress = etcAddress
						completeMark()
					}
					// Bitcoin
					BTCWalletUtils.getBitcoinWalletByMnemonic(
						mnemonic,
						path.btcPath
					) { btcAddress, base58Privatekey ->
						// 存入 `Btc PrivateKey` 到 `KeyStore`
						context.storeBase58PrivateKey(
							base58Privatekey,
							btcAddress,
							password,
							false,
							false
						)
						addresses.btcAddress = btcAddress
						completeMark()
					}
					BTCWalletUtils.getBitcoinWalletByMnemonic(
						mnemonic,
						path.testPath
					) { btcSeriesTestAddress, btcTestBase58Privatekey ->
						// 存入 `BtcTest PrivateKey` 到 `KeyStore`
						context.storeBase58PrivateKey(
							btcTestBase58Privatekey,
							btcSeriesTestAddress,
							password,
							true,
							false
						)
						addresses.btcSeriesTestAddress = btcSeriesTestAddress
						completeMark()
					}
					// Litecoin
					LTCWalletUtils.generateBase58Keypair(
						mnemonic,
						path.ltcPath
					).let { ltcKeyPair ->
						context.storeLTCBase58PrivateKey(
							ltcKeyPair.privateKey,
							ltcKeyPair.address,
							password,
							false
						)
						addresses.ltcAddress = ltcKeyPair.address
						completeMark()
					}
					// Bitcoin Cash
					BCHWalletUtils.generateBCHKeyPair(
						mnemonic,
						path.bchPath
					).let { bchKeyPair ->
						context.storeBase58PrivateKey(
							bchKeyPair.privateKey,
							bchKeyPair.address,
							password,
							false,
							false
						)
						addresses.bchAddress = bchKeyPair.address
						completeMark()
					}
					// Bitcoin Cash
					EOSWalletUtils.generateKeyPair(
						mnemonic,
						path.bchPath
					).let { eosKeyPair ->
						// `EOS` 的 `Prefix` 使用的 是 `Bitcoin` 的 `Mainnet Prefix` 所以无论是否是测试网这里的 `isTestnet` 都传 `False`
						context.storeBase58PrivateKey(
							eosKeyPair.privateKey,
							eosKeyPair.address,
							password,
							false,
							false
						)
						addresses.eosAddress = eosKeyPair.address
						completeMark()
					}
				}
			}

			override fun mergeCallBack() {
				hold(addresses)
			}
		}.start()
	}
}