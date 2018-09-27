package io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.present

import android.os.Handler
import com.blinnnk.util.TinyNumberUtils
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.module.home.quotation.tradermemory.RAMTradePresenterManager
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.model.RAMMarketHeaderModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.model.RAMMarketModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.view.TraderMemoryDetailFragment

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
class TraderMemoryDetailPresenter(override val fragment: TraderMemoryDetailFragment)
	: BasePresenter<TraderMemoryDetailFragment>() {

	fun merchandiseRAM() {

	}

	fun getCurrentChainName(isMainnet: Boolean, type: ChainType): String {
		return if (isMainnet) ChainType(type.id).getMainnetChainName()
		else ChainType(type.id).getTestnetChainName()

	}

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

	var ramMarketModel = RAMMarketModel(RAMMarketHeaderModel())
	
	private val refreshRunnable = Runnable {
		RAMTradePresenterManager.refreshData("")
		postRefresh()
	}
	
	private val refreshHandler = Handler()
	
	private fun postRefresh() {
		refreshHandler.postDelayed(refreshRunnable, 1 *1000)
	}
	
	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		refreshHandler.removeCallbacks(refreshRunnable)
	}
	
	override fun onFragmentResume() {
		super.onFragmentResume()
		refreshHandler.removeCallbacks(refreshRunnable)
		postRefresh()
	}
	
	override fun onFragmentPause() {
		super.onFragmentPause()
		refreshHandler.removeCallbacks(refreshRunnable)
	}
	
	fun onHiddenChanged(hidden: Boolean) {
		if (hidden) {
			refreshHandler.removeCallbacks(refreshRunnable)
		} else {
			refreshHandler.removeCallbacks(refreshRunnable)
			postRefresh()
		}
	}
	
	
	
	
	
	


}