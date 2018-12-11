package io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter

import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.multichain.node.ChainNodeTable
import io.goldstone.blockchain.crypto.multichain.node.ChainURL
import io.goldstone.blockchain.kernel.commontable.AppConfigTable
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.view.NodeSelectionFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @date 2018/6/20 8:59 PM
 * @author KaySaith
 */
class NodeSelectionPresenter(
	override val fragment: NodeSelectionFragment
) : BasePresenter<NodeSelectionFragment>() {

	/**
	 * `ChainID` 会重复使用导致获取 `Chain` 并不能准确, 所以切换 `Chain` 的时候存储
	 */
	fun updateERC20Chain(chainURL: ChainURL) {
		SharedChain.updateCurrentETH(chainURL)
	}

	fun updateETCChain(chainURL: ChainURL) {
		SharedChain.updateETCCurrent(chainURL)
	}

	fun updateBTCChain(chainURL: ChainURL) {
		SharedChain.updateBTCCurrent(chainURL)
	}

	fun updateBCHChain(chainURL: ChainURL) {
		SharedChain.updateBCHCurrent(chainURL)
	}

	fun updateLTCChain(chainURL: ChainURL) {
		SharedChain.updateLTCCurrent(chainURL)
	}

	fun updateEOSChain(chainURL: ChainURL) {
		SharedChain.updateEOSCurrent(chainURL)
	}

	companion object {
		fun setAllTestnet(isUIThread: Boolean = false, callback: () -> Unit) {
			GlobalScope.launch(Dispatchers.Default) {
				AppConfigTable.dao.updateChainStatus(false)
				val testnetList = ChainNodeTable.dao.getUsedTestnet()
				SharedValue.updateIsTestEnvironment(true)
				updateSharedChainInfo(testnetList)
				if (isUIThread) launchUI(callback) else callback()
			}
		}

		fun setAllMainnet(isUIThread: Boolean = false, callback: () -> Unit) {
			GlobalScope.launch(Dispatchers.Default) {
				AppConfigTable.dao.updateChainStatus(true)
				val mainnetList = ChainNodeTable.dao.getUsedMainnet()
				SharedValue.updateIsTestEnvironment(false)
				updateSharedChainInfo(mainnetList)
				if (isUIThread) launchUI(callback) else callback()
			}
		}

		private fun updateSharedChainInfo(nodeList: List<ChainNodeTable>) {
			nodeList.forEach {
				when {
					ChainType(it.chainType).isETH() -> SharedChain.updateCurrentETH(ChainURL(it))
					ChainType(it.chainType).isBTC() -> SharedChain.updateBTCCurrent(ChainURL(it))
					ChainType(it.chainType).isLTC() -> SharedChain.updateLTCCurrent(ChainURL(it))
					ChainType(it.chainType).isBCH() -> SharedChain.updateBCHCurrent(ChainURL(it))
					ChainType(it.chainType).isEOS() -> SharedChain.updateEOSCurrent(ChainURL(it))
					else -> SharedChain.updateETCCurrent(ChainURL(it))
				}
			}
		}
	}
}