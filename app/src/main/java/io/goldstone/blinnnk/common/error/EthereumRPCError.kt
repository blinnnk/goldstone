package io.goldstone.blinnnk.common.error


/**
 * @author KaySaith
 * @date  2018/09/21
 */
open class EthereumRPCError(override val message: String) : RequestError(message) {
	companion object {
		/** EOS Delegate/Refund CPU Errors */
		@JvmStatic
		val GetSymbol: (RequestError) -> EthereumRPCError = {
			EthereumRPCError("Ethereum RPC Error Of Getting Symbol ${it.message}")
		}

		@JvmStatic
		val GetInputCode: (RequestError) -> EthereumRPCError = {
			EthereumRPCError("Ethereum RPC Error Of Getting Input Code ${it.message}")
		}

		@JvmStatic
		val GetUsableNonce: (RequestError) -> EthereumRPCError = {
			EthereumRPCError("Ethereum RPC Error Of Getting Usable Nonce  ${it.message}")
		}

		@JvmStatic
		val GetBlockNumber: (RequestError) -> EthereumRPCError = {
			EthereumRPCError("Ethereum RPC Error Of Getting Block Number  ${it.message}")
		}

		@JvmStatic
		val GetBlockTimeByBlockHash: (RequestError) -> EthereumRPCError = {
			EthereumRPCError("Ethereum RPC Error Of Getting Block Time By Block Hash  ${it.message}")
		}

		@JvmStatic
		val GetTransactionByHash: (RequestError) -> EthereumRPCError = {
			EthereumRPCError("Ethereum RPC Error Of Getting Transaction By Block Hash  ${it.message}")
		}

		@JvmStatic
		val GetReceiptByHash: (RequestError) -> EthereumRPCError = {
			EthereumRPCError("Ethereum RPC Error Of Getting Receipt By Hash  ${it.message}")
		}

		@JvmStatic
		val GetTransactionExecutedValue: (RequestError) -> EthereumRPCError = {
			EthereumRPCError("Ethereum RPC Error Of Getting Transaction Executed Value  ${it.message}")
		}

		@JvmStatic
		val GetRAWTransaction: (RequestError) -> EthereumRPCError = {
			EthereumRPCError("Ethereum RPC Error Of Getting RAW Transaction  ${it.message}")
		}

		@JvmStatic
		val GetTokenBalance: (RequestError) -> EthereumRPCError = {
			EthereumRPCError("Ethereum RPC Error Of Getting Token Balance  ${it.message}")
		}

		@JvmStatic
		val GetTokenName: (RequestError) -> EthereumRPCError = {
			EthereumRPCError("Ethereum RPC Error Of Getting Token Name ${it.message}")
		}

		@JvmStatic
		val GetTokenDecimal: (RequestError) -> EthereumRPCError = {
			EthereumRPCError("Ethereum RPC Error Of Getting Token Decimal ${it.message}")
		}

		@JvmStatic
		val GetTokenTotalSupply: (RequestError) -> EthereumRPCError = {
			EthereumRPCError("Ethereum RPC Error Of Getting Token Total Supply ${it.message}")
		}

		@JvmStatic
		val GetETHBalance: (RequestError) -> EthereumRPCError = {
			EthereumRPCError("Ethereum RPC Error Of Getting ETH Balance ${it.message}")
		}

		val None = EthereumRPCError(GoldStoneError.None.message)
	}
}