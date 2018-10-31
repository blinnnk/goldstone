package io.goldstone.blockchain.common.error

import io.goldstone.blockchain.common.language.ErrorText


/**
 * @author KaySaith
 * @date  2018/09/21
 */
open class GoldStoneError(override val message: String, val tag: String = "GoldStoneError") : Throwable(message) {
	fun isNone(): Boolean = message.equals(GoldStoneError.None.message, true)
	fun hasError(): Boolean = !message.equals(GoldStoneError.None.message, true)

	companion object {
		@JvmStatic
		val None = GoldStoneError(ErrorText.none)
	}
}