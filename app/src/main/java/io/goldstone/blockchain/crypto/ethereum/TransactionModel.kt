package io.goldstone.blockchain.crypto.ethereum

/**
 * @date 2018/6/17 6:57 PM
 * @author KaySaith
 */
import io.goldstone.blockchain.crypto.utils.hexToByteArray
import java.math.BigInteger

private var DEFAULT_GAS_PRICE = BigInteger("20000000000")
private var DEFAULT_GAS_LIMIT = BigInteger("21000")

data class ChainDefinition(
	val id: Long,
	private val prefix: String = "ETH"
) {
	
	override fun toString() = "$prefix:$id"
}

data class Transaction(
	var chain: ChainDefinition?,
	var creationEpochSecond: Long?,
	var from: Address?,
	var gasLimit: BigInteger,
	var gasPrice: BigInteger,
	var input: List<Byte>,
	var nonce: BigInteger?,
	var to: Address?,
	var txHash: String?,
	var value: BigInteger
) {
	
	constructor() : this(
		chain = null,
		creationEpochSecond = null,
		from = null,
		gasLimit = DEFAULT_GAS_LIMIT,
		gasPrice = DEFAULT_GAS_PRICE,
		input = emptyList<Byte>(),
		nonce = null,
		to = null,
		txHash = null,
		value = BigInteger.ZERO
	)
}

fun Transaction.signViaEIP155(key: ECKeyPair, chainDefinition: ChainDefinition): SignatureData {
	val signatureData =
		key.signMessage(encodeRLP(SignatureData().apply { v = chainDefinition.id.toByte() }))
	return signatureData.copy(v = (signatureData.v + chainDefinition.id * 2 + 8).toByte())
}

fun Transaction.toRLPList(signature: SignatureData?) = RLPList(listOf(
	nonce!!.toRLP(),
	gasPrice.toRLP(),
	gasLimit.toRLP(),
	(to?.hex?.let { it } ?: "0x").hexToByteArray().toRLP(),
	value.toRLP(),
	input.toByteArray().toRLP()
).let {
	if (signature == null) {
		it
	} else {
		it.plus(
			listOf(
				signature.v.toRLP(),
				signature.r.toRLP(),
				signature.s.toRLP()
			)
		)
	}
})

fun Transaction.encodeRLP(signature: SignatureData? = null) = toRLPList(signature).encode()

fun RLPType.encode(): ByteArray = when (this) {
	is RLPElement -> bytes.encodeRLP(ELEMENT_OFFSET)
	is RLPList -> element.map { it.encode() }
		.fold(ByteArray(0)) { acc, bytes -> acc + bytes } // this can be speed optimized when needed
		.encodeRLP(LIST_OFFSET)
}

internal fun ByteArray.encodeRLP(offset: Int) = when {
	size == 1 && ((first().toInt() and 0xff) < ELEMENT_OFFSET) && offset == ELEMENT_OFFSET -> this
	size <= 55 -> ByteArray(1) { (size + offset).toByte() }.plus(this)
	else -> size.toMinimalByteArray().let { arr ->
		ByteArray(1) { (offset + 55 + arr.size).toByte() } + arr + this
	}
}