package io.goldstone.blockchain.module.common.tokenpayment.paymentdetail.model

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
) : Serializable

data class PaymentBTCSeriesModel(
	val toAddress: String,
	val fromAddress: String,
	val changeAddress: String,
	val value: Long,
	val estimateFeePerByte: Long,
	val signedMessageSize: Long
) : Serializable