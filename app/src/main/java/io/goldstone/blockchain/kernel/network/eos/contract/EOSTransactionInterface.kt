package io.goldstone.blockchain.kernel.network.eos.contract

import android.support.annotation.UiThread
import com.subgraph.orchid.encoders.Hex
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.eos.EOSTransactionSerialization
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.eos.ecc.Sha256
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import org.jetbrains.anko.runOnUiThread


/**
 * @author KaySaith
 * @date  2018/09/19
 */
abstract class EOSTransactionInterface {

	@Throws
	abstract fun serialized(
		errorCallback: (GoldStoneError) -> Unit,
		@UiThread hold: (EOSTransactionSerialization) -> Unit
	)

	open fun send(
		privateKey: EOSPrivateKey,
		errorCallback: (GoldStoneError) -> Unit,
		@UiThread hold: (EOSResponse) -> Unit
	) {
		serialized(errorCallback) { data ->
			val signature = privateKey.sign(Sha256.from(Hex.decode(data.serialized))).toString()
			EOSAPI.pushTransaction(
				listOf(signature),
				data.packedTX,
				errorCallback
			) {
				GoldStoneAPI.context.runOnUiThread { hold(it) }
			}
		}
	}

	open fun getSignHash(
		privateKey: String,
		errorCallback: (Throwable) -> Unit,
		hold: (signedHash: String) -> String
	) {
		serialized(errorCallback) {
			hold(EOSPrivateKey(privateKey).sign(Sha256.from(Hex.decode(it.serialized))).toString())
		}
	}
}