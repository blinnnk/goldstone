package io.goldstone.blockchain.kernel.network.common

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.orZero
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.TinyNumberUtils
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.utils.AesCrypto
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.multichain.TokenIcon
import io.goldstone.blockchain.crypto.multichain.generateObject
import io.goldstone.blockchain.crypto.multichain.node.ChainNodeTable
import io.goldstone.blockchain.kernel.commonmodel.ServerConfigModel
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.ParameterUtil
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil.requestData
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil.requestUnCryptoData
import io.goldstone.blockchain.module.home.profile.profile.model.ShareContentModel
import io.goldstone.blockchain.module.home.profile.profile.model.VersionModel
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionLineChartModel
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.model.TokenSearchModel
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.CoinInfoModel
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ETCTransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.ERC20TransactionModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.TokenPriceModel
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray
import org.json.JSONObject

@SuppressLint("StaticFieldLeak")
/**
 * @date 31/03/2018 8:08 PM
 * @author KaySaith
 * @Important
 * 请求 `Parameters` 以及请求 `Response` 的加密规则是, GoldStone 自有 Server 业务以及 GoldStone
 * 自有节点进行双向加密解密. 第三方接口和节点不加密. 如 `EtherScan`, `Infura` 和 `GasTracker` 等。
 */
object GoldStoneAPI {

	/** 网络请求很多是全台异步所以使用 `Application` 的 `Context` */
	lateinit var context: Context
	private val requestContentType =
		MediaType.parse("application/json; charset=utf-8")

	/**
	 * 从服务器获取产品指定的默认的 `DefaultTokenList`
	 */
	@JvmStatic
	fun getDefaultTokens(@WorkerThread hold: (default: List<DefaultTokenTable>?, error: RequestError) -> Unit) {
		// 首先比对 `MD5` 值如果合法的就会返回列表.
		val config = GoldStoneDataBase.database.appConfigDao().getAppConfig()
		requestData<String>(
			APIPath.defaultTokenList(APIPath.currentUrl, config?.defaultCoinListMD5.orEmpty()),
			"",
			true,
			{ hold(null, it) },
			isEncrypt = true
		) {
			// 如果接口带入的 `MD5` 值和服务器校验的一样, 那么这个接口就会返回一个空的列表
			val data = JSONObject(firstOrNull().orEmpty())
			val defaultTokens = data.safeGet("data")
			// MD5 值存入数据库
			val md5 = data.safeGet("md5")
			GoldStoneDataBase.database.appConfigDao().updateDefaultMD5(md5)
			val gson = Gson()
			val collectionType = object : TypeToken<Collection<DefaultTokenTable>>() {}.type
			val allDefaultTokens = arrayListOf<DefaultTokenTable>()
			val allNode = GoldStoneDataBase.database.chainNodeDao().getAll()
			object : ConcurrentAsyncCombine() {
				override var asyncCount = allNode.size
				override fun concurrentJobs() {
					allNode.forEach { node ->
						allDefaultTokens +=
							try {
								gson.fromJson<List<DefaultTokenTable>>(
									JSONObject(defaultTokens).safeGet(node.chainID),
									collectionType
								)
							} catch (error: Exception) {
								listOf<DefaultTokenTable>()
							}
						completeMark()
					}
				}

				override fun getResultInMainThread(): Boolean = false
				override fun mergeCallBack() {
					hold(allDefaultTokens, RequestError.None)
				}
			}.start()
		}
	}

	@JvmStatic
	fun getTokenInfoBySymbolFromServer(
		symbolsOrContract: String,
		@WorkerThread hold: (tokens: ArrayList<TokenSearchModel>?, error: RequestError) -> Unit
	) {
		requestData<TokenSearchModel>(
			APIPath.getTokenInfo(
				APIPath.currentUrl,
				symbolsOrContract,
				"${SharedChain.getCurrentETH().chainID.id},${SharedChain.getETCCurrent().chainID.id},${SharedChain.getBTCCurrent().chainID.id},${SharedChain.getLTCCurrent().chainID.id},${SharedChain.getEOSCurrent().chainID.id},${SharedChain.getBCHCurrent().chainID.id}"
			),
			"list",
			false,
			{ hold(null, it) },
			isEncrypt = true
		) {
			hold(toArrayList(), RequestError.None)
		}
	}

	@JvmStatic
	fun getETCTransactions(
		chainID: ChainID,
		address: String,
		startBlock: String,
		hold: (transactions: ArrayList<ETCTransactionModel>?, error: RequestError) -> Unit
	) {
		requestData<ETCTransactionModel>(
			APIPath.getETCTransactions(
				APIPath.currentUrl,
				chainID.id,
				address,
				startBlock
			),
			"list",
			false,
			{ hold(null, it) },
			isEncrypt = true
		) {
			hold(toArrayList(), RequestError.None)
		}
	}

