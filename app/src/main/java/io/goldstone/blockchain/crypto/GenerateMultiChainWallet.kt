package io.goldstone.blockchain.crypto

import android.content.Context
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils

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
				BTCUtils.getBitcoinWalletByMnemonic(
					mnemonic,
					path.btcPath
				) { btcAddress, _ ->
					BTCUtils.getBitcoinWalletByMnemonic(
						mnemonic,
						path.btcTestPath
					) { btcTestAddress, _ ->
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
		path: MultiChainPath = MultiChainPath(
			DefaultPath.ethPath,
			DefaultPath.etcPath,
			DefaultPath.btcPath,
			DefaultPath.btcTestPath
		),
		hold: (
			multiChainAddresses: MultiChainAddresses
		) -> Unit
	) {
		context.getEthereumWalletByMnemonic(mnemonic, path.ethPath, password) { ethAddress ->
			context.getEthereumWalletByMnemonic(
				mnemonic,
				path.etcPath,
				password
			) { etcAddress ->
				BTCUtils.getBitcoinWalletByMnemonic(
					mnemonic,
					path.btcPath
				) { btcAddress, _ ->
					BTCUtils.getBitcoinWalletByMnemonic(
						mnemonic,
						path.btcTestPath
					) { btcTestAddress, _ ->
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
)

data class MultiChainAddresses(
	val ethAddress: String,
	val etcAddress: String,
	val btcAddress: String,
	val btcTestAddress: String
)