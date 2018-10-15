package io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter

import com.blinnnk.util.TinyNumberUtils
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
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
		SharedChain.updateCurrentETHName(nodeName)
		SharedChain.updateCurrentETH(ChainID.getChainIDByName(nodeName))
		// 根据节点属性判断是否需要对 `JSON RPC` 加密或解密, `GoldStone`的节点请求全部加密了.
		SharedValue.updateEncryptERCNodeRequest(checkIsEncryptERCNode(nodeName))
	}

	fun updateETCChainID(nodeName: String) {
		SharedChain.updateETCCurrentName(nodeName)
		SharedChain.updateETCCurrent(ChainID.getChainIDByName(nodeName))
		// 根据节点属性判断是否需要对 `JSON RPC` 加密或解密, `GoldStone`的节点请求全部加密了.
		SharedValue.updateEncryptETCNodeRequest(checkIsEncryptETCNode(nodeName))
	}

	fun updateBTCChainID(nodeName: String) {
		SharedChain.updateBTCCurrentName(nodeName)
		SharedChain.updateBTCCurrent(ChainID.getChainIDByName(nodeName))
	}

	fun updateBCHChainID(nodeName: String) {
		SharedChain.updateBCHCurrentName(nodeName)
		SharedChain.updateBCHCurrent(ChainID.getChainIDByName(nodeName))
	}

	fun updateLTCChainID(nodeName: String) {
		SharedChain.updateLTCCurrentName(nodeName)
		SharedChain.updateLTCCurrent(ChainID.getChainIDByName(nodeName))
	}

	fun updateEOSChainID(nodeName: String) {
		SharedChain.updateEOSCurrentName(nodeName)
		SharedChain.updateEOSCurrent(ChainID.getChainIDByName(nodeName))
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
						SharedValue.updateIsTestEnvironment(true)
						SharedChain.updateBTCCurrent(ChainID.btcTest)
						SharedChain.updateLTCCurrent(ChainID.ltcTest)
						SharedChain.updateBCHCurrent(ChainID.bchTest)
						SharedChain.updateETCCurrent(ChainID.etcTest)
						SharedChain.updateEOSCurrent(ChainID.eosTest)
						SharedChain.updateCurrentETH(
							ChainID.getChainIDByName(ChainNameID.getChainNameByID(currentETHSeriesTestChainNameID))
						)
						SharedChain.updateETCCurrentName(
							ChainNameID.getChainNameByID(currentETCTestChainNameID)
						)
						SharedChain.updateEOSCurrentName(
							ChainNameID.getChainNameByID(currentEOSTestChainNameID)
						)
						SharedChain.updateCurrentETHName(
							ChainNameID.getChainNameByID(currentETHSeriesTestChainNameID)
						)
						SharedChain.updateBTCCurrentName(
							ChainNameID.getChainNameByID(currentBTCTestChainNameID)
						)
						SharedChain.updateBCHCurrentName(
							ChainNameID.getChainNameByID(currentBCHTestChainNameID)
						)
						SharedChain.updateLTCCurrentName(
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
						SharedValue.updateIsTestEnvironment(false)
						SharedChain.updateBTCCurrent(ChainID.btcMain)
						SharedChain.updateLTCCurrent(ChainID.ltcMain)
						SharedChain.updateBCHCurrent(ChainID.bchMain)
						SharedChain.updateETCCurrent(ChainID.etcMain)
						SharedChain.updateEOSCurrent(ChainID.eosMain)
						SharedChain.updateCurrentETH(
							ChainID.getChainIDByName(
								ChainNameID.getChainNameByID(currentETHSeriesChainNameID)
							)
						)
						SharedChain.updateETCCurrentName(
							ChainNameID.getChainNameByID(currentETCChainNameID)
						)
						SharedChain.updateEOSCurrentName(
							ChainNameID.getChainNameByID(currentEOSChainNameID)
						)
						SharedChain.updateCurrentETHName(ChainNameID.getChainNameByID(currentETHSeriesChainNameID))
						SharedChain.updateBTCCurrentName(
							ChainNameID.getChainNameByID(currentBTCChainNameID)
						)
						SharedChain.updateBCHCurrentName(
							ChainNameID.getChainNameByID(currentBCHChainNameID)
						)
						SharedChain.updateLTCCurrentName(
							ChainNameID.getChainNameByID(currentLTCChainNameID)
						)
						callback()
					}
				}
			}
		}
	}
}