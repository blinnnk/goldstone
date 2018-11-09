package io.goldstone.blockchain.kernel.network.eos.contract

import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import com.subgraph.orchid.encoders.Hex
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.crypto.eos.EOSTransactionSerialization
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.eos.ecc.Sha256
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
		@UiThread hold: (response: EOSResponse?, error: GoldStoneError) -> Unit
	) {
		serialized { data, error ->
			if (data != null && error.isNone()) {
				val signature = privateKey.sign(Sha256.from(Hex.decode(data.serialized))).toString()
				EOSAPI.pushTransaction(
					listOf(signature),
					data.packedTX,
					true,
					hold
				)
			} else hold(null, error)
		}
	}

	open fun getSignHash(
		privateKey: String,
		hold: (signedHash: String?, error: GoldStoneError) -> String
	) {
		serialized { data, error ->
			if (data != null && error.isNone()) {
				hold(EOSPrivateKey(privateKey).sign(Sha256.from(Hex.decode(data.serialized))).toString(), GoldStoneError.None)
			} else hold(null, error)
		}
	}
}