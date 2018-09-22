package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model

import io.goldstone.blockchain.crypto.ethereum.Address
import io.goldstone.blockchain.crypto.ethereum.ChainDefinition
import io.goldstone.blockchain.crypto.ethereum.Transaction
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.hexToByteArray
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.getSelectedGasPrice
import java.io.Serializable
import java.math.BigInteger

/**
 * @date 2018/5/16 3:32 PM
 * @author KaySaith
 */
data class PaymentPrepareModel(
	val fromAddress: String,
	val nonce: BigInteger,
	val gasLimit: BigInteger,
	val toAddress: String,
	val countWithDecimal: BigInteger,
	val count: Double,
	val inputData: String,
	val toWalletAddress: String,
	val memo: String,
	var gasPrice: BigInteger = BigInteger.ZERO
) : Serializable {
	fun generateRawTransaction(
		chainID: Long,
		minerFeeType: MinerFeeType,
		gasLimit: BigInteger
	): Transaction {
		return Transaction().apply transaction@{
			this@transaction.chain = ChainDefinition(chainID)
			this@transaction.nonce = this@PaymentPrepareModel.nonce
			this@transaction.gasPrice = minerFeeType.getSelectedGasPrice()
			this@transaction.gasLimit = gasLimit
			this@transaction.to = Address(toAddress)
			this@transaction.value =
				if (CryptoUtils.isERC20TransferByInputCode(inputData)) BigInteger.valueOf(0)
				else countWithDecimal
			this@transaction.input = inputData.hexToByteArray().toList()
		}
	}
}

data class PaymentBTCSeriesModel(
	val toAddress: String,
	val fromAddress: String,
	val changeAddress: String,
	val value: Long,
	val estimateFeePerByte: Long,
	val signedMessageSize: Long
) : Serializable