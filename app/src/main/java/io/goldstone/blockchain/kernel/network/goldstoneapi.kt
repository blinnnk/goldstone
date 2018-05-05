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
import okhttp3.*
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

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
	fun getDefaultTokens(hold: (ArrayList<DefaultTokenTable>) -> Unit) {
		requestData<DefaultTokenTable>(APIPath.defaultTokenList, "list") {
			forEachOrEnd { token, isEnd ->
				if (token.forceShow == TinyNumber.True.value) token.isUsed = true
				if (isEnd) hold(toArrayList())
			}
		}
	}

	@JvmStatic
	fun getCoinInfoBySymbolFromGoldStone(
		symbols: String, hold: (ArrayList<TokenSearchModel>) -> Unit
	) {
		requestData<TokenSearchModel>(APIPath.getCoinInfo + symbols, "list") {
			hold(toArrayList())
		}
	}

	@JvmStatic
	fun getCurrencyRate(symbols: String, hold: (Double) -> Unit) {
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
		address: String, startBlock: String = "0", hold: (ArrayList<ERC20TransactionModel>) -> Unit
	) {
		requestData<ERC20TransactionModel>(
			EtherScanApi.getAllTokenTransaction(address, startBlock), "result"
		) {
			hold(toArrayList())
		}
	}

	@JvmStatic
	fun getMarketSearchList(pair: String, hold: (ArrayList<QuotationSelectionTable>) -> Unit) {
		requestData<QuotationSelectionTable>(APIPath.marketSearch + pair, "pair_list") {
			hold(toArrayList())
		}
	}

	fun getERC20TokenIncomingTransaction(
		startBlock: String = "0",
		address: String = WalletTable.current.address,
		hold: (ArrayList<ERC20TransactionModel>) -> Unit
	) {
		requestData<ERC20TransactionModel>(
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
		requestData<TransactionTable>(EtherScanApi.transactions(address, startBlock), "result") {
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
			contentType,
			"{\"language\":\"$language\",\"cid\":\"$pushToken\",\"device\":\"$deviceID\",\"push_type\":$isChina,\"os\":$isAndroid}"
		).let {
			postRequest(it, APIPath.registerDevice) {
				hold(it)
			}
		}
	}

	fun getCurrencyLineChartData(
		pairList: JsonArray, hold: (ArrayList<QuotationSelectionLineChartModel>) -> Unit
	) {
		val contentType = MediaType.parse("application/json; charset=utf-8")
		RequestBody.create(
			contentType, "{\"pair_list\":$pairList}"
		).let {
			postRequestGetJsonObject<QuotationSelectionLineChartModel>(
				it, "data_list", APIPath.getCurrencyLineChartData
			) {
				hold(it.toArrayList())
			}
		}
	}

	fun registerWalletAddress(
		addressList: JsonArray, deviceID: String, netWorkError: () -> Unit = {}, hold: (String) -> Unit
	) {
		val contentType = MediaType.parse("application/json; charset=utf-8")
		RequestBody.create(contentType, "{\"address_list\":$addressList,\"device\":\"$deviceID\"}")
			.let {
				postRequest(it, APIPath.updateAddress, netWorkError) {
					hold(it)
				}
			}
	}

	fun getNotificationList(
		goldSonteID: String, time: Long, hold: (ArrayList<NotificationTable>) -> Unit
	) {
		val contentType = MediaType.parse("application/json; charset=utf-8")
		RequestBody.create(contentType, "{\"device\":\"$goldSonteID\",\"time\":$time}").let {
			postRequestGetJsonObject<NotificationTable>(it, "message_list", APIPath.getNotification) {
				GoldStoneAPI.context.runOnUiThread {
					hold(it.toArrayList())
				}
			}
		}
	}

	fun getQuotationCurrencyChart(
		pair: String, period: String, size: Int, hold: (ArrayList<ChartModel>) -> Unit
	) {
		requestData<ChartModel>(APIPath.getQuotationCurrencyChart(pair, period, size), "point_list") {
			hold(this.toArrayList())
		}
	}

	fun getQuotationCurrencyInfo(pair: String, hold: (JSONObject) -> Unit) {
		requestData<String>(APIPath.getQuotationCurrencyInfo(pair), "", true) {
			hold(JSONObject(this[0]))
		}
	}

	private inline fun <reified T> postRequestGetJsonObject(
		body: RequestBody, keyName: String, path: String, crossinline hold: (List<T>) -> Unit
	) {
		val client = OkHttpClient()
		val request =
			Request.Builder().url(path).method("POST", body).header("Content-type", "application/json")
				.build()
		client.newCall(request).enqueue(object : Callback {
			override fun onFailure(call: Call, error: IOException) {
				println("$error")
			}

			@SuppressLint("SetTextI18n")
			override fun onResponse(call: Call, response: Response) {
				val data = response.body()?.string()
				try {
					val dataObject = JSONObject(data?.substring(data.indexOf("{"), data.lastIndexOf("}") + 1))
					val jsonData = dataObject[keyName].toString()
					val gson = Gson()
					val collectionType = object : TypeToken<Collection<T>>() {}.type
					hold(gson.fromJson(jsonData, collectionType))
				} catch (error: Exception) {
					println("GoldStoneApi $error and $data")
				}
			}
		})
	}

	private fun postRequest(
		body: RequestBody, path: String, netWorkError: () -> Unit = {}, hold: (String) -> Unit
	) {
		val client = OkHttpClient()
		val request =
			Request.Builder().url(path).method("POST", body).header("Content-type", "application/json")
				.build()
		client.newCall(request).enqueue(object : Callback {
			override fun onFailure(call: Call, error: IOException) {
				Log.e("ERROR", error.toString())
				netWorkError()
			}

			@SuppressLint("SetTextI18n")
			override fun onResponse(call: Call, response: Response) {
				val data = response.body()?.string()
				try {
					hold(data.orEmpty())
				} catch (error: Exception) {
					Log.e("ERROR", error.toString())
				}
			}
		})
	}

	@JvmStatic
	private inline fun <reified T> requestData(
		api: String, keyName: String, justGetData: Boolean = false, crossinline hold: List<T>.() -> Unit
	) {
		val client = OkHttpClient()
		val request = Request.Builder().url(api).build()
		client.newCall(request).enqueue(object : Callback {
			override fun onFailure(call: Call, error: IOException) {
				println("$error")
			}

			override fun onResponse(call: Call, response: Response) {
				val data = response.body()?.string()
				try {
					val dataObject = JSONObject(data?.substring(data.indexOf("{"), data.lastIndexOf("}") + 1))
					val jsonData = if (keyName.isEmpty()) data else dataObject[keyName].toString()
					if (justGetData) {
						hold(listOf(jsonData as T))
					} else {
						val gson = Gson()
						val collectionType = object : TypeToken<Collection<T>>() {}.type
						hold(gson.fromJson(jsonData, collectionType))
					}
				} catch (error: Exception) {
					println("GoldStoneApi $error and $data")
				}
			}
		})
	}

	@JvmStatic
	private inline fun <reified T> requestList(
		api: String, keyName: String, crossinline hold: List<T>.() -> Unit
	) {
		val client = OkHttpClient()
		val request = Request.Builder().url(api).build()
		client.newCall(request).enqueue(object : Callback {
			override fun onFailure(call: Call, error: IOException) {
				Log.e("ERROR", error.toString())
			}

			override fun onResponse(call: Call, response: Response) {
				val data = response.body()?.string()
				try {
					val dataObject = JSONObject(data?.substring(data.indexOf("{"), data.lastIndexOf("}") + 1))
					val jsonArray = dataObject[keyName] as JSONArray
					val dataArray = arrayListOf<T>()
					(0 until jsonArray.length()).forEachOrEnd { index, isEnd ->
						dataArray.add(jsonArray.get(index) as T)
						if (isEnd) {
							hold(dataArray)
						}
					}
				} catch (error: Exception) {
					println("GoldStoneApi Multiple $error")
				}
			}
		})
	}
}

object GoldStoneCode {
	fun isSuccess(code: Any, callback: (isSuccessful: Boolean) -> Unit) {
		if (code == 0) callback(true)
		else {
			callback(false)
			Log.e("ERROR", "Wrong Code")
		}
	}
}





