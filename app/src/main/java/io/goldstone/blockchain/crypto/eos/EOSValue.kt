package io.goldstone.blockchain.crypto.eos

import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/14
 */

enum class EOSWalletType {
	Inactivated, NoDefault, None, Available
}

enum class EOSUnit(val value: String) {
	KB("KB"),
	MB("MB"),
	Byte("Byte")
}

enum class EOSCPUUnit(val value: String) {
	SEC("SEC"),
	MS("MS"),
	MUS("MUS"),
	MIN("MIN")
}

object EOSValue {
	const val maxNameLength = 12
	const val maxSpecialNameLength = 32
	const val memoMaxCharacterSize = 256
	const val defaultRegisterAssignRAM = 4096L
	const val defaultRegisterAssignBandWidth = 0.1
}

class EOSTransactionMethod(val value: String) {
	fun isTransfer(): Boolean = value.equals("transfer", true)
	companion object {
		val Transfer = EOSTransactionMethod("transfer")
		fun transfer() = EOSTransactionMethod("transfer")
		fun undelegatebw() = EOSTransactionMethod("undelegatebw")
	}
}

data class EOSTransactionSerialization(
	val packedTX: String,
	val serialized: String
)

data class EOSCodeName(val value: String) : Serializable {
	companion object {
		val EOSIOToken = EOSCodeName("eosio.token")
		val EOSIO = EOSCodeName("eosio")
	}
}