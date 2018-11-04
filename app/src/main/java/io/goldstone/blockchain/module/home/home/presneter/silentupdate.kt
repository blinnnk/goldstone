package io.goldstone.blockchain.module.home.home.presneter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.eosram.EOSResourceUtil
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import kotlinx.coroutines.experimental.*

/**
 * @author KaySaith
 * @date  2018/09/26
 */
abstract class SilentUpdater {
	fun star() {
		launch {
			withContext(CommonPool, CoroutineStart.LAZY) {
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
		EOSAPI.getEOSTokenList(SharedChain.getEOSCurrent().chainID, account) { tokenList, error ->
			// 拉取潜在资产的 `Icon Url`
			if (tokenList != null && error.isNone()) GoldStoneAPI.getIconURL(tokenList) { tokenIcons, getIconError ->
				val myTokenDao = GoldStoneDataBase.database.myTokenDao()
				if (tokenIcons != null && getIconError.isNone()) tokenList.forEach { contract ->
					//  这个接口只服务主网下的 `Token` 插入 `DefaultToken`
					DefaultTokenTable(
						contract,
						tokenIcons.get(contract.orEmpty())?.url.orEmpty()
					).preventDuplicateInsert()

					val targetToken = myTokenDao.getTokenByContractAndAddress(
						contract.contract.orEmpty(),
						contract.symbol,
						account.accountName,
						ChainID.EOS.id
					)
					// 有可能用户本地已经插入并且被用户手动关闭了, 所以只有本地不存在的时候才插入
					// 插入 `MyTokenTable`
					if (targetToken.isNull()) myTokenDao.insert(
						MyTokenTable(
							0,
							account.accountName,
							SharedAddress.getCurrentEOS(),
							contract.symbol,
							0.0,
							contract.contract.orEmpty(),
							ChainID.EOS.id,
							false
						)
					)
				}
			}
		}
	}

	// 检查更新默认 `Token` 的 `Name` 信息
	private fun updateUnknownDefaultToken() {
		GoldStoneDataBase.database.defaultTokenDao().getAllTokens().filter {
			ChainID(it.chainID).isETHMain() && it.name.isEmpty()
		}.forEach { default ->
			default.updateTokenNameFromChain()
		}
	}

	private fun updateRAMUnitPrice() {
		EOSResourceUtil.getRAMPrice(EOSUnit.KB, false) { priceInEOS, error ->
			if (priceInEOS != null && error.isNone()) {
				SharedValue.updateRAMUnitPrice(priceInEOS)
			}
		}
	}

	private fun updateCPUUnitPrice() {
		if (SharedAddress.getCurrentEOSAccount().isValid()) {
			EOSResourceUtil.getCPUPrice(SharedAddress.getCurrentEOSAccount()) { priceInEOS, error ->
				if (priceInEOS != null && error.isNone()) {
					SharedValue.updateCPUUnitPrice(priceInEOS)
				}
			}
		}
	}

	private fun updateNETUnitPrice() {
		if (SharedAddress.getCurrentEOSAccount().isValid()) {
			EOSResourceUtil.getNETPrice(SharedAddress.getCurrentEOSAccount()) { priceInEOS, error ->
				if (priceInEOS != null && error.isNone()) {
					SharedValue.updateNETUnitPrice(priceInEOS)
				}
			}
		}
	}

	private fun updateNodeData() {
		// 拉取网络数据, 更新本地的选中状态后覆盖本地数据库 TODO 需要增加 MD5 校验减少网络请求
		GoldStoneAPI.getChainNodes { serverNodes, error ->
			val nodeDao = GoldStoneDataBase.database.chainNodeDao()
			if (serverNodes != null && error.isNone() && serverNodes.isNotEmpty()) {
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
			launch {
				// 检查 EOS 的 Token 价格, 从 NewDex 提供的接口
				myTokens.filter { ChainID(it.chainID).isEOSMain() }.forEach { token ->
					// 因为第三方接口 NewDex 没有列表查询的 API, 只能一个一个的请求, 这里可能会造成
					// 线程开启太多的内存溢出. 限制每 `500ms` 检查一个 `Symbol` 的价格
					delay(500)
					withContext(CommonPool, CoroutineStart.LAZY) {
						EOSAPI.updateLocalTokenPrice(TokenContract(token.contract, token.symbol, null))
					}
				}
			}
		}
	}

	private fun updateLocalDefaultTokens() {
		val defaultDao =
			GoldStoneDataBase.database.defaultTokenDao()
		GoldStoneAPI.getDefaultTokens { serverTokens, error ->
			if (serverTokens != null && serverTokens.isNotEmpty() && error.isNone()) {
				val localTokens = defaultDao.getAllTokens()
				// 开一个线程更新图片
				launch {
					updateLocalTokenIcon(serverTokens, localTokens)
				}
				// 移除掉一样的数据
				defaultDao.insertAll(
					serverTokens.filterNot { server ->
						localTokens.any { local ->
							local.chainID.equals(server.chainID, true)
								&& local.contract.equals(server.contract, true)
						}
					}
				)
			}
		}
	}

	private fun updateLocalTokenIcon(
		serverTokens: List<DefaultTokenTable>,
		localTokens: List<DefaultTokenTable>
	) {
		val unManuallyData = localTokens.filter { it.serverTokenID.isNotEmpty() }
		serverTokens.filter { server ->
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
			if (isEmpty()) return
			val defaultDao =
				GoldStoneDataBase.database.defaultTokenDao()
			forEach { server ->
				defaultDao.getToken(server.contract, server.symbol, server.chainID)?.apply {
					defaultDao.update(
						apply {
							iconUrl = server.iconUrl
							isDefault = server.isDefault
							forceShow = server.forceShow
						}
					)
				}
			}
		}
	}
}