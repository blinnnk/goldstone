package io.goldstone.blockchain.common.error


/**
 * @author KaySaith
 * @date  2018/09/21
 */
class EthereumRPCError(override val message: String) : GoldStoneError(message) {
	companion object {
		/** EOS Delegate/Refund CPU Errors */
		@JvmStatic
		val GetSymbol: (Throwable) -> EthereumRPCError = {
			EthereumRPCError("Ethereum RPC Error Of Getting Symbol ${it.message}")
		}
		@JvmStatic
		val GetTokenName: (Throwable) -> EthereumRPCError = {
			EthereumRPCError("Ethereum RPC Error Of Getting Token Name ${it.message}")
		}

		@JvmStatic
		val GetTokenDecimal: (Throwable) -> EthereumRPCError = {
			EthereumRPCError("Ethereum RPC Error Of Getting Token Decimal ${it.message}")
		}
	}
}