	@JvmStatic
	fun getNewVersionOrElse(hold: (versionData: VersionModel?, error: RequestError) -> Unit) {
		requestData<String>(
			APIPath.getNewVersion(APIPath.currentUrl),
			"",
			true,
			{ hold(null, it) },
			isEncrypt = true
		) {
			val data = JSONObject(this[0])
			val hasNewVersion =
				if (this[0].contains("has_new_version"))
					TinyNumberUtils.isTrue(data.safeGet("has_new_version"))
				else false
			context.runOnUiThread {
				if (hasNewVersion) {
					hold(VersionModel(JSONObject(data.safeGet("data"))), RequestError.None)
				} else {
					hold(null, RequestError.RPCResult("empty result"))
				}
			}
		}
	}

	@JvmStatic
	fun getCurrencyRate(
		symbols: String,
		@WorkerThread hold: (rate: Double?, error: RequestError) -> Unit
	) {
		requestData<String>(
			APIPath.getCurrencyRate(APIPath.currentUrl) + symbols,
			"rate",
			true,
			{ hold(null, it) },
			isEncrypt = true
		) {
			firstOrNull().isNotNull { hold(firstOrNull()?.toDoubleOrNull().orZero(), RequestError.None) }
		}
	}

	@JvmStatic
	fun getTerms(
		md5: String,
		@WorkerThread hold: (term: String?, error: RequestError) -> Unit
	) {
		requestData<String>(
			APIPath.terms(APIPath.currentUrl) + md5,
			"",
			true,
			{ hold(null, it) },
			isEncrypt = true
		) {
			hold(JSONObject(this[0]).safeGet("result"), RequestError.None)
		}
	}

	@JvmStatic
	fun getConfigList(
		@WorkerThread hold: (configs: List<ServerConfigModel>?, error: RequestError) -> Unit
	) {
		requestData<ServerConfigModel>(
			APIPath.getConfigList(APIPath.currentUrl),
			"list",
			false,
			{ hold(null, it) },
			isEncrypt = true
		) {
			hold(this, RequestError.None)
		}
	}

	@JvmStatic
	fun getShareContent(hold: (content: ShareContentModel?, error: RequestError) -> Unit) {
		requestData<String>(
			APIPath.getShareContent(APIPath.currentUrl),
			"data",
			false,
			{ hold(null, it) },
			isEncrypt = true
		) {
			firstOrNull().isNotNull {
				hold(ShareContentModel(JSONObject(this[0])), RequestError.None)
			}
		}
	}

	@JvmStatic
	fun getChainNodes(hold: (content: List<ChainNodeTable>?, error: RequestError) -> Unit) {
		requestData<ChainNodeTable>(
			APIPath.getChainNodes(APIPath.currentUrl),
			"data",
			false,
			{ hold(null, it) },
			isEncrypt = true
		) {
			hold(this, RequestError.None)
		}
	}

	@JvmStatic
	fun getMarketSearchList(
		pair: String,
		@WorkerThread hold: (markets: List<QuotationSelectionTable>?, error: RequestError) -> Unit
	) {
		requestData<QuotationSelectionTable>(
			APIPath.marketSearch(APIPath.currentUrl) + pair,
			"pair_list",
			false,
			{ hold(null, it) },
			isEncrypt = true
		) {
			hold(this, RequestError.None)
		}
	}

	fun getERC20TokenIncomingTransaction(
		startBlock: String = "0",
		address: String,
		hold: (erc20Transactions: List<ERC20TransactionModel>?, error: RequestError) -> Unit
	) {
		requestUnCryptoData<ERC20TransactionModel>(
			EtherScanApi.getTokenIncomingTransaction(address, startBlock),
			"result",
			false,
			{ hold(null, it) }
		) {
			hold(this, RequestError.None)
		}
	}

	/**
	 * 从 `EtherScan` 获取指定钱包地址的 `TransactionList`
	 */
	@JvmStatic
	fun getTransactionListByAddress(
		startBlock: String = "0",
		address: String,
		hold: (transactions: List<TransactionTable>?, error: RequestError) -> Unit
	) {
		requestUnCryptoData<TransactionTable>(
			EtherScanApi.transactions(address, startBlock),
			"result",
			false,
			{ hold(null, it) }
		) {
			hold(map { TransactionTable(it) }, RequestError.None)
		}
	}

	fun getIconURL(
		contractList: List<TokenContract>,
		hold: (data: List<TokenIcon>?, error: GoldStoneError) -> Unit
	) {
		RequisitionUtil.post<TokenIcon>(
			AesCrypto.encrypt(contractList.generateObject()).orEmpty(),
			APIPath.getIconURL(APIPath.currentUrl),
			"token_list",
			{ hold(null, it) },
			true
		) {
			hold(it, RequestError.None)
		}
	}

	fun registerDevice(
		language: String,
		pushToken: String,
		deviceID: String,
		isChina: Int,
		isAndroid: Int,
		country: String,
		errorCallback: (RequestError) -> Unit,
		hold: (String) -> Unit
	) {
		RequisitionUtil.postRequest(
			RequestBody.create(
				requestContentType,
				ParameterUtil.prepare(
					true,
					Pair("language", language),
					Pair("cid", pushToken),
					Pair("device", deviceID),
					Pair("push_type", isChina),
					Pair("os", isAndroid),
					Pair("country", country)
				)
			),
			APIPath.registerDevice(APIPath.currentUrl),
			errorCallback,
			true
		) {
			hold(it)
		}
	}

