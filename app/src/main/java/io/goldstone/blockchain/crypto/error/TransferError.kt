package io.goldstone.blockchain.crypto.error


/**
 * @author KaySaith
 * @date  2018/09/14
 */

open class GoldStoneError(override val message: String, val tag: String = "GoldStoneError") : Throwable(message) {
	fun isNone(): Boolean = message.equals(GoldStoneError.None.message, true)

	companion object {
		@JvmStatic
		val None = GoldStoneError("No errors")
	}
}

class TransferError(val content: String) : GoldStoneError(content) {
	companion object {
		@JvmStatic
		val BalanceIsNotEnough = TransferError("this account doesn't have enough balance")
		@JvmStatic
		val IncorrectDecimal = TransferError("this input count's decimal is wrong with its own decimal value")
		@JvmStatic
		val GetWrongFeeFromChain = TransferError("there is error when get fee from chain")
		@JvmStatic
		val GetChainInfoError = TransferError("get chain info error, please check your net environment")
		@JvmStatic
		val TradingInputIsEmpty = TransferError("please enter the count which you will trading")
	}
}

class StakeBandWidthError(override val message: String) : GoldStoneError(message) {
	companion object {
		/** EOS Delegate/Refund CPU Errors */
		@JvmStatic
		val TransferToSelf = StakeBandWidthError("you can't transfer to you self when stake resource and from account name is same as to account name")
	}
}

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

class AccountError(val content: String) : GoldStoneError(content) {
	companion object {
		@JvmStatic
		val DecryptKeyStoreError = AccountError("decrypt your keystore by password found error")
		@JvmStatic
		val InvalidAccountName = AccountError("invalid eos account name")
		@JvmStatic
		val None = AccountError(GoldStoneError.None.message)
	}
}

class PasswordError(val content: String) : GoldStoneError(content) {
	companion object {
		@JvmStatic
		val InputIsEmpty = AccountError("please enter your password to unlock your wallet")
		@JvmStatic
		val None = AccountError(GoldStoneError.None.message)
	}
}

class RequestError(override val message: String) : GoldStoneError(message) {
	companion object {
		@JvmStatic
		val PostFailed: (errorDetail: Throwable) -> RequestError = { error ->
			RequestError("post request failed || ${error.message}")
		}
		@JvmStatic
		val ResolveDataError: (errorDetail: Throwable) -> RequestError = { error ->
			RequestError("resolve request result data failed || ${error.message}")
		}
	}
}