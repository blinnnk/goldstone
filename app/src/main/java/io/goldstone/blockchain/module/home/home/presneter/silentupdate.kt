package io.goldstone.blockchain.module.home.home.presneter

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.eosram.EOSResourceUtil
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.jetbrains.anko.doAsync

/**
 * @author KaySaith
 * @date  2018/09/26
 */
abstract class SilentUpdater {
	fun star() {
		doAsync {
			if (NetworkUtil.hasNetwork(GoldStoneAPI.context)) {
				updateUnknownDefaultToken()
				updateRAMUnitPrice()
				updateMyTokenCurrencyPrice()
				updateCPUUnitPrice()
				updateNETUnitPrice()
			}
		}
	}

	// 检查更新默认 `Token` 的 `Name` 信息
	private fun updateUnknownDefaultToken() {
		GoldStoneDataBase.database.defaultTokenDao().getAllTokens().filter {
			ChainID(it.chainID).isETHMain() && it.name.isEmpty()
		}.forEach {
			it.updateTokenNameFromChain()
		}
	}

	private fun updateRAMUnitPrice() {
		EOSResourceUtil.getRAMPrice(EOSUnit.KB, false) { priceInEOS, error ->
			if (!priceInEOS.isNull() && error.isNone()) {
				SharedValue.updateRAMUnitPrice(priceInEOS!!)
			} else LogUtil.error("SilentUpdater updateRAMUnitPrice", error)
		}
	}

	private fun updateCPUUnitPrice() {
		if (SharedAddress.getCurrentEOSAccount().isValid()) {
			EOSResourceUtil.getCPUPrice(SharedAddress.getCurrentEOSAccount()) { priceInEOS, error ->
				if (!priceInEOS.isNull() && error.isNone()) {
					SharedValue.updateCPUUnitPrice(priceInEOS!!)
				} else LogUtil.error("SilentUpdater updateRAMUnitPrice", error)
			}
		}
	}

	private fun updateNETUnitPrice() {
		if (SharedAddress.getCurrentEOSAccount().isValid()) {
			EOSResourceUtil.getNETPrice(SharedAddress.getCurrentEOSAccount()) { priceInEOS, error ->
				if (!priceInEOS.isNull() && error.isNone()) {
					SharedValue.updateNETUnitPrice(priceInEOS!!)
				} else LogUtil.error("SilentUpdater updateRAMUnitPrice", error)
			}
		}
	}

	private fun updateMyTokenCurrencyPrice() {
		MyTokenTable.getMyTokens(false) { myTokens ->
			GoldStoneAPI.getPriceByContractAddress(myTokens.map { it.contract }.toJsonArray(), {}) { newPrices ->
				object : ConcurrentAsyncCombine() {
					override var asyncCount: Int = newPrices.size
					override fun concurrentJobs() {
						newPrices.forEach {
							// 同时更新缓存里面的数据
							DefaultTokenTable.updateTokenPrice(TokenContract(it.contract), it.price)
							completeMark()
						}
					}

					override fun mergeCallBack() {}
				}.start()
			}
		}
	}
}