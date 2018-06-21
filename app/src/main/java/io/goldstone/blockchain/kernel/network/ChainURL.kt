package io.goldstone.blockchain.kernel.network

import io.goldstone.blockchain.common.value.ChainText

/**
 * @date 2018/6/21 10:36 AM
 * @author KaySaith
 */
object ChainURL {
	
	val currentChain: (currentChainName: String) -> String = {
		when (it) {
			ChainText.goldStoneMain -> ChainURL.main
			ChainText.ropsten -> ChainURL.ropsten
			ChainText.rinkeby -> ChainURL.rinkeyb
			ChainText.kovan -> ChainURL.kovan
			ChainText.infuraRopsten -> ChainURL.infuraRopsten
			ChainText.infuraKovan -> ChainURL.infuraKovan
			ChainText.infuraRinkeby -> ChainURL.infuraRinkeby
			ChainText.infuraMain -> ChainURL.infuraMain
			else -> ChainURL.main
		}
	}
	val currentETCChain: (currentChainName: String) -> String = {
		when (it) {
			ChainText.etcMorden -> ChainURL.etcMorderTest
			ChainText.etcMainGasTracker -> ChainURL.etcMain
			else -> ChainURL.etcMain
		}
	}
	/** Chain Address */
	const val main = "https://eth-node-mainnet.goldstone.io/eth"
	private const val ropsten = "https://eth-node-ropsten.goldstone.io/eth"
	private const val kovan = "https://eth-node-kovan.goldstone.io/eth"
	private const val rinkeyb = "https://eth-node-rinkeby.goldstone.io/eth"
	/** ETC Chain Address */
	private const val etcMain = "https://web3.gastracker.io"
	private const val etcMorderTest = "https://web3.gastracker.io/morden"
	/** Infura Chain Address */
	private const val infuraMain = "https://mainnet.infura.io/zduaQszH37teJ0IEFUYG"
	private const val infuraRopsten = "https://ropsten.infura.io/zduaQszH37teJ0IEFUYG"
	private const val infuraKovan = "https://kovan.infura.io/zduaQszH37teJ0IEFUYG%7C"
	private const val infuraRinkeby = "https://rinkeby.infura.io/zduaQszH37teJ0IEFUYG"
}