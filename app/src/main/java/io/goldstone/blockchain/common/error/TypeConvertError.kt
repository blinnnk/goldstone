package io.goldstone.blockchain.common.error


/**
 * @author KaySaith
 * @date  2018/09/22
 */
class TypeConvertError(override val message: String) : GoldStoneError(message) {
	companion object {
		val StringToInt = TypeConvertError("string content contains illegal symbol except number")
	}
}