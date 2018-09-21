package io.goldstone.blockchain.common.error


/**
 * @author KaySaith
 * @date  2018/09/21
 */
class EOSRPCError(override val message: String) : GoldStoneError(message) {
	companion object {
		val RegisteredName = EOSRPCError("this account name has been registered")
	}
}