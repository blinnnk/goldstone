package io.goldstone.blockchain.crypto.multichain

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.util.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.ethereum.getAddress
import io.goldstone.blockchain.crypto.keystore.generateETHSeriesAddress
import io.goldstone.blockchain.crypto.keystore.generateMnemonicVerifyKeyStore
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
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
		val targetID = SharedWallet.getMaxWalletID() + 1
		object : ConcurrentAsyncCombine() {
			val paths = DefaultPath.allPaths()
			override var asyncCount: Int = paths.size
			override val completeInUIThread: Boolean = false
			override fun doChildTask(index: Int) {
				context.apply {
					when (paths[index]) {
						// Ethereum
						DefaultPath.ethPath -> {
							val ethAddress = generateETHSeriesAddress(mnemonic, path.ethPath).getAddress()
							addresses.eth = Bip44Address(ethAddress, getAddressIndexFromPath(path.ethPath), ChainType.ETH.id)
							// 助记词钱包生成一个定制盐用于校验用户本地权限
							generateMnemonicVerifyKeyStore(
								targetID,
								mnemonic,
								CryptoValue.verifyPasswordSalt,
								password
							) { address, error ->
								if (address.isNotNull() && error.isNone()) completeMark()
							}
						}
						// Ethereum Classic
						DefaultPath.etcPath -> {
							val etcAddress = generateETHSeriesAddress(mnemonic, path.etcPath).getAddress()
							addresses.etc = Bip44Address(etcAddress, getAddressIndexFromPath(path.etcPath), ChainType.ETC.id)
							completeMark()
						}

						// Bitcoin
						DefaultPath.btcPath -> BTCWalletUtils.getBitcoinWalletByMnemonic(mnemonic, path.btcPath) { btcAddress, _ ->
							addresses.btc = Bip44Address(
								btcAddress,
								getAddressIndexFromPath(path.btcPath),
								ChainType.BTC.id
							)
							completeMark()
						}
						// BTC Test
						DefaultPath.testPath ->
							BTCWalletUtils.getBitcoinWalletByMnemonic(mnemonic, path.testPath) { btcSeriesTestAddress, _ ->
								addresses.btcSeriesTest = Bip44Address(
									btcSeriesTestAddress,
									getAddressIndexFromPath(path.testPath),
									ChainType.AllTest.id
								)
								completeMark()
							}
						// Litecoin
						DefaultPath.ltcPath -> LTCWalletUtils.generateBase58Keypair(mnemonic, path.ltcPath).let { ltcKeyPair ->
							addresses.ltc = Bip44Address(
								ltcKeyPair.address,
								getAddressIndexFromPath(path.ltcPath),
								ChainType.LTC.id
							)
							completeMark()
						}
						// Bitcoin Cash
						DefaultPath.bchPath -> BCHWalletUtils.generateBCHKeyPair(mnemonic, path.bchPath).let { bchKeyPair ->
							addresses.bch = Bip44Address(
								bchKeyPair.address,
								getAddressIndexFromPath(path.bchPath),
								ChainType.BCH.id
							)
							completeMark()
						}
						// Bitcoin Cash
						DefaultPath.eosPath -> EOSWalletUtils.generateKeyPair(mnemonic, path.eosPath).let { eosKeyPair ->
							addresses.eos = Bip44Address(
								eosKeyPair.address,
								getAddressIndexFromPath(path.eosPath),
								ChainType.EOS.id
							)
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