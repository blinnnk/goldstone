package io.goldstone.blockchain.kernel.network

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.blinnnk.extension.forEachOrEnd
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.toArrayList
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.common.utils.AesCrypto
import io.goldstone.blockchain.common.utils.safeGet
import io.goldstone.blockchain.crypto.getObjectMD5HexString
import io.goldstone.blockchain.crypto.toJsonObject
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.ChartModel
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionLineChartModel
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.model.TokenSearchModel
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TinyNumber
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.ERC20TransactionModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.TokenPriceModel
import okhttp3.*
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.StreamCorruptedException

@SuppressLint("StaticFieldLeak")
/**
 * @date 31/03/2018 8:08 PM
 * @author KaySaith
 */

object GoldStoneAPI {

	/** 网络请求很多是全台异步所以使用 `Application` 的 `Context` */
	lateinit var context: Context

	/**
	 * 从服务器获取产品指定的默认的 `DefaultTokenList`
	 */
	@JvmStatic
	fun getDefaultTokens(
		errorCallback: () -> Unit = {},
		hold: (ArrayList<DefaultTokenTable>) -> Unit
	) {
		requestData<DefaultTokenTable>(APIPath.defaultTokenList, "list", false, {
			errorCallback()
		}) {
			forEachOrEnd { token, isEnd ->
				if (token.forceShow == TinyNumber.True.value) token.isUsed = true
				if (isEnd) hold(toArrayList())
			}
		}
	}

	@JvmStatic
	fun getCoinInfoBySymbolFromGoldStone(
		symbols: String,
		hold: (ArrayList<TokenSearchModel>) -> Unit
	) {
		requestData<TokenSearchModel>(APIPath.getCoinInfo + symbols, "list") {
			hold(toArrayList())
		}
	}

	@JvmStatic
	fun getCurrencyRate(
		symbols: String,
		hold: (Double) -> Unit
	) {
		requestData<String>(
			APIPath.getCurrencyRate + symbols, "rate", true
		) {
			this[0].isNotNull { hold(this[0].toDouble()) }
		}
	}

	@JvmStatic
	fun getCountryList(hold: (ArrayList<String>) -> Unit) {
		requestList<String>(
			APIPath.getCountryList, "list"
		) {
			hold(this.toArrayList())
		}
	}

	fun getERC20TokenTransaction(
		address: String,
		startBlock: String = "0",
		hold: (ArrayList<ERC20TransactionModel>) -> Unit
	) {
		requestData<ERC20TransactionModel>(
			EtherScanApi.getAllTokenTransaction(address, startBlock), "result"
		) {
			hold(toArrayList())
		}
	}

	@JvmStatic
	fun getMarketSearchList(
		pair: String,
		hold: (ArrayList<QuotationSelectionTable>) -> Unit
	) {
		requestData<QuotationSelectionTable>(APIPath.marketSearch + pair, "pair_list") {
			hold(toArrayList())
		}
	}

