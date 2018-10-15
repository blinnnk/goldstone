package io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.present

import android.os.Handler
import com.blinnnk.util.TinyNumberUtils
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.module.home.quotation.tradermemory.RAMTradeRefreshEvent
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

	private val refreshRunnable = Runnable {
		RAMTradeRefreshEvent.refreshData("")
		postRefresh()
	}

	private val candlRefreshRunbale = Runnable {
		RAMTradeRefreshEvent.refreshData("candle")
		postRefreshCandle()
	}

	private val refreshHandler = Handler()

	private fun postRefresh() {
		refreshHandler.postDelayed(refreshRunnable, 20 * 1000)
	}

	private fun postRefreshCandle() {
		refreshHandler.postDelayed(candlRefreshRunbale, 60 * 1000)
	}

	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		refreshHandler.removeCallbacks(refreshRunnable)
	}

	override fun onFragmentResume() {
		super.onFragmentResume()
		refreshHandler.removeCallbacks(refreshRunnable)
		postRefresh()
		refreshHandler.removeCallbacks(candlRefreshRunbale)
		postRefreshCandle()
	}

	override fun onFragmentPause() {
		super.onFragmentPause()
		refreshHandler.removeCallbacksAndMessages(null)
	}

	fun onHiddenChanged(hidden: Boolean) {
		refreshHandler.removeCallbacksAndMessages(null)
		if (hidden) {
		} else {
			postRefresh()
			postRefreshCandle()
		}
	}


}