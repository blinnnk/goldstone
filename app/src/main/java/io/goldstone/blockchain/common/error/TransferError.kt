package io.goldstone.blockchain.common.error


/**
 * @author KaySaith
 * @date  2018/09/14
 */

class TransferError(val content: String) : GoldStoneError(content) {
	companion object {
		@JvmStatic
		val BalanceIsNotEnough = TransferError("this account doesn't have enough balance")
		@JvmStatic
		val SellRAMTooLess = TransferError("the value you decide sell about RAM much greater than 1 byte")
		@JvmStatic
		val IncorrectDecimal = TransferError("this input count's decimal is wrong with its own decimal value")
		@JvmStatic
		val GetWrongFeeFromChain = TransferError("there is error when get fee from chain")
		@JvmStatic
		val TradingInputIsEmpty = TransferError("please enter the count that you will trading")
		@JvmStatic
		val wrongRAMInputValue = TransferError("you must enter only Integer because of the unit of selling ram is byte")
	}
}

class StakeBandWidthError(override val message: String) : GoldStoneError(message) {
	companion object {
		/** EOS Delegate/Refund CPU Errors */
		@JvmStatic
		val TransferToSelf = StakeBandWidthError("you can't transfer to you self when stake resource and from account name is same as to account name")
	}
}

open class AccountError(val content: String) : GoldStoneError(content) {
	companion object {
		@JvmStatic
		val DecryptKeyStoreError = AccountError("decrypt your keystore by password found error")
		@JvmStatic
		val InvalidAccountName = AccountError("invalid eos account name")
		@JvmStatic
		val UnavailableAccountName = AccountError("Unavailable Account Name")
		@JvmStatic
		val EmptyName = AccountError("please enter the account name which you decide to register")
		@JvmStatic
		val EmptyRepeatPassword = AccountError("Repeat Password Is Empty Now")
		@JvmStatic
		val DifferentRepeatPassword = AccountError("The password entered twice is inconsistent")
		@JvmStatic
		val AgreeTerms = AccountError("Please read and agree to the terms")
		@JvmStatic
		val InvalidMnemonic = AccountError("Incorrect mnemonic format")
		@JvmStatic
		val InvalidBip44Path = AccountError("Incorrect Bip44 Path")
		@JvmStatic
		val EmptyPublicKey = AccountError("please enter the public key which you decide to bind the account name")
		@JvmStatic
		val WrongPassword = AccountError("Wrong Password")
		@JvmStatic
		val InvalidAddress = AccountError("Address Formatted is Invalid")
		@JvmStatic
		val ExistAddress = AccountError("This Address Has Existed In Your Wallet")
		@JvmStatic
		val InvalidPrivateKey = AccountError("Invalid Private Key")
		@JvmStatic
		val PasswordFormatted: (reason: String) -> AccountError = {
			AccountError("Password Formatted is Wrong $it")
		}
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

open class RequestError(override val message: String) : GoldStoneError(message) {
	companion object {
		@JvmStatic
		val PostFailed: (errorDetail: String) -> RequestError = { error ->
			RequestError("post request failed \n[ERROR: $error]")
		}
		@JvmStatic
		val ResolveDataError: (errorDetail: Throwable) -> RequestError = { error ->
			RequestError("resolve request result data failed \n[ERROR: ${error.message}]")
		}
		@JvmStatic
		val NullResponse: (description: String) -> RequestError = { description ->
			RequestError("null response from server or chain \n[ERROR: $description]")
		}
		@JvmStatic
		val None = RequestError(GoldStoneError.None.message)

		@JvmStatic
		val RPCResult: (description: String) -> RequestError = { description ->
			RequestError("Error Description\n\n[ERROR: $description]")
		}
	}
}