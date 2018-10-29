package io.goldstone.blockchain.crypto.ethereum.walletfile

/**
 * @date 2018/6/17 9:13 PM
 * @author KaySaith
 */
import kotlinx.serialization.KInput
import kotlinx.serialization.KOutput
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.internal.SerialClassDescImpl

const val AES_128_CTR = "pbkdf2"
const val SCRYPT = "scrypt"

@Serializable
data class CipherParams(var iv: String)

@Serializable
data class WalletCrypto(
	var cipher: String,
	var ciphertext: String,
	var cipherparams: CipherParams,
	var kdf: String,
	@Serializable(with = KdfSerializer::class)
	var kdfparams: KdfParams,
	var mac: String
) {
	
	object KdfSerializer : KSerializer<KdfParams> {
		override val serialClassDesc = SerialClassDescImpl("KDFSerializer")
		
		override fun load(input: KInput) =
			throw NotImplementedError("loading WalletCrypto is not implemented - use WalletCryptoForImport instead")
		
		override fun save(output: KOutput, obj: KdfParams) = when (obj) {
			is ScryptKdfParams -> output.write(obj)
			is Aes128CtrKdfParams -> output.write(obj)
		}
	}
}

@Serializable
data class WalletCryptoForImport(
	var cipher: String,
	var ciphertext: String,
	var cipherparams: CipherParams,
	var kdf: String,
	var kdfparams: Map<String, String>,
	var mac: String
)

@Serializable
data class Wallet(
	val address: String?,
	val crypto: WalletCrypto,
	val id: String,
	val version: Int
)