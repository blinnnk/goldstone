package io.goldstone.blockchain.common.error

import io.goldstone.blockchain.common.language.EosResourceErrorText
import io.goldstone.blockchain.common.language.ErrorText
import io.goldstone.blockchain.common.language.TransactionErrorText
import io.goldstone.blockchain.common.language.WalletErrorText


/**
 * @author KaySaith
 * @date  2018/09/14
 */

class TransferError(val content: String) : GoldStoneError(content) {
	companion object {
		@JvmStatic
		val BalanceIsNotEnough = TransferError(ErrorText.balanceIsNotEnough)
		@JvmStatic
		val SellRAMTooLess = TransferError(ErrorText.sellRAMTooLess)
		@JvmStatic
		val IncorrectDecimal = TransferError(ErrorText.incorrectDecimal)
		@JvmStatic
		val InvalidBigNumber = TransferError(ErrorText.inputTooBig)
		@JvmStatic
		val InvalidRAMNumber = TransferError(EosResourceErrorText.ramNoDecimals)
		@JvmStatic
		val GetWrongFeeFromChain = TransferError(ErrorText.getWrongFeeFromChain)
		@JvmStatic
		val TradingInputIsEmpty = TransferError(ErrorText.tradingInputIsEmpty)
		@JvmStatic
		val WrongRAMInputValue = TransferError(ErrorText.sellRAMTooLess)
		@JvmStatic
		val LessRAMForRegister = TransferError(EosResourceErrorText.ramNotEnoughForNewAccount)
	}
}

open class AccountError(val content: String) : GoldStoneError(content) {
	companion object {
		@JvmStatic
		val DecryptKeyStoreError = AccountError(ErrorText.decryptKeyStoreError)
		@JvmStatic
		val BackUpMnemonic = AccountError(WalletErrorText.mnemonicsBackUpReminder)
		@JvmStatic
		val InvalidAccountName = AccountError(ErrorText.invalidAccountName)
		@JvmStatic
		val UnavailableAccountName = AccountError(ErrorText.eosNameResultUnavailable)
		@JvmStatic
		val InactivatedAccountName = AccountError(ErrorText.inactivatedAccountName)
		@JvmStatic
		val EmptyName = AccountError(ErrorText.emptyName)
		@JvmStatic
		val EmptyRepeatPassword = AccountError(ErrorText.emptyRepeatPassword)
		@JvmStatic
		val DifferentRepeatPassword = AccountError(ErrorText.differentRepeatPassword)
		@JvmStatic
		val AgreeTerms = AccountError(ErrorText.agreeTerms)
		@JvmStatic
		val InvalidMnemonic = AccountError(ErrorText.invalidMnemonic)
		@JvmStatic
		val InvalidBip44Path = AccountError(ErrorText.invalidBip44Path)
		@JvmStatic
		val EmptyPublicKey = AccountError(ErrorText.emptyPublicKey)
		@JvmStatic
		val WrongPassword = AccountError(ErrorText.wrongPassword)
		@JvmStatic
		val InvalidAddress = AccountError(ErrorText.invalidAddress)
		@JvmStatic
		val ExistAddress = AccountError(ErrorText.existAddress)
		@JvmStatic
		val InvalidPrivateKey = AccountError(ErrorText.invalidPrivateKey)
		@JvmStatic
		val PasswordFormatted: (reason: String) -> AccountError = {
			AccountError("${ErrorText.passwordFormatted} $it")
		}
		@JvmStatic
		val None = AccountError(GoldStoneError.None.message)
	}
}

class PasswordError(val content: String) : GoldStoneError(content) {
	companion object {
		@JvmStatic
		val InputIsEmpty = AccountError(TransactionErrorText.emptyConfirmPassword)
		@JvmStatic
		val None = AccountError(GoldStoneError.None.message)
	}
}

open class RequestError(override val message: String) : GoldStoneError(message) {
	companion object {
		@JvmStatic
		val PostFailed: (errorDetail: String) -> RequestError = { error ->
			RequestError("${ErrorText.postFailed} \n[${ErrorText.error}: $error]")
		}
		@JvmStatic
		val ResolveDataError: (errorDetail: Throwable) -> RequestError = { error ->
			RequestError("${ErrorText.resolveDataError} \n[${ErrorText.error}: ${error.message}]")
		}
		@JvmStatic
		val NullResponse: (description: String) -> RequestError = { description ->
			RequestError("${ErrorText.nullResponse} \n[${ErrorText.error}: $description]")
		}
		@JvmStatic
		val None = RequestError(GoldStoneError.None.message)

		@JvmStatic
		val RPCResult: (description: String) -> RequestError = { description ->
			RequestError("${ErrorText.error}\n\n[${ErrorText.error}: $description]")
		}

		@JvmStatic
		val EmptyResut = RequestError("Empty Result")
	}
}