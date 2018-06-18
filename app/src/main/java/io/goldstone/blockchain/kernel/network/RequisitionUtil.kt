package io.goldstone.blockchain.kernel.network

import com.blinnnk.extension.safeGet
import com.blinnnk.util.SystemUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.common.utils.AesCrypto
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.GoldStoneCrayptoKey
import io.goldstone.blockchain.crypto.toJsonObject
import io.goldstone.blockchain.crypto.utils.getObjectMD5HexString
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import okhttp3.*
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * @date 2018/6/12 4:34 PM
 * @author KaySaith
 */
object RequisitionUtil {
	
	inline fun <reified T> postRequestGetJsonObject(
		body: RequestBody,
		keyName: String,
		path: String,
		justData: Boolean = false,
		noinline errorCallback: (Exception) -> Unit,
		crossinline hold: (List<T>) -> Unit
	) {
		val client =
			OkHttpClient
				.Builder()
				.connectTimeout(20, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS)
				.build()
		
		getcryptoRequest(body, path) {
			client.newCall(it).enqueue(object : Callback {
				override fun onFailure(call: Call, error: IOException) {
					GoldStoneAPI.context.runOnUiThread {
						errorCallback(error)
					}
					LogUtil.error(path, error)
				}
				
				override fun onResponse(
					call: Call,
					response: Response
				) {
					val data = AesCrypto.decrypt(response.body()?.string().orEmpty())
					try {
						val dataObject = data?.toJsonObject() ?: JSONObject("")
						val jsonData = dataObject[keyName].toString()
						if (justData) {
							hold(listOf(jsonData as T))
							return
						}
						val gson = Gson()
						val collectionType = object : TypeToken<Collection<T>>() {}.type
						hold(gson.fromJson(jsonData, collectionType))
					} catch (error: Exception) {
						LogUtil.error(keyName, error)
						GoldStoneCode.showErrorCodeReason(data, {
							GoldStoneAPI.context.runOnUiThread {
								errorCallback(error)
							}
						})
					}
				}
			})
		}
	}
	
	fun postRequest(
		body: RequestBody,
		path: String,
		netWorkError: (Exception) -> Unit,
		hold: (String) -> Unit
	) {
		val client =
			OkHttpClient
				.Builder()
				.connectTimeout(20, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS)
				.build()
		
		getcryptoRequest(body, path) {
			client.newCall(it).enqueue(object : Callback {
				override fun onFailure(call: Call, error: IOException) {
					LogUtil.error(path, error)
					GoldStoneAPI.context.runOnUiThread {
						netWorkError(error)
					}
				}
				
				override fun onResponse(call: Call, response: Response) {
					val data = AesCrypto.decrypt(response.body()?.string().orEmpty())
					try {
						hold(data.orEmpty())
					} catch (error: Exception) {
						GoldStoneAPI.context.runOnUiThread {
							netWorkError(error)
						}
						LogUtil.error(path, error)
					}
				}
			})
		}
	}
	
	@JvmStatic
	inline fun <reified T> requestData(
		api: String,
		keyName: String,
		justGetData: Boolean = false,
		crossinline errorCallback: (Exception) -> Unit,
		maxConnectTime: Long = 20,
		crossinline hold: List<T>.() -> Unit
	) {
		val client =
			OkHttpClient
				.Builder()
				.connectTimeout(maxConnectTime, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS)
				.build()
		
		getcryptGetRequest(api) {
			client.newCall(it).enqueue(object : Callback {
				override fun onFailure(call: Call, error: IOException) {
					GoldStoneAPI.context.runOnUiThread {
						errorCallback(error)
					}
					LogUtil.error(keyName + "requestData", error)
				}
				
				override fun onResponse(call: Call, response: Response) {
					val data = AesCrypto.decrypt(response.body()?.string().orEmpty())
					try {
						val dataObject = data?.toJsonObject() ?: JSONObject("")
						val jsonData = if (keyName.isEmpty()) data else dataObject[keyName].toString()
						if (justGetData) {
							hold(listOf(jsonData as T))
						} else {
							val gson = Gson()
							val collectionType = object : TypeToken<Collection<T>>() {}.type
							hold(gson.fromJson(jsonData, collectionType))
						}
					} catch (error: Exception) {
						GoldStoneAPI.context.runOnUiThread {
							errorCallback(error)
						}
						GoldStoneCode.showErrorCodeReason(data)
						LogUtil.error("$keyName requestData", error)
					}
				}
			})
		}
	}
	
