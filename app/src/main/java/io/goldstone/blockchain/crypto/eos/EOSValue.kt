package io.goldstone.blockchain.crypto.eos


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
	Byte("Byte"),
	SEC("SEC"),
	MS("MS"),
	MUS("MUS"),
	MIN("MIN")
}

object EOSValue {
	const val maxNameLength = 12
	const val maxSpecialNameLength = 32
	const val memoMaxCharacterSize = 256
}

enum class EOSTransactionMethod(val value: String) {
	Transfer("transfer")
}

enum class EOSCodeName(val value: String) {
	EOSIOToken("eosio.token")
}

data class EOSTransactionSerialization(
	val packedTX: String,
	val serialized: String
)