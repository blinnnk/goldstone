package io.goldstone.blinnnk.common.error


/**
 * @author KaySaith
 * @date  2018/09/22
 */
class TypeConvertError(override val message: String) : GoldStoneError(message) {
	companion object {
		val AmountToCount = TypeConvertError("wrong amount input value, Amount Input Only Support String or Long as Input Value")
	}
}