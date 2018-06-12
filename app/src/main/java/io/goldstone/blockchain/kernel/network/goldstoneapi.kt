package io.goldstone.blockchain.kernel.network

import android.annotation.SuppressLint
import android.content.Context
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.orZero
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toArrayList
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.common.utils.AesCrypto
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.value.ChainID
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.kernel.commonmodel.ServerConfigModel
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.RequisitionUtil.postRequest
import io.goldstone.blockchain.kernel.network.RequisitionUtil.postRequestGetJsonObject
import io.goldstone.blockchain.kernel.network.RequisitionUtil.requestData
import io.goldstone.blockchain.kernel.network.RequisitionUtil.requestUncryptoData
import io.goldstone.blockchain.module.home.profile.profile.model.ShareContentModel
import io.goldstone.blockchain.module.home.profile.profile.model.VersionModel
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.ChartModel
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionLineChartModel
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.model.TokenSearchModel
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TinyNumber
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.ERC20TransactionModel
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
 */
object GoldStoneAPI {
	
	/** 网络请求很多是全台异步所以使用 `Application` 的 `Context` */
	lateinit var context: Context
	private val requestContentType = MediaType.parse("application/json; charset=utf-8")
	
	/**
	 * 从服务器获取产品指定的默认的 `DefaultTokenList`
	 */
	@JvmStatic
	fun getDefaultTokens(
		errorCallback: (Exception) -> Unit = {},
		hold: (ArrayList<DefaultTokenTable>) -> Unit
	) {
		requestData<String>(APIPath.defaultTokenList, "data", true, errorCallback) {
			val gson = Gson()
			val collectionType = object : TypeToken<Collection<DefaultTokenTable>>() {}.type
			val allDefaultTokens = arrayListOf<DefaultTokenTable>()
			object : ConcurrentAsyncCombine() {
				override var asyncCount = ChainID.getAllChainID().size
				override fun concurrentJobs() {
					ChainID.getAllChainID().forEach { chainID ->
						allDefaultTokens +=
							gson.fromJson<List<DefaultTokenTable>>(
								JSONObject(this@requestData[0]).safeGet(chainID),
								collectionType
							).map {
								it.apply {
									it.chain_id = chainID
									it.isDefault = true
								}
							}.apply {
								completeMark()
							}
					}
				}
				
				override fun mergeCallBack() {
					hold(allDefaultTokens)
				}
			}.start()
		}
	}
	
	@JvmStatic
	fun getCoinInfoBySymbolFromGoldStone(
		symbols: String,
		errorCallback: (Exception) -> Unit,
		hold: (ArrayList<TokenSearchModel>) -> Unit
	) {
		requestData<TokenSearchModel>(
			APIPath.getCoinInfo + symbols,
			"list",
			errorCallback = errorCallback
		) {
			hold(toArrayList())
		}
	}
	
