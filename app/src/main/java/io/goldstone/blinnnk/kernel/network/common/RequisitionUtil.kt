package io.goldstone.blinnnk.kernel.network.common

import android.support.annotation.WorkerThread
import android.util.Log
import com.blinnnk.extension.getTargetChild
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.orZero
import com.blinnnk.extension.safeGet
import com.blinnnk.util.SystemUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blinnnk.GoldStoneApp
import io.goldstone.blinnnk.common.error.RequestError
import io.goldstone.blinnnk.common.language.HoneyLanguage
import io.goldstone.blinnnk.common.language.currentLanguage
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.common.utils.AesCrypto
import io.goldstone.blinnnk.common.value.ErrorTag
import io.goldstone.blinnnk.common.value.GoldStoneCryptoKey
import io.goldstone.blinnnk.common.value.currentChannel
import io.goldstone.blinnnk.crypto.keystore.toJsonObject
import io.goldstone.blinnnk.crypto.multichain.node.ChainURL
import io.goldstone.blinnnk.crypto.utils.getObjectMD5HexString
import io.goldstone.blinnnk.kernel.network.ethereum.ETHJsonRPC
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * @date 2018/6/12 4:34 PM
 * @author KaySaith
 */
object RequisitionUtil {

	val client = OkHttpClient
		.Builder()
		.connectTimeout(30, TimeUnit.SECONDS)
		.readTimeout(60, TimeUnit.SECONDS)
		.build()

	fun post(
		condition: String,
		api: String,
		isEncrypt: Boolean,
		@WorkerThread hold: (result: String?, error: RequestError) -> Unit
	) {
		postRequest<String>(
			RequestBody.create(ETHJsonRPC.contentType, condition),
			"",
			api,
			true,
			isEncrypt
		) { result, error ->
			hold(result?.firstOrNull(), error)
		}
	}

	inline fun <reified T> post(
		condition: String,
		api: String,
		keyName: String,
		isEncrypt: Boolean,
		@WorkerThread noinline hold: (result: List<T>?, error: RequestError) -> Unit
	) {
		postRequest(
			RequestBody.create(ETHJsonRPC.contentType, condition),
			keyName,
			api,
			false,
			isEncrypt,
			hold
		)
	}

	inline fun <reified T> postSingle(
		condition: String,
		api: String,
		keyName: String,
		isEncrypt: Boolean,
		@WorkerThread noinline holdSingle: (result: T?, error: RequestError) -> Unit
	) {
		postRequest<T>(
			RequestBody.create(ETHJsonRPC.contentType, condition),
			keyName,
			api,
			false,
			isEncrypt
		) { result, error ->
			holdSingle(result?.firstOrNull(), error)
		}
	}

	fun postAndGetTargetKeyValue(
		condition: String,
		api: String,
		keyName: String,
		isEncrypt: Boolean,
		@WorkerThread hold: (result: String?, error: RequestError) -> Unit
	) {
		postRequest<String>(
			RequestBody.create(ETHJsonRPC.contentType, condition),
			keyName,
			api,
			true,
			isEncrypt
		) { result, error ->
			hold(result?.firstOrNull(), error)
		}
	}

