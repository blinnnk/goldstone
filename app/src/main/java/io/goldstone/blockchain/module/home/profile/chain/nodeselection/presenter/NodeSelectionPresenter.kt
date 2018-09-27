package io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter

import com.blinnnk.util.TinyNumberUtils
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.ChainNameID
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.view.NodeSelectionFragment

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
	fun updateERC20ChainID(nodeName: String) {
		Config.updateCurrentChainName(nodeName)
		Config.updateCurrentChain(ChainID.getChainIDByName(nodeName))
		// 根据节点属性判断是否需要对 `JSON RPC` 加密或解密, `GoldStone`的节点请求全部加密了.
		Config.updateEncryptERCNodeRequest(checkIsEncryptERCNode(nodeName))
	}

	fun updateETCChainID(nodeName: String) {
		Config.updateETCCurrentChainName(nodeName)
		Config.updateETCCurrentChain(ChainID.getChainIDByName(nodeName))
		// 根据节点属性判断是否需要对 `JSON RPC` 加密或解密, `GoldStone`的节点请求全部加密了.
		Config.updateEncryptETCNodeRequest(checkIsEncryptETCNode(nodeName))
	}

	fun updateBTCChainID(nodeName: String) {
		Config.updateBTCCurrentChainName(nodeName)
		Config.updateBTCCurrentChain(ChainID.getChainIDByName(nodeName))
	}

	fun updateBCHChainID(nodeName: String) {
		Config.updateBCHCurrentChainName(nodeName)
		Config.updateBCHCurrentChain(ChainID.getChainIDByName(nodeName))
	}

	fun updateLTCChainID(nodeName: String) {
		Config.updateLTCCurrentChainName(nodeName)
		Config.updateLTCCurrentChain(ChainID.getChainIDByName(nodeName))
	}

	fun updateEOSChainID(nodeName: String) {
		Config.updateEOSCurrentChainName(nodeName)
		Config.updateEOSCurrentChain(ChainID.getChainIDByName(nodeName))
	}

	fun getCurrentChainName(isMainnet: Boolean, type: ChainType): String {
		return if (isMainnet) ChainType(type.id).getMainnetChainName()
		else ChainType(type.id).getTestnetChainName()

	}

	private fun checkIsEncryptERCNode(nodeName: String): Boolean {
		return TinyNumberUtils.allFalse(
			nodeName.contains("infura", true)
		)
	}

	private fun checkIsEncryptETCNode(nodeName: String): Boolean {
		return TinyNumberUtils.allFalse(
			nodeName.contains("gasTracker", true)
		)
	}

	companion object {
		fun setAllTestnet(callback: () -> Unit) {
			AppConfigTable.getAppConfig {
				it?.apply {
					AppConfigTable.updateChainStatus(false) {
						Config.updateIsTestEnvironment(true)
						Config.updateBTCCurrentChain(ChainID.btcTest)
						Config.updateLTCCurrentChain(ChainID.ltcTest)
						Config.updateBCHCurrentChain(ChainID.bchTest)
						Config.updateETCCurrentChain(ChainID.etcTest)
						Config.updateEOSCurrentChain(ChainID.eosTest)
						Config.updateCurrentChain(
							ChainID.getChainIDByName(
								ChainNameID.getChainNameByID(currentETHSeriesTestChainNameID)
							)
						)
						Config.updateETCCurrentChainName(
							ChainNameID.getChainNameByID(currentETCTestChainNameID)
						)
						Config.updateEOSCurrentChainName(
							ChainNameID.getChainNameByID(currentEOSTestChainNameID)
						)
						Config.updateCurrentChainName(
							ChainNameID.getChainNameByID(currentETHSeriesTestChainNameID)
						)
						Config.updateBTCCurrentChainName(
							ChainNameID.getChainNameByID(currentBTCTestChainNameID)
						)
						Config.updateBCHCurrentChainName(
							ChainNameID.getChainNameByID(currentBCHTestChainNameID)
						)
						Config.updateLTCCurrentChainName(
							ChainNameID.getChainNameByID(currentLTCTestChainNameID)
						)
						callback()
					}
				}
			}

		}

		fun setAllMainnet(callback: () -> Unit) {
			AppConfigTable.getAppConfig {
				it?.apply {
					AppConfigTable.updateChainStatus(true) {
						Config.updateIsTestEnvironment(false)
						Config.updateBTCCurrentChain(ChainID.btcMain)
						Config.updateLTCCurrentChain(ChainID.ltcMain)
						Config.updateBCHCurrentChain(ChainID.bchMain)
						Config.updateETCCurrentChain(ChainID.etcMain)
						Config.updateEOSCurrentChain(ChainID.eosMain)
						Config.updateCurrentChain(
							ChainID.getChainIDByName(
								ChainNameID.getChainNameByID(currentETHSeriesChainNameID)
							)
						)
						Config.updateETCCurrentChainName(
							ChainNameID.getChainNameByID(currentETCChainNameID)
						)
						Config.updateEOSCurrentChainName(
							ChainNameID.getChainNameByID(currentEOSChainNameID)
						)
						Config.updateCurrentChainName(ChainNameID.getChainNameByID(currentETHSeriesChainNameID))
						Config.updateBTCCurrentChainName(
							ChainNameID.getChainNameByID(currentBTCChainNameID)
						)
						Config.updateBCHCurrentChainName(
							ChainNameID.getChainNameByID(currentBCHChainNameID)
						)
						Config.updateLTCCurrentChainName(
							ChainNameID.getChainNameByID(currentLTCChainNameID)
						)
						callback()
					}
				}
			}
		}
	}
}