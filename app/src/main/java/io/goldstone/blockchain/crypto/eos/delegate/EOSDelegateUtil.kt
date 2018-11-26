package io.goldstone.blockchain.crypto.eos.delegate

import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.base.EOSModel
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.utils.toEOSCount
import io.goldstone.blockchain.crypto.utils.toNoPrefixHexString
import java.io.Serializable
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/11/22
 * @description
 * {"receiver": "kingofdragon", "unstake_cpu_quantity": "0.0010 EOS", "unstake_net_quantity": "0.0010 EOS", "from": "kingofdragon"}
 */

class EOSBandwidthInfo(
	private val from: EOSAccount,
	private val receiver: EOSAccount,
	private val cpuAmount: BigInteger,
	private val netAmount: BigInteger
) : Serializable, EOSModel {

	override fun createObject(): String {
		return "{\"receiver\": \"${receiver.name}\", \"unstake_cpu_quantity\": \"${cpuAmount.toEOSCount()} EOS\", \"unstake_net_quantity\": \"${netAmount.toEOSCount()} EOS\", \"from\": \"${from.name}\"}"
	}

	override fun serialize(): String {
		val decimalCode = EOSUtils.getEvenHexOfDecimal(CryptoValue.eosDecimal)
		val symbolCode = CoinSymbol.EOS.symbol.toByteArray().toNoPrefixHexString()
		val decimalAndSymbol = decimalCode + symbolCode
		val completeZero = "00000000"
		val encryptFromAccount = EOSUtils.getLittleEndianCode(from.name)
		val encryptToAccount = EOSUtils.getLittleEndianCode(receiver.name)
		val cpuAmountCode = EOSUtils.convertAmountToCode(cpuAmount)
		val netAmountCode = EOSUtils.convertAmountToCode(netAmount)
		return encryptFromAccount + encryptToAccount + netAmountCode + decimalAndSymbol + completeZero + cpuAmountCode + decimalAndSymbol + completeZero
	}
}

