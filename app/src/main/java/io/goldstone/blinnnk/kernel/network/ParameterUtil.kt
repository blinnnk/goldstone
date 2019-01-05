@file:Suppress("IMPLICIT_CAST_TO_ANY")

package io.goldstone.blinnnk.kernel.network

import com.blinnnk.extension.isNull
import io.goldstone.blinnnk.common.utils.AesCrypto

/**
 * @date 2018/6/17 2:16 PM
 * @author KaySaith
 */
object ParameterUtil {

	fun <T> prepare(
		isEncrypt: Boolean = true,
		vararg parameters: Pair<String, T>
	): String {
		var content = ""
		parameters.forEach {
			val value = if (it.second is String) {
				"\"${it.second}\""
			} else it.second
			content += "\"${it.first}\":$value,"
		}
		val finalContent = "{${content.substringBeforeLast(",")}}"
		return if (isEncrypt) AesCrypto.encrypt(finalContent).orEmpty() else finalContent
	}

	fun <T> prepareJsonRPC(
		isEncrypt: Boolean,
		method: String,
		id: Int?,
		hasLatest: Boolean,
		isRPC2: Boolean,
		vararg parameters: T?
	): String {
		var content = ""
		parameters.forEach {
			if (it.isNull()) return@forEach
			val value = if (it is String) "\"$it\"" else it
			content += "$value,"
		}
		val latest = if (hasLatest) ",\"latest\"" else ""
		val finalParameter =
			if (content.isEmpty()) ""
			else content.substringBeforeLast(",") + latest
		val rpcVersion = if (isRPC2) 2.0 else 1.0
		val rpcID = if (id.isNull()) "" else ",\"id\":$id"
		val rpcContent =
			"{\"jsonrpc\":\"$rpcVersion\", \"method\":\"$method\", \"params\":[$finalParameter]$rpcID}"
		return if (isEncrypt) AesCrypto.encrypt(rpcContent).orEmpty() else rpcContent
	}

	fun <T> preparePairJsonRPC(
		isEncrypt: Boolean,
		method: String,
		hasLatest: Boolean,
		isRPC2: Boolean,
		vararg parameters: Pair<String, T>
	): String {
		var content = ""
		parameters.forEach {
			val value = if (it.second is String) {
				"\"${it.second}\""
			} else it.second
			content += "\"${it.first}\":$value,"
		}
		val latest = if (hasLatest) ",\"latest\"" else ""
		val rpcVersion = if (isRPC2) 2.0 else 1.0
		val rpcContent =
			"{\"jsonrpc\":\"$rpcVersion\", \"method\":\"$method\", \"params\":[{${content
				.substringBeforeLast(",")}}$latest],\"id\":1}"
		return if (isEncrypt) AesCrypto.encrypt(rpcContent).orEmpty() else rpcContent
	}

	fun <T> prepareObjectContent(vararg parameters: Pair<String, T>): String {
		var content = ""
		parameters.forEach {
			val value = if (it.second is String) {
				"\"${it.second}\""
			} else it.second
			content += "\"${it.first}\":$value,"
		}
		return "{${content.substringBeforeLast(",")}}"
	}
	fun <T> prepareObjectContent(params: List<Pair<String, T>>): String {
		var content = ""
		params.forEach {
			val value = if (it.second is String) {
				"\"${it.second}\""
			} else it.second
			content += "\"${it.first}\":$value,"
		}
		return "{${content.substringBeforeLast(",")}}"
	}
}