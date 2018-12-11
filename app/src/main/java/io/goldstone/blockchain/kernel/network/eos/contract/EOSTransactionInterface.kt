package io.goldstone.blockchain.kernel.network.eos.contract

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.subgraph.orchid.encoders.Hex
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.crypto.eos.EOSTransactionSerialization
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.eos.ecc.Sha256
import io.goldstone.blockchain.kernel.network.ParameterUtil
import io.goldstone.blockchain.kernel.network.eos.EOSAPI

/**
 * @author KaySaith
 * @date  2018/09/19
 */
abstract class EOSTransactionInterface {

	@Throws
	abstract fun serialized(
		@UiThread hold: (serialization: EOSTransactionSerialization?, error: GoldStoneError) -> Unit
	)

	open fun send(
		privateKey: EOSPrivateKey,
		@WorkerThread hold: (response: EOSResponse?, error: GoldStoneError) -> Unit
	) {
		System.out.println("hello 4")
		serialized { data, error ->
			System.out.println("hello 5")
			if (data.isNotNull() && error.isNone()) {
				val signature = privateKey.sign(Sha256.from(Hex.decode(data.serialized))).toString()
				EOSAPI.pushTransaction(listOf(signature), data.packedTX, hold)
			} else hold(null, error)
		}
	}

	open fun getSignHash(
		privateKey: EOSPrivateKey,
		hold: (signedHash: String?, error: GoldStoneError) -> Unit
	) {
		serialized { data, error ->
			if (data.isNotNull() && error.isNone()) {
				hold(privateKey.sign(Sha256.from(Hex.decode(data.serialized))).toString(), GoldStoneError.None)
			} else hold(null, error)
		}
	}

	open fun getPushTransactionObject(
		privateKey: EOSPrivateKey,
		hold: (json: String?, error: GoldStoneError) -> Unit) {
		serialized { data, error ->
			if (data.isNotNull() && error.isNone()) {
				val signedHash = privateKey.sign(Sha256.from(Hex.decode(data.serialized))).toString()
				hold(
					ParameterUtil.prepareObjectContent(
						Pair("signatures", listOf(signedHash).toJsonArray()),
						Pair("packed_trx", data.packedTX),
						Pair("compression", "none"),
						Pair("packed_context_free_data", "00")
					),
					error
				)
			} else hold(null, error)
		}
	}
}