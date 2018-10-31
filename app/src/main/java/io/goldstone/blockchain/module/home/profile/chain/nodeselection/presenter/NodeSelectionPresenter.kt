package io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter

import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.multichain.node.ChainNodeTable
import io.goldstone.blockchain.crypto.multichain.node.ChainURL
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.view.NodeSelectionFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

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
	fun updateERC20ChainID(chainURL: ChainURL) {
		SharedChain.updateCurrentETH(chainURL)
	}

	fun updateETCChainID(chainURL: ChainURL) {
		SharedChain.updateETCCurrent(chainURL)
	}

	fun updateBTCChainID(chainURL: ChainURL) {
		SharedChain.updateBTCCurrent(chainURL)
	}

	fun updateBCHChainID(chainURL: ChainURL) {
		SharedChain.updateBCHCurrent(chainURL)
	}

	fun updateLTCChainID(chainURL: ChainURL) {
		SharedChain.updateLTCCurrent(chainURL)
	}

	fun updateEOSChainID(chainURL: ChainURL) {
		SharedChain.updateEOSCurrent(chainURL)
	}

	companion object {
		fun setAllTestnet(isMainnet: Boolean = false, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().updateChainStatus(false)
				val testnetList = GoldStoneDataBase.database.chainNodeDao().getUsedTestnet()
				SharedValue.updateIsTestEnvironment(true)
				updateSharedChainInfo(testnetList)
				if (isMainnet) uiThread { callback() } else callback()
			}
		}

		fun setAllMainnet(isMainnet: Boolean = false, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.appConfigDao().updateChainStatus(true)
				val mainnetList = GoldStoneDataBase.database.chainNodeDao().getUsedMainnet()
				SharedValue.updateIsTestEnvironment(false)
				updateSharedChainInfo(mainnetList)
				if (isMainnet) uiThread { callback() } else callback()
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