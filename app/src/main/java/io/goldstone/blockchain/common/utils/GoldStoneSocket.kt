package io.goldstone.blockchain.common.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.blinnnk.extension.orFalse
import io.goldstone.blockchain.common.value.WebUrl
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil.generateCryptoGetRequest
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * @date 2018/4/28 3:33 PM
 * @author KaySaith
 */
abstract class GoldStoneWebSocket : WebSocketListener() {

	/**
	 * 添加日志，需要观察长链接断的情况
	 */
	private val tag: String = "GoldStoneWebSocket"
	private val timeout = 30000L
	private val normalCloseCode = 1000
	private var serverURL = socketUrl
	private var isConnected = false
	private val handler = Handler(Looper.getMainLooper())
	private val handlerPing = Handler(Looper.getMainLooper())
	private var reconnectCount = 0 // 重连次数
	private var reconnectMaxCount = 10
	private val minInterval: Long = 6000 // 重连最小时间间隔
	private var webSocket: WebSocket? = null

	abstract fun onOpened()

	open fun getServerBack(content: JSONObject, isDisconnected: Boolean) {
		// Do Something
	}

	// 定时向服务器汇报状态的 `runnable`
	private val pingRunnable = Runnable {
		webSocket
			?.send(AesCrypto.encrypt("{\"t\": \"ping\", \"time\": ${System.currentTimeMillis()}}").orEmpty())
	}

	private fun reportStatus() {
		// 每 `5s` 像服务器汇报一下链接状态
		handlerPing.removeCallbacks(pingRunnable)
		handlerPing.postDelayed(pingRunnable, 5000L)
	}

	override fun onOpen(webSocket: WebSocket, response: Response) {
		super.onOpen(webSocket, response)
		this.webSocket = webSocket
		isConnected = true
		reconnectCount = 0
		onOpened()
		reportStatus() // 第一次汇报状态
		Log.v(tag, "onOpen")
	}

	override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
		super.onFailure(webSocket, t, response)
		webSocket.close(normalCloseCode, null)
		isConnected = false
		reconnectWebSocket()
		Log.v(tag, "onFailure")
	}

	override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
		super.onClosing(webSocket!!, code, reason!!)
		isConnected = false
		Log.v(tag, "onClosing")
	}

	override fun onMessage(webSocket: WebSocket?, text: String?) {
		super.onMessage(webSocket!!, text!!)
		val jsonObject = JSONObject(AesCrypto.decrypt(text))
		if (jsonObject["t"] != "pong") {
			// 如果返回的字符串中包含 `offline` 那么以为着长链接是断开的
			getServerBack(
				jsonObject,
				AesCrypto.decrypt(text)?.contains("offline").orFalse()
			)
		} else {
			reportStatus()
		}
	}

	override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
		super.onClosed(webSocket, code, reason)
		closeSocket()
		isConnected = false
		Log.v(tag, "onClosed")
	}

	fun isSocketConnected(): Boolean {
		return isConnected
	}

	fun runSocket() {
		closeSocket()
		val client =
			OkHttpClient.Builder()
				.readTimeout(timeout, TimeUnit.MILLISECONDS)
				.writeTimeout(timeout, TimeUnit.MILLISECONDS)
				.connectTimeout(timeout, TimeUnit.MILLISECONDS)
				.retryOnConnectionFailure(true)
				.build()
		generateCryptoGetRequest(serverURL, true) {
			client.newWebSocket(it, this)
			client.dispatcher().executorService().shutdown()
		}
	}

	private fun reconnectWebSocket() {
		if (webSocket == null || !isConnected) {
			closeSocket()
			reconnectCount += 1
			Log.v("tag", "reconnectCount $reconnectCount")
			if (reconnectCount <= reconnectMaxCount) {
				handler.removeCallbacks(runnable)
				handler.postDelayed(runnable, minInterval * reconnectCount)
			} else {
				reconnectCount = 0
			}
		}
	}

	private val runnable = Runnable {
		runSocket()
	}

	fun closeSocket() {
		webSocket?.let {
			// 取消订阅
			webSocket?.send(AesCrypto.encrypt("{\"t\": \"unsub_tick\"}").orEmpty())
			it.close(normalCloseCode, null)
			webSocket = null
		}
	}

	fun sendMessage(message: String) {
		webSocket?.send(AesCrypto.encrypt(message).orEmpty())
	}

	companion object {
		private var socketUrl = WebUrl.normalSocket
		fun updateSocketUrl(newUrl: String) {
			socketUrl = newUrl
		}
	}
}

