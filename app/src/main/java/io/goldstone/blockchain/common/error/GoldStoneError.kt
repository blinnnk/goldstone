package io.goldstone.blockchain.common.error


/**
 * @author KaySaith
 * @date  2018/09/21
 */
open class GoldStoneError(override val message: String, val tag: String = "GoldStoneError") : Throwable(message) {
	fun isNone(): Boolean = message.equals(GoldStoneError.None.message, true)

	companion object {
		@JvmStatic
		val None = GoldStoneError("No errors")
	}
}