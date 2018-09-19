package io.goldstone.blockchain.crypto.error


/**
 * @author KaySaith
 * @date  2018/09/14
 */

abstract class GoldStoneError(override val message: String) : Throwable(message)

class TransferError(val content: String) : GoldStoneError(content) {
	companion object {
		val balanceIsNotEnough = TransferError("this account doesn't have enough balance")
		val incorrectDecimal = TransferError("this input count's decimal is wrong with its own decimal value")
		val getWrongFeeFromChain = TransferError("there is error when get fee from chain")
		val getChainInfoError = TransferError("get chain info error, please check your net environment")
		val none = TransferError("No errors")
	}
}

class StakeBandWidthError(override val message: String) : GoldStoneError(message) {
	companion object {
		/** EOS Delegate/Refund CPU Errors */
		val transferToSelf = StakeBandWidthError("you can't transfer to you self when stake resource and from account name is same as to account name")
	}
}

class AccountError(val content: String) : GoldStoneError(content) {
	companion object {
		val decryptKeyStoreError = AccountError("decrypt your keystore by password found error")
	}
}

class RequestError(override val message: String) : GoldStoneError(message) {
	companion object {
		val postFailed: (errorDetail: Throwable) -> GoldStoneError = { error ->
			AccountError("post request failed || ${error.message}")
		}
		val resolveDataError: (errorDetail: Throwable) -> GoldStoneError = { error ->
			AccountError("resolve request result data failed || ${error.message}")
		}
	}
}