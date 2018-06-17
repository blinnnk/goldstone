package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model

import java.io.Serializable
import java.math.BigInteger

/**
 * @date 2018/5/16 3:32 PM
 * @author KaySaith
 */
data class PaymentPrepareModel(
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