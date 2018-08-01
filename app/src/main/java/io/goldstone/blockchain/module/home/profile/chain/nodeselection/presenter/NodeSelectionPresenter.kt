package io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter

import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.TinyNumberUtils
import io.goldstone.blockchain.common.value.ChainID
import io.goldstone.blockchain.common.value.ChainText
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.ChainType
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
	 * `ChainID` 会重复使用导致获取 `ChainName` 并不能准确, 所以切换 `Chain` 的时候存储 `NodeName`
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
	
	fun getDefaultOrCurrentChainName(isMainnet: Boolean, type: ChainType): String {
		return if (isMainnet) {
			when (type) {
				ChainType.ETH -> {
					if (Config.getCurrentChain() != ChainID.Main.id) {
						ChainText.infuraMain
					} else {
						Config.getCurrentChainName()
					}
				}
				
				ChainType.BTC -> {
					ChainText.btcMain
				}
				
				else -> {
					if (Config.getETCCurrentChain() != ChainID.ETCMain.id) {
						ChainText.etcMainGasTracker
					} else {
						Config.getETCCurrentChainName()
					}
				}
			}
		} else {
			when (type) {
				ChainType.ETH -> {
					if (Config.getCurrentChain() == ChainID.Main.id) {
						ChainText.infuraRopsten
					} else {
						Config.getCurrentChainName()
					}
				}
				
				ChainType.BTC -> {
					ChainText.btcTest
				}
				
				else -> {
					if (Config.getETCCurrentChain() == ChainID.ETCMain.id) {
						ChainText.etcMorden
					} else {
						Config.getETCCurrentChainName()
					}
				}
			}
		}
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
						Config.updateBTCCurrentChain(ChainID.BTCTest.id)
						Config.updateETCCurrentChain(ChainID.ETCTest.id)
						Config.updateCurrentChain(
							ChainID.getChainIDByName(currentETHERC20AndETCTestChainName)
						)
						Config.updateETCCurrentChainName(currentETCTestChainName)
						Config.updateCurrentChainName(currentETHERC20AndETCTestChainName)
						Config.updateBTCCurrentChainName(currentBTCTestChainName)
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
						Config.updateBTCCurrentChain(ChainID.BTCTest.id)
						Config.updateETCCurrentChain(ChainID.ETCTest.id)
						Config.updateCurrentChain(
							ChainID.getChainIDByName(currentETHERC20AndETCChainName)
						)
						Config.updateETCCurrentChainName(currentETCChainName)
						Config.updateCurrentChainName(currentETHERC20AndETCChainName)
						Config.updateBTCCurrentChainName(currentBTCChainName)
						callback()
					}
				}
			}
		}
	}
}