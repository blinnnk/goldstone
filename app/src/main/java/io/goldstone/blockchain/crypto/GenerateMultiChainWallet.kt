package io.goldstone.blockchain.crypto

import android.content.Context
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blockchain.crypto.bitcoin.storeBase58PrivateKey

/**
 * @date 2018/7/14 12:20 PM
 * @author KaySaith
 */
object GenerateMultiChainWallet {
	
	fun create(
		context: Context,
		password: String,
		path: MultiChainPath = MultiChainPath(
			DefaultPath.ethPath,
			DefaultPath.etcPath,
			DefaultPath.btcPath,
			DefaultPath.btcTestPath
		),
		hold: (
			multiChainAddresses: MultiChainAddresses,
			mnemonic: String
		) -> Unit
	) {
		context.generateWallet(password, path.ethPath) { mnemonic, ethAddress ->
			context.getEthereumWalletByMnemonic(
				mnemonic,
				path.etcPath,
				password
			) { etcAddress ->
				BTCWalletUtils.getBitcoinWalletByMnemonic(
					mnemonic,
					path.btcPath
				) { btcAddress, secret ->
					context.storeBase58PrivateKey(secret, btcAddress, password, false)
					BTCWalletUtils.getBitcoinWalletByMnemonic(
						mnemonic,
						path.btcTestPath
					) { btcTestAddress, testSecret ->
						context.storeBase58PrivateKey(testSecret, btcTestAddress, password, true)
						hold(MultiChainAddresses(ethAddress, etcAddress, btcAddress, btcTestAddress), mnemonic)
					}
				}
			}
		}
	}
	
	fun import(
		context: Context,
		mnemonic: String,
		password: String,
		path: MultiChainPath,
		hold: (multiChainAddresses: MultiChainAddresses) -> Unit
	) {
		context.getEthereumWalletByMnemonic(mnemonic, path.ethPath, password) { ethAddress ->
			if (ethAddress.equals(ImportWalletText.existAddress, true)) {
				hold(MultiChainAddresses())
				return@getEthereumWalletByMnemonic
			}
			context.getEthereumWalletByMnemonic(
				mnemonic,
				path.etcPath,
				password
			) { etcAddress ->
				BTCWalletUtils.getBitcoinWalletByMnemonic(
					mnemonic,
					path.btcPath
				) { btcAddress, base58Privatekey ->
					// 存入 `Btc PrivateKey` 到 `KeyStore`
					context.storeBase58PrivateKey(base58Privatekey, btcAddress, password, false)
					BTCWalletUtils.getBitcoinWalletByMnemonic(
						mnemonic,
						path.btcTestPath
					) { btcTestAddress, btcTestBase58Privatekey ->
						// 存入 `BtcTest PrivateKey` 到 `KeyStore`
						context.storeBase58PrivateKey(btcTestBase58Privatekey, btcTestAddress, password, true)
						hold(MultiChainAddresses(ethAddress, etcAddress, btcAddress, btcTestAddress))
					}
				}
			}
		}
	}
}

data class MultiChainPath(
	val ethPath: String,
	val etcPath: String,
	val btcPath: String,
	val btcTestPath: String
) {
	
	constructor() : this(
		"",
		"",
		"",
		""
	)
}

data class MultiChainAddresses(
	val ethAddress: String,
	val etcAddress: String,
	val btcAddress: String,
	val btcTestAddress: String
) {
	
	constructor() : this(
		"",
		"",
		"",
		""
	)
}