	inline fun <reified T> postRequest(
		body: RequestBody,
		keyName: String,
		path: String,
		justData: Boolean,
		isEncrypt: Boolean,
		crossinline hold: (result: List<T>?, error: RequestError) -> Unit
	) {
		generateCryptoPostRequest(body, path, isEncrypt) { requestBody ->
			client.newCall(requestBody).enqueue(object : Callback {
				override fun onFailure(call: Call, error: IOException) {
					hold(null, RequestError.PostFailed("[API: ${path.getKeyName()}]\n[ERROR: $error]\n[API: $path]"))
				}

				override fun onResponse(call: Call, response: Response) {
					val data =
						if (isEncrypt) AesCrypto.decrypt(response.body()?.string().orEmpty())
						else response.body()?.string().orEmpty()
					try {
						val dataObject = data?.toJsonObject() ?: JSONObject("")
						val jsonData = if (keyName.isEmpty()) data else dataObject[keyName].toString()
						if (justData) {
							hold(listOf(jsonData as T), RequestError.None)
							return
						}
						val gson = Gson()
						val collectionType = object : TypeToken<Collection<T>>() {}.type
						hold(gson.fromJson(jsonData, collectionType), RequestError.None)
					} catch (error: Exception) {
						GoldStoneCode.showErrorCodeReason(data)
						hold(null, RequestError.ResolveDataError(error))
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
		targetGoldStoneID: String? = null,
		isEncrypt: Boolean,
		crossinline hold: (result: List<T>?, error: RequestError) -> Unit
	) {
		generateCryptoGetRequest(api, isEncrypt, targetGoldStoneID) {
			client.newCall(it).enqueue(object : Callback {
				override fun onFailure(call: Call, error: IOException) {
					hold(null, RequestError.PostFailed("[API: ${api.getKeyName()}]\n[ERROR: $error]"))
				}

				override fun onResponse(call: Call, response: Response) {
					val data =
						if (isEncrypt) AesCrypto.decrypt(response.body()?.string().orEmpty())
						else response.body()?.string().orEmpty()
					// 结果返回为 `Empty` 或 `Null`
					if (data.isNullOrBlank()) {
						hold(null, RequestError.NullResponse(keyName))
						GoldStoneCode.showErrorCodeReason(data)
					} else try {
						val dataObject = data.toJsonObject()
						val jsonData = if (keyName.isEmpty()) data else dataObject[keyName].toString()
						if (justGetData) {
							hold(listOf(jsonData as T), RequestError.None)
						} else {
							val gson = Gson()
							val collectionType = object : TypeToken<Collection<T>>() {}.type
							hold(gson.fromJson(jsonData, collectionType), RequestError.None)
						}
					} catch (error: Exception) {
						hold(null, RequestError.ResolveDataError(error))
						GoldStoneCode.showErrorCodeReason(data)
					}
				}
			})
		}
	}

	/** 请求 ehterScan, blockchain.info 的数据是明文请求不需要加密 */
	@JvmStatic
	inline fun <reified T> requestUnCryptoData(
		api: String,
		keyName: List<String>,
		justGetData: Boolean = false,
		crossinline hold: (data: List<T>?, error: RequestError) -> Unit
	) {
		val request = Request.Builder().url(api).build()
		client.newCall(request).enqueue(object : Callback {
			override fun onFailure(call: Call, error: IOException) {
				hold(null, RequestError.PostFailed("[API: ${api.getKeyName()}]\n[ERROR: $error]\n[API: $api]"))
			}

			override fun onResponse(call: Call, response: Response) {
				val data = response.body()?.string()
				try {
					val dataObject = data?.toJsonObject() ?: JSONObject("")
					val jsonData =
						if (keyName.isEmpty()) data
						else dataObject.getTargetChild(keyName)
					if (justGetData) {
						hold(listOf(jsonData as T), RequestError.None)
					} else {
						val gson = Gson()
						val collectionType = object : TypeToken<Collection<T>>() {}.type
						hold(gson.fromJson(jsonData, collectionType), RequestError.None)
					}
				} catch (error: Exception) {
					hold(null, RequestError.ResolveDataError(error))
					GoldStoneCode.showErrorCodeReason(data)
				}
			}
		})
	}

	fun String.getKeyName(): String {
		return if (contains("/")) substringAfterLast("/")
		else this
	}

	// `GoldStone` 加密规则的 `Header Request`
	private val generateRequest: (
		path: String,
		goldStoneID: String,
		bold: RequestBody?
	) -> Request = { path, goldStoneID, body ->
		val timeStamp = System.currentTimeMillis().toString()
		val version = SystemUtils.getVersionCode(GoldStoneApp.appContext).toString()
		val sign = getSignHeader(goldStoneID, timeStamp, version)
		Request.Builder()
			.url(path)
			.apply {
				if (body.isNotNull()) method("POST", body)
			}
			.header("Content-type", "application/json")
			.addHeader("device", goldStoneID)
			.addHeader("timestamp", timeStamp)
			.addHeader("os", "0")
			.addHeader("language", HoneyLanguage.getLanguageSymbol(currentLanguage))
			.addHeader("version", version)
			.addHeader("sign", sign)
			.addHeader("channel", currentChannel.value)
			.build()
	}

	fun getSignHeader(goldStoneID: String, timeStamp: String, version: String): String {
		return (goldStoneID + "0" + GoldStoneCryptoKey.apiKey + timeStamp + version).getObjectMD5HexString()
	}

	fun generateCryptoPostRequest(
		body: RequestBody,
		path: String,
		isEncrypt: Boolean,
		targetGoldStoneID: String = "",
		hold: (Request) -> Unit
	) = when {
		isEncrypt && targetGoldStoneID.isEmpty() ->
			hold(generateRequest(path, SharedWallet.getGoldStoneID(), body))
		targetGoldStoneID.isNotEmpty() ->
			hold(generateRequest(path, SharedWallet.getGoldStoneID(), body))
		else -> hold(
			Request.Builder()
				.url(path)
				.method("POST", body)
				.header("Content-type", "application/json")
				.build()
		)
	}

	fun generateCryptoGetRequest(
		api: String,
		isEncrypt: Boolean,
		targetGoldStoneID: String? = null,
		hold: (Request) -> Unit
	) = when {
		isEncrypt && targetGoldStoneID.isNullOrBlank() ->
			hold(generateRequest(api, SharedWallet.getGoldStoneID(), null))
		targetGoldStoneID?.count().orZero() > 0 ->
			hold(generateRequest(api, SharedWallet.getGoldStoneID(), null))
		else -> hold(
			Request.Builder()
				.url(api)
				.header("Content-type", "application/json")
				.build()
		)
	}

	fun callChainBy(
		body: RequestBody,
		chainURL: ChainURL,
		hold: (result: String?, error: RequestError) -> Unit
	) {
		generateCryptoPostRequest(body, chainURL.getURL(), chainURL.isEncrypt) { it ->
			client.newCall(it).enqueue(object : Callback {
				override fun onFailure(call: Call, error: IOException) {
					hold(null, RequestError.PostFailed("[CHAIN NAME: ${chainURL.chainType.id}]\n[ERROR: $error]\n[CHAIN: ${chainURL.getURL()}]"))
				}

				override fun onResponse(call: Call, response: Response) {
					val data =
						if (chainURL.isEncrypt) AesCrypto.decrypt(response.body()?.string().orEmpty())
						else response.body()?.string().orEmpty()
					checkChainErrorCode(data).let {
						if (it.isNotEmpty()) {
							hold(null, RequestError.RPCResult(it))
							return
						}
					}
					try {
						val dataObject = data?.toJsonObject() ?: JSONObject("")
						hold(dataObject.safeGet("result"), RequestError.None)
					} catch (error: Exception) {
						hold(
							null,
							RequestError.ResolveDataError(Throwable("$error onResponse Error in ${chainURL.chainType.id}\n[URL: ${chainURL.getURL()}]\n[Detail:\n ${AesCrypto.decrypt(data.orEmpty())}]"))
						)
					}
				}
			})
		}
	}

	private fun checkChainErrorCode(data: String?): String {
		val hasError = data?.contains("error")
		val errorData = if (hasError == true) try {
			JSONObject(data).safeGet("error")
		} catch (error: Exception) {
			Log.e("checkChainErrorCode", error.message)
			""
		} else {
			val code =
				if (data?.contains("code") == true)
					JSONObject(data).get("code")?.toString()?.toIntOrNull()
				else null
			return if (code == -10) ErrorTag.chain else ""
		}
		return when {
			data.isNullOrBlank() -> return ""
			errorData.isNotEmpty() -> try {
				if (errorData.equals("null", true)) ""
				else JSONObject(errorData).safeGet("message")
			} catch (error: Exception) {
				"$error"
			}
			else -> ""
		}
	}
}

object GoldStoneCode {
	fun isSuccess(code: Any, callback: (isSuccessful: Boolean) -> Unit) {
		if (code == 0) callback(true)
		else {
			callback(false)
			Log.e("Request Error: ", "function: GoldStoneCode, wrongCode: $code")
		}
	}

	fun showErrorCodeReason(data: String?, errorCallback: () -> Unit = {}) {
		val logTag = "Request Error: "
		data?.apply {
			val code = try {
				JSONObject(this).safeGet("code")
			} catch (error: Exception) {
				"100"
			}
			if (code.isNotEmpty()) {
				when (code.toInt()) {
					-1 -> {
						errorCallback()
						Log.e(logTag, "Server Error GoldStone")
					}

					-6 -> {
						errorCallback()
						Log.e(logTag, "参数错误 \n [DATA: $data]")
					}

					-4 -> {
						/**
						 *  `Device` 错误, `APi URL` 是否正确, `API` 文档是否有错误
						 */
						errorCallback()
						Log.e(logTag, "Url Error")
					}
				}
			}
		}

	}
}