	@JvmStatic
	fun getNewVersionOrElse(
		errorCallback: (Exception) -> Unit = {},
		hold: (VersionModel?) -> Unit
	) {
		requestData<String>(
			APIPath.getNewVersion,
			"",
			true,
			errorCallback
		) {
			val data = JSONObject(this[0])
			val hasNewVersion =
				data.safeGet("has_new_version").toIntOrNull().orZero() == TinyNumber.True.value
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
		errorCallback: (Exception) -> Unit,
		hold: (Double) -> Unit
	) {
		requestData<String>(
			APIPath.getCurrencyRate + symbols,
			"rate",
			true,
			errorCallback
		) {
			this[0].isNotNull { hold(this[0].toDouble()) }
		}
	}
	
	@JvmStatic
	fun getTerms(
		md5: String,
		errorCallback: (Exception) -> Unit,
		hold: (String) -> Unit
	) {
		requestData<String>(
			APIPath.terms + md5,
			"",
			true,
			errorCallback
		) {
			hold(JSONObject(this[0]).safeGet("result"))
		}
	}
	
	@JvmStatic
	fun getConfigList(
		errorCallback: (Exception) -> Unit,
		hold: (ArrayList<ServerConfigModel>) -> Unit
	) {
		requestData<ServerConfigModel>(
			APIPath.getConfigList,
			"list",
			errorCallback = errorCallback
		) {
			GoldStoneAPI.context.runOnUiThread {
				hold(toArrayList())
			}
		}
	}
	
	@JvmStatic
	fun getShareContent(
		errorCallback: (Exception) -> Unit,
		hold: (ShareContentModel) -> Unit
	) {
		// Get server copywriting when user click share software button everytime
		// this is an important behavior so it need to set a short connect timeout value
		val timeOutValue = 3L
		requestData<String>(
			APIPath.getShareContent,
			"data",
			true,
			errorCallback,
			timeOutValue
		) {
			this[0].isNotNull {
				hold(ShareContentModel(JSONObject(this[0])))
			}
		}
	}
	
	@JvmStatic
	fun getMarketSearchList(
		pair: String,
		errorCallback: (Exception) -> Unit,
		hold: (ArrayList<QuotationSelectionTable>) -> Unit
	) {
		requestData<QuotationSelectionTable>(
			APIPath.marketSearch + pair,
			"pair_list",
			errorCallback = errorCallback
		) {
			hold(toArrayList())
		}
	}
	
	fun getERC20TokenIncomingTransaction(
		startBlock: String = "0",
		address: String = Config.getCurrentAddress(),
		errorCallback: (Exception) -> Unit,
		hold: (ArrayList<ERC20TransactionModel>) -> Unit
	) {
		requestUncryptoData<ERC20TransactionModel>(
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
		errorCallback: (Exception) -> Unit,
		address: String = Config.getCurrentAddress(),
		hold: ArrayList<TransactionTable>.() -> Unit
	) {
		requestUncryptoData<TransactionTable>(
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
		chainID: Int,
		errorCallback: (Exception) -> Unit,
		hold: (String) -> Unit
	) {
		RequestBody.create(
			requestContentType,
			AesCrypto.encrypt(
				"{\"language\":\"$language\", \"cid\":\"$pushToken\", \"device\":\"$deviceID\",\"push_type\":$isChina, \"os\":$isAndroid, \"chainid\":$chainID}"
			).orEmpty()
		).let {
			postRequest(it, APIPath.registerDevice, errorCallback) {
				hold(it)
			}
		}
	}
	
	fun getCurrencyLineChartData(
		pairList: JsonArray,
		errorCallback: (Exception) -> Unit = {},
		hold: (ArrayList<QuotationSelectionLineChartModel>) -> Unit
	) {
		RequestBody.create(
			requestContentType,
			AesCrypto.encrypt("{\"pair_list\":$pairList}").orEmpty()
		).let {
			postRequestGetJsonObject<QuotationSelectionLineChartModel>(
				it,
				"data_list",
				APIPath.getCurrencyLineChartData,
				errorCallback = errorCallback
			) {
				hold(it.toArrayList())
			}
		}
	}
	
	fun registerWalletAddress(
		addressList: JsonArray,
		deviceID: String,
		errorCallback: (Exception) -> Unit = {},
		hold: (String) -> Unit
	) {
		RequestBody.create(
			requestContentType,
			AesCrypto.encrypt("{\"address_list\":$addressList,\"device\":\"$deviceID\"}").orEmpty()
		).let {
			postRequest(it, APIPath.updateAddress, errorCallback) {
				hold(it)
			}
		}
	}
	
	fun getUnreadCount(
		deviceID: String,
		time: Long,
		errorCallback: (Exception) -> Unit = {},
		hold: (String) -> Unit
	) {
		RequestBody.create(
			requestContentType, AesCrypto.encrypt("{\"device\":\"$deviceID\",\"time\":$time}").orEmpty()
		).let {
			postRequest(
				it,
				APIPath.getUnreadCount,
				errorCallback
			) {
				hold(JSONObject(it).safeGet("count"))
			}
		}
	}
	
	fun getNotificationList(
		goldSonteID: String,
		time: Long,
		errorCallback: (Exception) -> Unit,
		hold: (ArrayList<NotificationTable>) -> Unit
	) {
		// 加密 `Post` 请求
		val content = AesCrypto.encrypt("{\"device\":\"$goldSonteID\",\"time\":$time}").orEmpty()
		RequestBody.create(requestContentType, content).let {
			postRequestGetJsonObject<String>(
				it,
				"message_list",
				APIPath.getNotification,
				true,
				errorCallback
			) {
				// 因为返回的数据格式复杂这里采用自己处理数据的方式, 不实用 `Gson`
				val notificationData = arrayListOf<NotificationTable>()
				val jsonarray = JSONArray(it[0])
				GoldStoneAPI.context.runOnUiThread {
					if (jsonarray.length() == 0) {
						hold(arrayListOf())
					} else {
						(0 until jsonarray.length()).forEach {
							notificationData.add(NotificationTable(JSONObject(jsonarray[it].toString())))
							if (it == jsonarray.length() - 1) {
								hold(notificationData)
							}
						}
					}
				}
			}
		}
	}
	
	fun getPriceByContractAddress(
		addressList: JsonArray,
		errorCallback: (Exception) -> Unit,
		hold: (ArrayList<TokenPriceModel>) -> Unit
	) {
		// 加密 `Post` 请求
		val content = AesCrypto.encrypt("{\"address_list\":$addressList}").orEmpty()
		RequestBody.create(requestContentType, content).let {
			postRequestGetJsonObject<TokenPriceModel>(
				it,
				"price_list",
				APIPath.getPriceByAddress,
				errorCallback = errorCallback
			) {
				GoldStoneAPI.context.runOnUiThread {
					hold(it.toArrayList())
				}
			}
		}
	}
	
	fun getQuotationCurrencyChart(
		pair: String,
		period: String,
		size: Int,
		errorCallback: (Exception) -> Unit,
		hold: (ArrayList<ChartModel>) -> Unit
	) {
		requestData<ChartModel>(
			api = APIPath.getQuotationCurrencyChart(pair, period, size),
			keyName = "point_list",
			errorCallback = errorCallback
		) {
			hold(this.toArrayList())
		}
	}
	
	fun getQuotationCurrencyInfo(
		pair: String,
		errorCallback: (Exception) -> Unit,
		hold: (JSONObject) -> Unit
	) {
		requestData<String>(
			APIPath.getQuotationCurrencyInfo(pair),
			"",
			true,
			errorCallback
		) {
			hold(JSONObject(this[0]))
		}
	}
	
	fun getQuotationCurrencyDescription(
		symbol: String,
		errorCallback: (Exception) -> Unit,
		hold: (String) -> Unit
	) {
		requestData<String>(
			APIPath.getTokenDescription + symbol,
			"",
			true,
			errorCallback
		) {
			this[0].let {
				hold(JSONObject(it).safeGet("description"))
			}
		}
	}
}