	/** 请求 ehterScan 的数据是明文请求不需要加密 */
	@JvmStatic
	inline fun <reified T> requestUncryptoData(
		api: String,
		keyName: String,
		justGetData: Boolean = false,
		crossinline errorCallback: (Exception) -> Unit = {},
		crossinline hold: List<T>.() -> Unit
	) {
		val client =
			OkHttpClient
				.Builder()
				.connectTimeout(20, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS)
				.build()
		val request = Request.Builder().url(api).build()
		client.newCall(request).enqueue(object : Callback {
			override fun onFailure(call: Call, error: IOException) {
				GoldStoneAPI.context.runOnUiThread { errorCallback(error) }
				LogUtil.error(keyName, error)
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
					GoldStoneAPI.context.runOnUiThread { errorCallback(error) }
					LogUtil.error(keyName, error)
					GoldStoneCode.showErrorCodeReason(data)
				}
			}
		})
	}
	
	/** —————————————————— header 加密请求参数准备 ——————————————————————*/
	fun getcryptoRequest(
		body: RequestBody,
		path: String,
		callback: (Request) -> Unit
	) {
		val timeStamp = System.currentTimeMillis().toString()
		val version = SystemUtils.getVersionCode(GoldStoneAPI.context).toString()
		AppConfigTable.getAppConfig {
			it?.apply {
				val sign =
					(goldStoneID + "0" + GoldStoneCrayptoKey.apiKey + timeStamp + version)
						.getObjectMD5HexString()
						.removePrefix("0x")
				val request =
					Request.Builder().url(path).method("POST", body)
						.header("Content-type", "application/json")
						.addHeader("device", goldStoneID)
						.addHeader("timestamp", timeStamp)
						.addHeader("os", "0")
						.addHeader("version", version)
						.addHeader("sign", sign)
						.addHeader("chainid", chainID)
						.build()
				callback(request)
			}
		}
	}
	
	fun getcryptGetRequest(
		api: String,
		callback: (Request) -> Unit
	) {
		val timeStamp = System.currentTimeMillis().toString()
		val version = SystemUtils.getVersionCode(GoldStoneAPI.context).toString()
		AppConfigTable.getAppConfig {
			it?.apply {
				val sign =
					(goldStoneID + "0" + GoldStoneCrayptoKey.apiKey + timeStamp + version)
						.getObjectMD5HexString()
						.removePrefix("0x")
				val request =
					Request.Builder()
						.url(api)
						.header("Content-type", "application/json")
						.addHeader("device", goldStoneID)
						.addHeader("timestamp", timeStamp)
						.addHeader("os", "0")
						.addHeader("version", version)
						.addHeader("sign", sign)
						.addHeader("chainid", chainID)
						.build()
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
			LogUtil.error("function: GoldStoneCode, wrongCode: $code")
		}
	}
	
	fun showErrorCodeReason(data: String?, errorCallback: () -> Unit = {}) {
		data?.apply {
			val code = try {
				JSONObject(this).safeGet("code")
			} catch (error: Exception) {
				"100"
			}
			if (code.isNotEmpty()) {
				when (code.toInt()) {
					-1 -> {
						GoldStoneAPI.context.runOnUiThread {
							errorCallback()
						}
						LogUtil.error("Server Error GoldStone")
					}
					
					-4 -> {
						GoldStoneAPI.context.runOnUiThread {
							errorCallback()
						}
						LogUtil.error("Url Error")
						/**
						 *  `Device` 错误, `APi URL` 是否正确, `API` 文档是否有错误
						 */
					}
				}
			}
		}
	}
}