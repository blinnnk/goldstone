@file:Suppress("IMPLICIT_CAST_TO_ANY")

package io.goldstone.blockchain.kernel.network

import io.goldstone.blockchain.common.utils.AesCrypto

/**
 * @date 2018/6/17 2:16 PM
 * @author KaySaith
 */
object ParameterUtil {
	
	fun <T> prepare(vararg parameters: Pair<String, T>, isEncrypt: Boolean = true): String {
		var content = ""
		parameters.forEach {
			val value = if (it.second is String) {
				"\"${it.second}\""
			} else it.second
			content += "\"${it.first}\":$value,"
		}
		return if (isEncrypt) {
			AesCrypto.encrypt("{${content.substringBeforeLast(",")}}").orEmpty()
		} else {
			"{${content.substringBeforeLast(",")}}"
		}
	}
	
	fun <T> prepareJsonRPC(
		method: String,
		id: Int,
		hasLatest: Boolean,
		vararg parameters: T
	): String {
		var content = ""
		parameters.forEach {
			val value = if (it is String) "\"$it\"" else it
			content += "$value,"
		}
		val latest = if (hasLatest) ",\"latest\"" else ""
		return AesCrypto.encrypt(
			"{\"jsonrpc\":\"2.0\", \"method\":\"$method\", \"params\":[${content.substringBeforeLast(",")}$latest], \"id\":$id}"
		).orEmpty()
	}
	
	fun <T> preparePairJsonRPC(
		method: String,
		hasLatest: Boolean,
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
		return AesCrypto.encrypt(
			"{\"jsonrpc\":\"2.0\", \"method\":\"$method\", \"params\":[{${content.substringBeforeLast(",")}}$latest],\"id\":1}"
		).orEmpty()
	}
}