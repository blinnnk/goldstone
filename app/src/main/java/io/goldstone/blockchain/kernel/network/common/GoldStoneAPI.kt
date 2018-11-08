package io.goldstone.blockchain.kernel.network.common

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orEmpty
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
import io.goldstone.blockchain.common.utils.toList
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.multichain.TokenIcon
import io.goldstone.blockchain.crypto.multichain.generateObject
import io.goldstone.blockchain.crypto.multichain.node.ChainNodeTable
import io.goldstone.blockchain.kernel.commonmodel.ServerConfigModel
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.ParameterUtil
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil.requestData
import io.goldstone.blockchain.module.home.profile.profile.model.ShareContentModel
import io.goldstone.blockchain.module.home.profile.profile.model.VersionModel
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionLineChartModel
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.model.TokenSearchModel
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.CoinInfoModel
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.kernel.commonmodel.ETCTransactionModel
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
	fun getDefaultTokens(
		@WorkerThread hold: (default: List<DefaultTokenTable>?, error: RequestError) -> Unit
	) {
		// 首先比对 `MD5` 值如果合法的就会返回列表.
		val config = GoldStoneDataBase.database.appConfigDao().getAppConfig()
		requestData<String>(
			APIPath.defaultTokenList(APIPath.currentUrl, config?.defaultCoinListMD5.orEmpty()),
			"",
			true,
			isEncrypt = true
		) { result, error ->
			if (result.isNull() || error.hasError()) {
				hold(null, error)
				return@requestData
			}
			// 如果接口带入的 `MD5` 值和服务器校验的一样, 那么这个接口就会返回一个空的列表
			val data = JSONObject(result?.firstOrNull().orEmpty())
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
				override val completeInUIThread: Boolean = false
				override fun doChildTask(index: Int) {
					allDefaultTokens +=
						try {
							gson.fromJson<List<DefaultTokenTable>>(
								JSONObject(defaultTokens).safeGet(allNode[index].chainID),
								collectionType
							)
						} catch (error: Exception) {
							listOf<DefaultTokenTable>()
						}
					completeMark()
				}

				override fun mergeCallBack() = hold(allDefaultTokens, RequestError.None)
			}.start()
		}
	}

	@JvmStatic
	fun getTokenInfoBySymbolFromServer(
		symbolsOrContract: String,
		@WorkerThread hold: (tokens: List<TokenSearchModel>?, error: RequestError) -> Unit
	) {
		requestData(
			APIPath.getTokenInfo(
				APIPath.currentUrl,
				symbolsOrContract,
				"${SharedChain.getCurrentETH().chainID.id},${SharedChain.getETCCurrent().chainID.id},${SharedChain.getBTCCurrent().chainID.id},${SharedChain.getLTCCurrent().chainID.id},${SharedChain.getEOSCurrent().chainID.id},${SharedChain.getBCHCurrent().chainID.id}"
			),
			"list",
			false,
			isEncrypt = true,
			hold = hold
		)
	}

	@JvmStatic
	fun getETCTransactions(
		chainID: ChainID,
		address: String,
		startBlock: String,
		@WorkerThread hold: (transactions: List<ETCTransactionModel>?, error: RequestError) -> Unit
	) {
		requestData(
			APIPath.getETCTransactions(
				APIPath.currentUrl,
				chainID.id,
				address,
				startBlock
			),
			"list",
			false,
			isEncrypt = true,
			hold = hold
		)
	}

	@JvmStatic
	fun getNewVersionOrElse(
		@WorkerThread hold: (versionData: VersionModel?, error: RequestError) -> Unit
	) {
		requestData<String>(
			APIPath.getNewVersion(APIPath.currentUrl),
			"",
			true,
			isEncrypt = true
		) { result, error ->
			if (!result.isNull() && error.isNone()) {
				val content = result?.firstOrNull().orEmpty()
				val data = JSONObject(content)
				val hasNewVersion =
					if (content.contains("has_new_version"))
						TinyNumberUtils.isTrue(data.safeGet("has_new_version"))
					else false
				if (hasNewVersion)
					hold(VersionModel(JSONObject(data.safeGet("data"))), RequestError.None)
				else hold(null, RequestError.RPCResult("empty result"))
			} else hold(null, error)
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
			isEncrypt = true
		) { result, error ->
			val rate = result?.firstOrNull()?.toDoubleOrNull()
			hold(rate, error)
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
			isEncrypt = true
		) { result, error ->
			if (!result.isNull() && error.isNone()) {
				hold(JSONObject(result!!.firstOrNull()).safeGet("result"), error)
			} else hold(null, error)
		}
	}

	@JvmStatic
	fun getConfigList(
		@WorkerThread hold: (configs: List<ServerConfigModel>?, error: RequestError) -> Unit
	) {
		requestData(
			APIPath.getConfigList(APIPath.currentUrl),
			"list",
			false,
			isEncrypt = true,
			hold = hold
		)
	}

	@JvmStatic
	fun getShareContent(@WorkerThread hold: (content: ShareContentModel?, error: RequestError) -> Unit) {
		requestData<String>(
			APIPath.getShareContent(APIPath.currentUrl),
			"data",
			false,
			isEncrypt = true
		) { result, error ->
			if (!result.isNull() && error.isNone()) {
				hold(ShareContentModel(JSONObject(result!!.firstOrNull().orEmpty())), error)
			} else hold(null, error)
		}
	}

	@JvmStatic
	fun getChainNodes(
		@WorkerThread hold: (content: List<ChainNodeTable>?, error: RequestError) -> Unit
	) {
		requestData(
			APIPath.getChainNodes(APIPath.currentUrl),
			"data",
			false,
			isEncrypt = true,
			hold = hold
		)
	}

	@JvmStatic
	fun getMarketSearchList(
		pair: String,
		marketIds: String,
		@WorkerThread hold: (quotationTableList: List<QuotationSelectionTable>?, error: RequestError) -> Unit
	) {
		requestData(
			APIPath.marketSearch(APIPath.currentUrl, pair, marketIds),
			"pair_list",
			false,
			isEncrypt = true,
			hold = hold
		)
	}

	@JvmStatic
	fun getMarketList(
		md5: String,
		@WorkerThread hold: (exchangeTableList: ArrayList<ExchangeTable>?, newMd5: String?, error: RequestError) -> Unit) {
		requestData<String>(
			APIPath.marketList(APIPath.currentUrl, md5),
			"",
			true,
			isEncrypt = true
		) { list, error ->
			if (error.isNone() && !list.isNull()) {
				try {
					val data = JSONObject(list!!.firstOrNull())
					val exchangeTables = data.safeGet("list")
					val newMd5 = data.safeGet("md5")
					val collectionType = object : TypeToken<Collection<ExchangeTable>>() {}.type
					val exchangeTableList =
						if (exchangeTables.isEmpty()) arrayListOf<ExchangeTable>()
						else Gson().fromJson(exchangeTables, collectionType)
					hold(exchangeTableList, newMd5, RequestError.None)
				} catch (error: Exception) {
					hold(null, null, RequestError.ResolveDataError(error))
				}
			} else {
				hold(null, null, error)
			}

		}
	}

	fun getIconURL(
		contractList: List<TokenContract>,
		@WorkerThread hold: (data: List<TokenIcon>?, error: GoldStoneError) -> Unit
	) {
		RequisitionUtil.post(
			AesCrypto.encrypt(contractList.generateObject()).orEmpty(),
			APIPath.getIconURL(APIPath.currentUrl),
			"token_list",
			true,
			hold
		)
	}

	fun registerDevice(
		language: String,
		pushToken: String,
		deviceID: String,
		isChina: Int,
		isAndroid: Int,
		country: String,
		@WorkerThread hold: (result: String?, error: RequestError) -> Unit
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
			true,
			hold
		)
	}

	fun unregisterDevice(
		targetGoldStoneID: String,
		@WorkerThread hold: (isRegistered: Boolean?, error: RequestError) -> Unit
	) {
		requestData<String>(
			APIPath.unregeisterDevice(APIPath.currentUrl),
			"code",
			true,
			isEncrypt = true,
			targetGoldStoneID = targetGoldStoneID,
			maxConnectTime = 5
		) { result, error ->
			if (!result.isNull() && error.isNone()) {
				hold(result!!.firstOrNull() == "0", error)
			} else hold(null, error)
		}
	}

	fun getCurrencyLineChartData(
		pairList: JsonArray,
		@WorkerThread hold: (lineData: List<QuotationSelectionLineChartModel>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.postRequest<QuotationSelectionLineChartModel>(
			RequestBody.create(
				requestContentType,
				ParameterUtil.prepare(true, Pair("pair_list", pairList))
			),
			"data_list",
			APIPath.getCurrencyLineChartData(APIPath.currentUrl),
			isEncrypt = true
		) { result, error ->
			if (result != null && error.isNone()) {
				hold(result, error)
			} else hold(null, error)
		}
	}

	fun registerWalletAddresses(
		content: String,
		@WorkerThread hold: (result: String?, error: RequestError) -> Unit
	) {
		RequisitionUtil.postRequest(
			RequestBody.create(
				requestContentType,
				content
			),
			APIPath.updateAddresses(APIPath.currentUrl),
			true,
			hold
		)
	}

	fun getUnreadCount(
		deviceID: String,
		time: Long,
		@WorkerThread hold: (unreadCount: Int?, error: RequestError) -> Unit
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
			true
		) { result, error ->
			if (result != null && error.isNone()) {
				hold(JSONObject(result).safeGet("count").toIntOrNull(), error)
			} else hold(null, error)
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
			true
		) { result, error ->
			// 因为返回的数据格式复杂这里采用自己处理数据的方式, 不用 `Gson`
			if (result != null && error.isNone()) {
				val jsonArray = JSONArray(result.firstOrNull().orEmpty())
				if (jsonArray.length() == 0) {
					hold(arrayListOf(), error)
				} else {
					val notifications =
						jsonArray.toList().map { NotificationTable(it) }.toArrayList()
					hold(notifications, error)
				}
			} else hold(null, error)
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
			isEncrypt = true
		) { result, error ->
			if (!result.isNull() && error.isNone()) {
				if (isMainThread) GoldStoneAPI.context.runOnUiThread {
					hold(result, error)
				} else hold(result, error)
			} else hold(null, error)
		}
	}

	fun getQuotationCurrencyCandleChart(
		pair: String,
		period: String,
		size: Int,
		@WorkerThread hold: (candleChart: List<CandleChartModel>?, error: RequestError) -> Unit
	) {
		requestData(
			APIPath.getQuotationCurrencyCandleChart(APIPath.currentUrl, pair, period, size),
			"ticks",
			isEncrypt = true,
			hold = hold
		)
	}

	fun getQuotationCurrencyInfo(
		pair: String,
		@WorkerThread hold: (data: JSONObject?, error: RequestError) -> Unit
	) {
		requestData<String>(
			APIPath.getQuotationCurrencyInfo(APIPath.currentUrl, pair),
			"",
			true,
			isEncrypt = true
		) { result, error ->
			if (!result.isNull() && error.isNone()) {
				hold(JSONObject(result!!.firstOrNull().orEmpty()), error)
			} else hold(null, error)
		}
	}

	fun getTokenInfoFromMarket(
		symbol: String,
		chainID: ChainID,
		@WorkerThread hold: (coinInfo: CoinInfoModel?, error: RequestError) -> Unit
	) {
		requestData<String>(
			APIPath.getCoinInfo(APIPath.currentUrl) + symbol,
			"",
			true,
			isEncrypt = true
		) { result, error ->
			if (!result.isNull() && error.isNone()) {
				hold(
					CoinInfoModel(JSONObject(result!!.firstOrNull().orEmpty()), symbol, chainID),
					error
				)
			} else hold(null, error)
		}
	}
}