	fun unregisterDevice(
		targetGoldStoneID: String,
		errorCallback: (RequestError) -> Unit,
		hold: (Boolean) -> Unit
	) {
		requestData<String>(
			APIPath.unregeisterDevice(APIPath.currentUrl),
			"code",
			true,
			errorCallback,
			isEncrypt = true,
			targetGoldStoneID = targetGoldStoneID,
			maxConnectTime = 5
		) {
			if (this.isNotEmpty()) hold(this[0] == "0")
			else hold(false)
		}
	}

	fun getCurrencyLineChartData(
		pairList: JsonArray,
		hold: (lineData: List<QuotationSelectionLineChartModel>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.postRequest<QuotationSelectionLineChartModel>(
			RequestBody.create(
				requestContentType,
				ParameterUtil.prepare(true, Pair("pair_list", pairList))
			),
			"data_list",
			APIPath.getCurrencyLineChartData(APIPath.currentUrl),
			errorCallback = { hold(null, it) },
			isEncrypt = true
		) {
			hold(it, RequestError.None)
		}
	}

	fun registerWalletAddresses(
		content: String,
		errorCallback: (RequestError) -> Unit,
		hold: (String) -> Unit
	) {
		RequisitionUtil.postRequest(
			RequestBody.create(
				requestContentType,
				content
			),
			APIPath.updateAddresses(APIPath.currentUrl),
			errorCallback,
			true,
			hold
		)
	}

	fun getUnreadCount(
		deviceID: String,
		time: Long,
		hold: (unreadCount: String?, error: RequestError) -> Unit
	) {
		RequisitionUtil.postRequest(
			RequestBody.create(
				requestContentType,
				ParameterUtil.prepare(
					true,
					Pair("device", deviceID),
					Pair("time", time)
				)
			),
			APIPath.getUnreadCount(APIPath.currentUrl),
			{ hold(null, it) },
			true
		) {
			hold(JSONObject(it).safeGet("count"), RequestError.None)
		}
	}

	fun getNotificationList(
		time: Long,
		@WorkerThread hold: (notifications: ArrayList<NotificationTable>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.postRequest<String>(
			RequestBody.create(
				requestContentType,
				ParameterUtil.prepare(true, Pair("time", time))
			),
			"message_list",
			APIPath.getNotification(APIPath.currentUrl),
			true,
			{ hold(null, it) },
			true
		) { it ->
			// 因为返回的数据格式复杂这里采用自己处理数据的方式, 不实用 `Gson`
			val notificationData = arrayListOf<NotificationTable>()
			val jsonArray = JSONArray(it[0])
			if (jsonArray.length() == 0) {
				hold(arrayListOf(), RequestError.None)
			} else {
				(0 until jsonArray.length()).forEach {
					notificationData.add(NotificationTable(JSONObject(jsonArray[it].toString())))
				}
				hold(notificationData, RequestError.None)
			}
		}
	}

	fun getPriceByContractAddress(
		addressList: List<String>,
		isMainThread: Boolean,
		hold: (priceList: List<TokenPriceModel>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.postRequest<TokenPriceModel>(
			RequestBody.create(
				requestContentType,
				ParameterUtil.prepare(true, Pair("address_list", addressList))
			),
			"price_list",
			APIPath.getPriceByAddress(APIPath.currentUrl),
			errorCallback = { hold(null, it) },
			isEncrypt = true
		) {
			if (isMainThread) GoldStoneAPI.context.runOnUiThread {
				hold(it, RequestError.None)
			} else {
				hold(it, RequestError.None)
			}
		}
	}

	fun getQuotationCurrencyCandleChart(
		pair: String,
		period: String,
		size: Int,
		hold: (candleChart: ArrayList<CandleChartModel>?, error: RequestError) -> Unit
	) {
		requestData<CandleChartModel>(
			APIPath.getQuotationCurrencyCandleChart(APIPath.currentUrl, pair, period, size),
			"ticks",
			errorCallback = { hold(null, it) },
			isEncrypt = true
		) {
			hold(this.toArrayList(), RequestError.None)
		}
	}

	fun getQuotationCurrencyInfo(
		pair: String,
		@WorkerThread hold: (data: JSONObject?, error: RequestError) -> Unit
	) {
		requestData<String>(
			APIPath.getQuotationCurrencyInfo(APIPath.currentUrl, pair),
			"",
			true,
			{ hold(null, it) },
			isEncrypt = true
		) {
			hold(JSONObject(firstOrNull().orEmpty()), RequestError.None)
		}
	}

	fun getTokenInfoFromMarket(
		symbol: String,
		chainID: String,
		hold: (coinInfo: CoinInfoModel?, error: RequestError) -> Unit
	) {
		requestData<String>(
			APIPath.getCoinInfo(APIPath.currentUrl) + symbol,
			"",
			true,
			{ hold(null, it) },
			isEncrypt = true
		) {
			hold(
				CoinInfoModel(JSONObject(firstOrNull().orEmpty()), symbol, chainID),
				RequestError.None
			)
		}
	}
}