	fun getERC20TokenIncomingTransaction(
		startBlock: String = "0",
		address: String = WalletTable.current.address,
		hold: (ArrayList<ERC20TransactionModel>) -> Unit
	) {
		requestUncryptoData<ERC20TransactionModel>(
			EtherScanApi.getTokenIncomingTransaction(
				address, startBlock
			), "result"
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
		address: String = WalletTable.current.address,
		hold: ArrayList<TransactionTable>.() -> Unit
	) {
		requestUncryptoData<TransactionTable>(
			EtherScanApi.transactions(address, startBlock), "result"
		) {
			hold(toArrayList())
		}
	}

	fun registerDevice(
		language: String,
		pushToken: String,
		deviceID: String,
		isChina: Int,
		isAndroid: Int,
		hold: (String) -> Unit
	) {
		val contentType = MediaType.parse("application/json; charset=utf-8")
		RequestBody.create(
			contentType, AesCrypto.encrypt(
				"{\"language\":\"$language\",\"cid\":\"$pushToken\",\"device\":\"$deviceID\",\"push_type\":$isChina,\"os\":$isAndroid}"
			).orEmpty()
		).let {
			postRequest(it, APIPath.registerDevice) {
				hold(it)
			}
		}
	}

	fun getCurrencyLineChartData(
		pairList: JsonArray,
		hold: (ArrayList<QuotationSelectionLineChartModel>) -> Unit
	) {
		val contentType = MediaType.parse("application/json; charset=utf-8")
		RequestBody.create(
			contentType, AesCrypto.encrypt("{\"pair_list\":$pairList}").orEmpty()
		).let {
			postRequestGetJsonObject<QuotationSelectionLineChartModel>(
				it, "data_list", APIPath.getCurrencyLineChartData
			) {
				hold(it.toArrayList())
			}
		}
	}

	fun registerWalletAddress(
		addressList: JsonArray,
		deviceID: String,
		netWorkError: () -> Unit = {},
		hold: (String) -> Unit
	) {
		val contentType = MediaType.parse("application/json; charset=utf-8")
		RequestBody.create(
			contentType,
			AesCrypto.encrypt("{\"address_list\":$addressList,\"device\":\"$deviceID\"}").orEmpty()
		).let {
			postRequest(it, APIPath.updateAddress, netWorkError) {
				hold(it)
			}
		}
	}

	fun getUnreadCount(
		deviceID: String,
		time: Long,
		netWorkError: () -> Unit = {},
		hold: (String) -> Unit
	) {
		val contentType = MediaType.parse("application/json; charset=utf-8")
		RequestBody.create(
			contentType, AesCrypto.encrypt("{\"device\":\"$deviceID\",\"time\":\"$time\"}").orEmpty()
		).let {

			postRequest(it, APIPath.getUnreadCount, netWorkError) {
				hold(it)
			}
		}
	}

	fun getNotificationList(
		goldSonteID: String,
		time: Long,
		hold: (ArrayList<NotificationTable>) -> Unit
	) {
		val contentType = MediaType.parse("application/json; charset=utf-8")
		// 加密 `Post` 请求
		val content = AesCrypto.encrypt("{\"device\":\"$goldSonteID\",\"time\":$time}").orEmpty()
		RequestBody.create(contentType, content).let {
			postRequestGetJsonObject<NotificationTable>(it, "message_list", APIPath.getNotification) {
				GoldStoneAPI.context.runOnUiThread {
					hold(it.toArrayList())
				}
			}
		}
	}

	fun getPriceByContractAddress(
		addressList: JsonArray,
		errorCallback: () -> Unit,
		hold: (ArrayList<TokenPriceModel>) -> Unit
	) {
		val contentType = MediaType.parse("application/json; charset=utf-8")
		// 加密 `Post` 请求
		val content = AesCrypto.encrypt("{\"address_list\":$addressList}").orEmpty()
		RequestBody.create(contentType, content).let {
			postRequestGetJsonObject<TokenPriceModel>(
				it, "price_list",
				APIPath.getPriceByAddress,
				errorCallback = { errorCallback() }) {
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
		hold: (ArrayList<ChartModel>) -> Unit
	) {
		requestData<ChartModel>(APIPath.getQuotationCurrencyChart(pair, period, size), "point_list") {
			hold(this.toArrayList())
		}
	}

	fun getQuotationCurrencyInfo(
		pair: String,
		hold: (JSONObject) -> Unit
	) {
		requestData<String>(APIPath.getQuotationCurrencyInfo(pair), "", true) {
			hold(JSONObject(this[0]))
		}
	}

	fun getQuotationCurrencyDescription(
		symbol: String,
		hold: (String) -> Unit
	) {
		requestData<String>(
			APIPath.getTokenDescription + symbol, "", true
		) {
			this[0].let {
				hold(JSONObject(it).safeGet("description"))
			}
		}
	}

	/**————————————————————— public network request method ———————————————————————*/

	private inline fun <reified T> postRequestGetJsonObject(
		body: RequestBody,
		keyName: String,
		path: String,
		noinline errorCallback: () -> Unit = {},
		crossinline hold: (List<T>) -> Unit
	) {
		val client = OkHttpClient()
		getcryptoRequest(body, path) {
			client.newCall(it).enqueue(object : Callback {
				override fun onFailure(
					call: Call,
					error: IOException
				) {
					Log.e("ERROR", error.toString())
				}

				override fun onResponse(
					call: Call,
					response: Response
				) {
					val data = AesCrypto.decrypt(response.body()?.string().orEmpty())
					try {
						val dataObject =
							data?.toJsonObject()
								?: JSONObject("")
						val jsonData = dataObject[keyName].toString()
						val gson = Gson()
						val collectionType = object : TypeToken<Collection<T>>() {}.type
						hold(gson.fromJson(jsonData, collectionType))
					} catch (error: Exception) {
						GoldStoneCode.showErrorCodeReason(data, errorCallback)
					}
				}
			})
		}

	}

	private fun postRequest(
		body: RequestBody,
		path: String,
		netWorkError: () -> Unit = {},
		hold: (String) -> Unit
	) {
		val client = OkHttpClient()
		getcryptoRequest(body, path) {
			client.newCall(it).enqueue(object : Callback {
				override fun onFailure(
					call: Call,
					error: IOException
				) {
					Log.e("ERROR", error.toString())
					netWorkError()
				}

				override fun onResponse(
					call: Call,
					response: Response
				) {
					val data = AesCrypto.decrypt(response.body()?.string().orEmpty())
					try {
						hold(data.orEmpty())
					} catch (error: Exception) {
						Log.e("ERROR", error.toString())
					}
				}
			})
		}
	}

	@JvmStatic
	private inline fun <reified T> requestData(
		api: String,
		keyName: String,
		justGetData: Boolean = false,
		crossinline netWorkError: () -> Unit = {},
		crossinline hold: List<T>.() -> Unit
	) {
		val client = OkHttpClient()
		getcryptoGetRequest(api) {
			client.newCall(it).enqueue(object : Callback {
				override fun onFailure(
					call: Call,
					error: IOException
				) {
					netWorkError()
					println("$error")
				}

				override fun onResponse(
					call: Call,
					response: Response
				) {
					val data = AesCrypto.decrypt(response.body()?.string().orEmpty())
					try {
						val dataObject =
							data?.toJsonObject()
								?: JSONObject("")
						val jsonData = if (keyName.isEmpty()) data else dataObject[keyName].toString()
						if (justGetData) {
							hold(listOf(jsonData as T))
						} else {
							val gson = Gson()
							val collectionType = object : TypeToken<Collection<T>>() {}.type
							hold(gson.fromJson(jsonData, collectionType))
						}
					} catch (error: Exception) {
						GoldStoneCode.showErrorCodeReason(data)
					}
				}
			})
		}
	}

	/** 请求 ehterScan 的数据是明文请求不需要加密 */
	@JvmStatic
	private inline fun <reified T> requestUncryptoData(
		api: String,
		keyName: String,
		justGetData: Boolean = false,
		crossinline hold: List<T>.() -> Unit
	) {
		val client = OkHttpClient()
		val request = Request.Builder().url(api).build()
		client.newCall(request).enqueue(object : Callback {
			override fun onFailure(
				call: Call,
				error: IOException
			) {
				println("$error")
			}

			override fun onResponse(
				call: Call,
				response: Response
			) {
				val data = response.body()?.string()
				try {
					val dataObject =
						data?.toJsonObject()
							?: JSONObject("")
					val jsonData = if (keyName.isEmpty()) data else dataObject[keyName].toString()
					if (justGetData) {
						hold(listOf(jsonData as T))
					} else {
						val gson = Gson()
						val collectionType = object : TypeToken<Collection<T>>() {}.type
						hold(gson.fromJson(jsonData, collectionType))
					}
				} catch (error: Exception) {
					GoldStoneCode.showErrorCodeReason(data)
				}
			}
		})
	}

	@JvmStatic
	private inline fun <reified T> requestList(
		api: String,
		keyName: String,
		crossinline hold: List<T>.() -> Unit
	) {
		val client = OkHttpClient()
		getcryptoGetRequest(api) {
			client.newCall(it).enqueue(object : Callback {
				override fun onFailure(
					call: Call,
					error: IOException
				) {
					Log.e("ERROR", error.toString())
				}

				override fun onResponse(
					call: Call,
					response: Response
				) {
					val data = AesCrypto.decrypt(response.body()?.string().orEmpty())
					try {
						val dataObject =
							data?.toJsonObject()
								?: JSONObject("")
						val jsonArray = dataObject[keyName] as JSONArray
						val dataArray = arrayListOf<T>()
						(0 until jsonArray.length()).forEachOrEnd { index, isEnd ->
							dataArray.add(jsonArray.get(index) as T)
							if (isEnd) {
								hold(dataArray)
							}
						}
					} catch (error: Exception) {
						GoldStoneCode.showErrorCodeReason(data)
					}
				}
			})
		}
	}

	/** —————————————————— header 加密请求参数准备 ——————————————————————*/
	private const val secretKey = "gPBZ[5Ms#dn@]4oN,{86"

	private fun getcryptoRequest(
		body: RequestBody,
		path: String,
		callback: (Request) -> Unit
	) {
		val timeStamp = System.currentTimeMillis().toString()
		val version = "1.0.0"
		AppConfigTable.getAppConfig {
			it?.apply {
				val sign =
					(goldStoneID + "0" + secretKey + timeStamp + version).getObjectMD5HexString()
						.removePrefix("0x")
				val request =
					Request.Builder().url(path).method("POST", body)
						.header("Content-type", "application/json").addHeader("device", goldStoneID)
						.addHeader("timestamp", timeStamp).addHeader("os", "0").addHeader("version", version)
						.addHeader("sign", sign).build()
				callback(request)
			}
		}
	}

	fun getcryptoGetRequest(
		api: String,
		callback: (Request) -> Unit
	) {
		val timeStamp = System.currentTimeMillis().toString()
		val version = "1.0.0"
		AppConfigTable.getAppConfig {
			it?.apply {
				val sign =
					(goldStoneID + "0" + secretKey + timeStamp + version).getObjectMD5HexString()
						.removePrefix("0x")
				val request =
					Request.Builder().url(api).header("Content-type", "application/json")
						.addHeader("device", goldStoneID).addHeader("timestamp", timeStamp).addHeader("os", "0")
						.addHeader("version", version).addHeader("sign", sign).build()
				callback(request)
			}
		}
	}
}

object GoldStoneCode {
	fun isSuccess(
		code: Any,
		callback: (isSuccessful: Boolean) -> Unit
	) {
		if (code == 0) callback(true)
		else {
			callback(false)
			Log.e("ERROR", "Wrong Code")
		}
	}

	fun showErrorCodeReason(
		data: String?,
		errorCallback: () -> Unit = {}
	) {
		data?.apply {
			val code = JSONObject(this).safeGet("code")
			if (code.isNotEmpty()) {
				when (code.toInt()) {
					-1 -> {
						errorCallback()
						Log.e("ERROR", "Server Error GoldStone")
					}

					-4 -> {
						errorCallback()
						Log.e("ERROR", "Url Error")
						/**
						 *  `Device` 错误, `APi URL` 是否正确, `API` 文档是否有错误
						 */
					}
				}
			}
		}
	}
}





