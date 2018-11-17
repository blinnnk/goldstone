package io.goldstone.blockchain.crypto.multichain

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.util.ConcurrentJobs
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blockchain.crypto.bitcoin.storeBase58PrivateKey
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.keystore.getEthereumWalletByMnemonic
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
import io.goldstone.blockchain.crypto.litecoin.storeLTCBase58PrivateKey
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.Bip44Address

/**
 * @date 2018/7/14 12:20 PM
 * @author KaySaith
 */
object GenerateMultiChainWallet {

	fun create(
		context: Context,
		password: String,
		@WorkerThread hold: (multiChainAddresses: ChainAddresses, mnemonic: String) -> Unit
	) {
		val path = ChainPath(
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
		path: ChainPath,
		@WorkerThread hold: (multiChainAddresses: ChainAddresses) -> Unit
	) {
		val addresses = ChainAddresses()
		object : ConcurrentJobs() {
			val paths = DefaultPath.allPaths()
			override var asyncCount: Int = paths.size
			override fun doChildJob(index: Int) {
				context.apply {
					when (paths[index]) {
						// Ethereum
						DefaultPath.ethPath -> getEthereumWalletByMnemonic(mnemonic, path.ethPath, password) { ethAddress, _ ->
							addresses.eth = Bip44Address(ethAddress!!, getAddressIndexFromPath(path.ethPath), ChainType.ETH.id)
							completeMark()
						}
						// Ethereum Classic
						DefaultPath.etcPath -> getEthereumWalletByMnemonic(
							mnemonic,
							path.etcPath,
							password
						) { etcAddress, _ ->
							addresses.etc = Bip44Address(etcAddress!!, getAddressIndexFromPath(path.etcPath), ChainType.ETC.id)
							completeMark()
						}
						// Bitcoin
						DefaultPath.btcPath -> BTCWalletUtils.getBitcoinWalletByMnemonic(mnemonic, path.btcPath) { btcAddress, base58Privatekey ->
							// 存入 `Btc PrivateKey` 到 `KeyStore`
							context.storeBase58PrivateKey(
								base58Privatekey,
								btcAddress,
								password,
								false
							)
							addresses.btc = Bip44Address(btcAddress, getAddressIndexFromPath(path.btcPath), ChainType.BTC.id)
							completeMark()
						}
						// BTC Test
						DefaultPath.testPath ->
							BTCWalletUtils.getBitcoinWalletByMnemonic(mnemonic, path.testPath) { btcSeriesTestAddress, btcTestBase58Privatekey ->
								// 存入 `BtcTest PrivateKey` 到 `KeyStore`
								context.storeBase58PrivateKey(
									btcTestBase58Privatekey,
									btcSeriesTestAddress,
									password,
									true
								)
								addresses.btcSeriesTest = Bip44Address(btcSeriesTestAddress, getAddressIndexFromPath(path.testPath), ChainType.AllTest.id)
								completeMark()
							}
						// Litecoin
						DefaultPath.ltcPath -> LTCWalletUtils.generateBase58Keypair(mnemonic, path.ltcPath).let { ltcKeyPair ->
							context.storeLTCBase58PrivateKey(
								ltcKeyPair.privateKey,
								ltcKeyPair.address,
								password
							)
							addresses.ltc = Bip44Address(ltcKeyPair.address, getAddressIndexFromPath(path.ltcPath), ChainType.LTC.id)
							completeMark()
						}
						// Bitcoin Cash
						DefaultPath.bchPath -> BCHWalletUtils.generateBCHKeyPair(mnemonic, path.bchPath).let { bchKeyPair ->
							context.storeBase58PrivateKey(
								bchKeyPair.privateKey,
								bchKeyPair.address,
								password,
								false
							)
							addresses.bch = Bip44Address(bchKeyPair.address, getAddressIndexFromPath(path.bchPath), ChainType.BCH.id)
							completeMark()
						}
						// Bitcoin Cash
						DefaultPath.eosPath -> EOSWalletUtils.generateKeyPair(mnemonic, path.eosPath).let { eosKeyPair ->
							// `EOS` 的 `Prefix` 使用的 是 `Bitcoin` 的 `Mainnet Prefix` 所以无论是否是测试网这里的 `isTestnet` 都传 `False`
							context.storeBase58PrivateKey(
								eosKeyPair.privateKey,
								eosKeyPair.address,
								password,
								false
							)
							addresses.eos = Bip44Address(eosKeyPair.address, getAddressIndexFromPath(path.eosPath), ChainType.EOS.id)
							completeMark()
						}
					}
				}
			}

			override fun mergeCallBack() = hold(addresses)
		}.start()
	}

	private fun getAddressIndexFromPath(path: String): Int {
		return if (path.isEmpty()) -1
		else path.substringAfterLast("/").toInt()
	}
}