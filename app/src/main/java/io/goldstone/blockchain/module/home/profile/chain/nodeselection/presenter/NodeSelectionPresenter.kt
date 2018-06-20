package io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter

import com.blinnnk.extension.jump
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.ChainID
import io.goldstone.blockchain.common.value.ChainText
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.CryptoID
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.view.NodeSelectionFragment

/**
 * @date 2018/6/20 8:59 PM
 * @author KaySaith
 */
class NodeSelectionPresenter(
	override val fragment: NodeSelectionFragment
) : BasePresenter<NodeSelectionFragment>() {
	
	fun updateERC20TestChainID(nodeName: String) {
		Config.updateCurrentChain(ChainID.getChainIDByName(nodeName))
	}
	
	fun updateETCTestChainID(nodeName: String) {
		Config.updateETCCurrentChain(ChainID.getChainIDByName(nodeName))
	}
	
	fun updateDatabaseThenJump(isMainnet: Boolean) {
		AppConfigTable.updateChainStatus(isMainnet) {
			fragment.activity?.jump<SplashActivity>()
		}
	}
	
	fun getDefaultOrCurrentChainName(isMainnet: Boolean, type: Int): String {
		return if (isMainnet) {
			when (type) {
				CryptoID.eth -> {
					if (Config.getCurrentChain() != ChainID.Main.id) {
						ChainText.goldStoneMain
					} else {
						ChainID.getChainNameByID(Config.getCurrentChain())
					}
				}
				
				else -> {
					if (Config.getETCCurrentChain() != ChainID.ETCMain.id) {
						ChainText.etcMain
					} else {
						ChainID.getChainNameByID(Config.getETCCurrentChain())
					}
				}
			}
		} else {
			when (type) {
				CryptoID.eth -> {
					if (Config.getCurrentChain() == ChainID.Main.id) {
						ChainText.ropsten
					} else {
						ChainID.getChainNameByID(Config.getCurrentChain())
					}
				}
				
				else -> {
					if (Config.getETCCurrentChain() == ChainID.ETCMain.id) {
						ChainText.morden
					} else {
						ChainID.getChainNameByID(Config.getETCCurrentChain())
					}
				}
			}
		}
	}
}