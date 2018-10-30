package io.goldstone.blockchain.module.home.home.presneter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
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
				updateLocalDefaultTokens()
				updateUnknownDefaultToken()
				updateRAMUnitPrice()
				updateMyTokenCurrencyPrice()
				updateCPUUnitPrice()
				updateNETUnitPrice()
				checkAvailableEOSTokenList()
				updateNodeData()
			}
		}
	}

	private fun checkAvailableEOSTokenList() {
		val account = SharedAddress.getCurrentEOSAccount()
		if (!account.isValid(false)) return
		EOSAPI.getEOSTokenList(
			SharedChain.getEOSCurrent().chainID,
			account
		) { tokenList, error ->
			if (!tokenList.isNull() && error.isNone()) {
				// 拉取潜在资产的 `Icon Url`
				GoldStoneAPI.getIconURL(tokenList!!) { tokenIcons, getIconError ->
					if (!tokenIcons.isNull() && getIconError.isNone()) {
						tokenList.forEach { contract ->
							// 插入 `DefaultToken`
							DefaultTokenTable(
								contract.contract.orEmpty(),
								contract.symbol,
								contract.decimal.orElse(4),
								ChainID.EOS, // 这个接口只服务主网下的 `Token`
								tokenIcons!!.getByTokenContract(contract.orEmpty())?.url.orEmpty(),
								true
							).preventDuplicateInsert()
							// 插入 `MyTokenTable`
							MyTokenTable(
								0,
								account.accountName,
								SharedAddress.getCurrentEOS(),
								contract.symbol,
								0.0,
								contract.contract.orEmpty(),
								ChainID.EOS.id
							).preventDuplicateInsert()
						}
					}
				}
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
			}
		}
	}

	private fun updateCPUUnitPrice() {
		if (SharedAddress.getCurrentEOSAccount().isValid()) {
			EOSResourceUtil.getCPUPrice(SharedAddress.getCurrentEOSAccount()) { priceInEOS, error ->
				if (!priceInEOS.isNull() && error.isNone()) {
					SharedValue.updateCPUUnitPrice(priceInEOS!!)
				}
			}
		}
	}

	private fun updateNETUnitPrice() {
		if (SharedAddress.getCurrentEOSAccount().isValid()) {
			EOSResourceUtil.getNETPrice(SharedAddress.getCurrentEOSAccount()) { priceInEOS, error ->
				if (!priceInEOS.isNull() && error.isNone()) {
					SharedValue.updateNETUnitPrice(priceInEOS!!)
				}
			}
		}
	}

	private fun updateNodeData() {
		// 拉取网络数据, 更新本地的选中状态后覆盖本地数据库 TODO 需要增加 MD5 校验减少网络请求
		GoldStoneAPI.getChainNodes { serverNodes, error ->
			val nodeDao = GoldStoneDataBase.database.chainNodeDao()
			if (!serverNodes.isNull() && error.isNone() && serverNodes!!.isNotEmpty()) {
				val localNodes = nodeDao.getAll()
				serverNodes.map { node ->
					node.apply {
						isUsed = localNodes.find {
							it.url.equals(node.url, true)
						}?.isUsed.orElse(1)
					}
				}.let {
					nodeDao.insertAll(it)
				}
			}
		}
	}

	private fun updateMyTokenCurrencyPrice() {
		MyTokenTable.getMyTokens(false) { myTokens ->
			GoldStoneAPI.getPriceByContractAddress(
				// `EOS` 的 `Token` 价格在下面的方法从第三方获取, 这里过滤掉 `EOS` 的 `Token`
				myTokens.asSequence().filterNot {
					ChainID(it.chainID).isEOSMain() && !CoinSymbol(it.symbol).isEOS()
				}.map {
					"{\"address\":\"${it.contract}\",\"symbol\":\"${it.symbol}\"}"
				}.toList(),
				false
			) { newPrices, error ->
				if (!newPrices.isNull() && error.isNone()) {
					newPrices!!.forEach {
						DefaultTokenTable.updateTokenPrice(TokenContract(it.contract, it.symbol, null), it.price)
					}
				}
			}
			// 检查 EOS 的 Token 价格, 从 NewDex 提供的接口
			myTokens.filter { ChainID(it.chainID).isEOSMain() }.forEach { token ->
				EOSAPI.updateLocalTokenPrice(TokenContract(token.contract, token.symbol, null))
			}
		}
	}

	private fun updateLocalDefaultTokens() {
		GoldStoneAPI.getDefaultTokens { serverTokens, error ->
			if (!serverTokens.isNull() && !serverTokens!!.isEmpty() && error.isNone()) {
				val localTokens =
					GoldStoneDataBase.database.defaultTokenDao().getAllTokens()
				// 开一个线程更新图片
				serverTokens.updateLocalTokenIcon(localTokens.toArrayList())
				// 移除掉一样的数据
				serverTokens.filterNot { server ->
					localTokens.any { local ->
						local.chainID.equals(server.chainID, true)
							&& local.contract.equals(server.contract, true)
					}
				}.apply {
					GoldStoneDataBase.database.defaultTokenDao().insertAll(this)
				}
			}
		}
	}

	private fun List<DefaultTokenTable>.updateLocalTokenIcon(localTokens: ArrayList<DefaultTokenTable>) {
		doAsync {
			val unManuallyData = localTokens.filter { it.serverTokenID.isNotEmpty() }
			filter { server ->
				unManuallyData.find {
					it.serverTokenID.equals(server.serverTokenID, true)
				}?.let {
					// 如果本地的非手动添加的数据没有存在于最新从 `Server` 拉取下来的意味着已经被 `CMS` 移除
					GoldStoneDataBase.database.defaultTokenDao().update(it.apply { isDefault = false })
				}

				localTokens.any { local ->
					local.chainID.equals(server.chainID, true)
						&& local.contract.equals(server.contract, true)
				}
			}.apply {
				if (isEmpty()) return@doAsync
				forEach { server ->
					GoldStoneDataBase.database.defaultTokenDao().apply {
						getTokenByContract(server.contract, server.symbol, server.chainID)?.let {
							update(it.apply {
								iconUrl = server.iconUrl
								isDefault = server.isDefault
								forceShow = server.forceShow
							})
						}
					}
				}
			}
		}
	}
}