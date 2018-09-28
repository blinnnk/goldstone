package io.goldstone.blockchain.kernel.network

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.forEachOrEnd
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.TinyNumberUtils
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.commonmodel.ServerConfigModel
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.RequisitionUtil.requestData
import io.goldstone.blockchain.kernel.network.RequisitionUtil.requestUnCryptoData
import io.goldstone.blockchain.module.home.profile.profile.model.ShareContentModel
import io.goldstone.blockchain.module.home.profile.profile.model.VersionModel
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.*
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
	fun getDefaultTokens(
		errorCallback: (RequestError) -> Unit,
		@WorkerThread hold: (List<DefaultTokenTable>) -> Unit
	) {
		// 首先比对 `MD5` 值如果合法的就会返回列表.
		AppConfigTable.getAppConfig { it ->
			requestData<String>(
				APIPath.defaultTokenList(APIPath.currentUrl, it?.defaultCoinListMD5.orEmpty()),
				"",
				true,
				errorCallback,
				isEncrypt = true
			) {
				// 如果接口带入的 `MD5` 值和服务器校验的一样, 那么这个接口就会返回一个空的列表
				val data = JSONObject(this[0])
				val defaultTokens = data.safeGet("data")
				// MD5 值存入数据库
				val md5 = data.safeGet("md5")
				AppConfigTable.updateDefaultTokenMD5(md5)
				val gson = Gson()
				val collectionType = object : TypeToken<Collection<DefaultTokenTable>>() {}.type
				val allDefaultTokens = arrayListOf<DefaultTokenTable>()
				object : ConcurrentAsyncCombine() {
					override var asyncCount = ChainID.getAllChainID().size
					override fun concurrentJobs() {
						ChainID.getAllChainID().forEach { chainID ->
							allDefaultTokens +=
								try {
									gson.fromJson<List<DefaultTokenTable>>(
										JSONObject(defaultTokens).safeGet(chainID),
										collectionType
									)
								} catch (error: Exception) {
									listOf<DefaultTokenTable>()
								}.map { defaultToken ->
									defaultToken.apply {
										this.chainID = chainID
										this.isDefault = true
									}
								}.apply {
									completeMark()
								}
						}
					}

					override fun getResultInMainThread(): Boolean = false
					override fun mergeCallBack() = hold(allDefaultTokens)

				}.start()
			}
		}
	}

	@JvmStatic
	fun getTokenInfoBySymbolFromServer(
		symbolsOrContract: String,
		errorCallback: (RequestError) -> Unit,
		hold: (ArrayList<TokenSearchModel>) -> Unit
	) {
		requestData<TokenSearchModel>(
			APIPath.getTokenInfo(
				APIPath.currentUrl,
				symbolsOrContract,
				"${SharedChain.getCurrentETH().id},${SharedChain.getETCCurrent().id},${SharedChain.getBTCCurrent().id},${SharedChain.getLTCCurrent().id},${SharedChain.getEOSCurrent().id}"
			),
			"list",
			false,
			errorCallback,
			isEncrypt = true
		) {
			hold(toArrayList())
		}
	}

	@JvmStatic
	fun getETCTransactions(
		chainID: ChainID,
		address: String,
		startBlock: String,
		errorCallback: (RequestError) -> Unit,
		hold: (ArrayList<ETCTransactionModel>) -> Unit
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
			errorCallback,
			isEncrypt = true
		) {
			hold(toArrayList())
		}
	}

	@JvmStatic
	fun getNewVersionOrElse(
		errorCallback: (RequestError) -> Unit = {},
		hold: (VersionModel?) -> Unit
	) {
		requestData<String>(
			APIPath.getNewVersion(APIPath.currentUrl),
			"",
			true,
			errorCallback,
			isEncrypt = true
		) {
			val data = JSONObject(this[0])
			val hasNewVersion =
				if (this[0].contains("has_new_version"))
					TinyNumberUtils.isTrue(data.safeGet("has_new_version"))
				else false
			GoldStoneAPI.context.runOnUiThread {
				if (hasNewVersion) {
					hold(VersionModel(JSONObject(data.safeGet("data"))))
				} else {
					hold(null)
				}
			}
		}
	}

	@JvmStatic
	fun getCurrencyRate(
		symbols: String,
		errorCallback: (RequestError) -> Unit,
		hold: (Double) -> Unit
	) {
		requestData<String>(
			APIPath.getCurrencyRate(APIPath.currentUrl) + symbols,
			"rate",
			true,
			errorCallback,
			isEncrypt = true
		) {
			this[0].isNotNull { hold(this[0].toDouble()) }
		}
	}

	@JvmStatic
	fun getTerms(
		md5: String,
		errorCallback: (RequestError) -> Unit,
		hold: (String) -> Unit
	) {
		requestData<String>(
			APIPath.terms(APIPath.currentUrl) + md5,
			"",
			true,
			errorCallback,
			isEncrypt = true
		) {
			hold(JSONObject(this[0]).safeGet("result"))
		}
	}

	@JvmStatic
	fun getConfigList(
		errorCallback: (RequestError) -> Unit,
		@UiThread hold: (List<ServerConfigModel>) -> Unit
	) {
		requestData<ServerConfigModel>(
			APIPath.getConfigList(APIPath.currentUrl),
			"list",
			false,
			errorCallback,
			isEncrypt = true
		) {
			GoldStoneAPI.context.runOnUiThread {
				hold(this@requestData)
			}
		}
	}

	@JvmStatic
	fun getShareContent(
		errorCallback: (RequestError) -> Unit,
		hold: (ShareContentModel) -> Unit
	) {
		requestData<String>(
			APIPath.getShareContent(APIPath.currentUrl),
			"data",
			true,
			errorCallback,
			isEncrypt = true
		) {
			this[0].isNotNull {
				hold(ShareContentModel(JSONObject(this[0])))
			}
		}
	}

	@JvmStatic
	fun getMarketSearchList(
		pair: String,
		marketIds: String,
		errorCallback: (RequestError) -> Unit,
		hold: (List<QuotationSelectionTable>) -> Unit
	) {
		requestData<QuotationSelectionTable>(
			APIPath.marketSearch(APIPath.currentUrl, pair, marketIds),
			"pair_list",
			false,
			errorCallback,
			isEncrypt = true
		) {
			hold(this)
		}
	}
	
	@JvmStatic
	fun getMarketList(
		md5: String,
		errorCallback: (RequestError) -> Unit,
		hold: (ArrayList<ExchangeTable>, String) -> Unit) {
		requestData<String>(
			APIPath.marketList(APIPath.currentUrl, md5),
			"",
			true,
			errorCallback,
			isEncrypt = true
		) {
			try {
				val data = JSONObject(this[0])
				val exchangeTables = data.safeGet("list")
				val newMd5 = data.safeGet("md5")
				val collectionType = object : TypeToken<Collection<ExchangeTable>>() {}.type
				val exchangeTableList =
					if (exchangeTables.isEmpty()) arrayListOf<ExchangeTable>()
					else Gson().fromJson(exchangeTables, collectionType)
				hold(exchangeTableList, newMd5)
			} catch (error: Exception) {
				errorCallback(RequestError.ResolveDataError(error))
			}
		}
	}

	fun getERC20TokenIncomingTransaction(
		startBlock: String = "0",
		errorCallback: (RequestError) -> Unit,
		address: String,
		hold: (ArrayList<ERC20TransactionModel>) -> Unit
	) {
		requestUnCryptoData<ERC20TransactionModel>(
			EtherScanApi.getTokenIncomingTransaction(address, startBlock),
			"result",
			false,
			errorCallback
		) {
			hold(toArrayList())
		}
	}

	/**
	 * 从 `EtherScan` 获取指定钱包地址的 `TransactionList`
	 */
	@JvmStatic
	fun getTransactionListByAddress(
		startBlock: String = "0",
		address: String,
		errorCallback: (RequestError) -> Unit,
		hold: ArrayList<TransactionTable>.() -> Unit
	) {
		requestUnCryptoData<TransactionTable>(
			EtherScanApi.transactions(address, startBlock),
			"result",
			false,
			errorCallback
		) {
			hold(map { TransactionTable(it) }.toArrayList())
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
		errorCallback: (RequestError) -> Unit,
		hold: (List<QuotationSelectionLineChartModel>) -> Unit
	) {
		RequisitionUtil.postRequest(
			RequestBody.create(
				requestContentType,
				ParameterUtil.prepare(
					true,
					Pair("pair_list", pairList)
				)
			),
			"data_list",
			APIPath.getCurrencyLineChartData(APIPath.currentUrl),
			errorCallback = errorCallback,
			isEncrypt = true,
			hold = hold
		)
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
		errorCallback: (RequestError) -> Unit,
		hold: (String) -> Unit
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
			errorCallback,
			true
		) {
			hold(JSONObject(it).safeGet("count"))
		}
	}

	fun getNotificationList(
		time: Long,
		errorCallback: (RequestError) -> Unit,
		hold: (ArrayList<NotificationTable>) -> Unit
	) {
		RequisitionUtil.postRequest<String>(
			RequestBody.create(
				requestContentType,
				ParameterUtil.prepare(true, Pair("time", time))
			),
			"message_list",
			APIPath.getNotification(APIPath.currentUrl),
			true,
			errorCallback,
			true
		) { it ->
			// 因为返回的数据格式复杂这里采用自己处理数据的方式, 不实用 `Gson`
			val notificationData = arrayListOf<NotificationTable>()
			val jsonArray = JSONArray(it[0])
			GoldStoneAPI.context.runOnUiThread {
				if (jsonArray.length() == 0) {
					hold(arrayListOf())
				} else {
					(0 until jsonArray.length()).forEachOrEnd { it, isEnd ->
						notificationData.add(NotificationTable(JSONObject(jsonArray[it].toString())))
						if (isEnd) hold(notificationData)
					}
				}
			}
		}
	}

	fun getPriceByContractAddress(
		addressList: JsonArray,
		errorCallback: (RequestError) -> Unit,
		@UiThread hold: (List<TokenPriceModel>) -> Unit
	) {
		RequisitionUtil.postRequest<TokenPriceModel>(
			RequestBody.create(
				requestContentType,
				ParameterUtil.prepare(true, Pair("address_list", addressList))
			),
			"price_list",
			APIPath.getPriceByAddress(APIPath.currentUrl),
			errorCallback = errorCallback,
			isEncrypt = true
		) {
			GoldStoneAPI.context.runOnUiThread {
				hold(it)
			}
		}
	}

	fun getQuotationCurrencyCandleChart(
		pair: String,
		period: String,
		size: Int,
		errorCallback: (RequestError) -> Unit,
		hold: (ArrayList<CandleChartModel>) -> Unit
	) {
		requestData<CandleChartModel>(
			APIPath.getQuotationCurrencyCandleChart(APIPath.currentUrl, pair, period, size),
			"ticks",
			errorCallback = errorCallback,
			isEncrypt = true
		) {
			hold(this.toArrayList())
		}
	}

	fun getQuotationCurrencyInfo(
		pair: String,
		errorCallback: (RequestError) -> Unit,
		hold: (JSONObject) -> Unit
	) {
		requestData<String>(
			APIPath.getQuotationCurrencyInfo(APIPath.currentUrl, pair),
			"",
			true,
			errorCallback,
			isEncrypt = true
		) {
			hold(JSONObject(first()))
		}
	}

	fun getTokenInfoFromMarket(
		symbol: String,
		chainID: String,
		errorCallback: (RequestError) -> Unit,
		hold: (CoinInfoModel) -> Unit
	) {
		requestData<String>(
			APIPath.getCoinInfo(APIPath.currentUrl) + symbol,
			"",
			true,
			errorCallback,
			isEncrypt = true
		) {
			hold(CoinInfoModel(JSONObject(firstOrNull().orEmpty()), symbol, chainID))
		}
	}
}





