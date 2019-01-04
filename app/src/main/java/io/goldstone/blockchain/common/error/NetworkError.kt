package io.goldstone.blockchain.common.error

import io.goldstone.blockchain.common.language.DialogText


/**
 * @author KaySaith
 * @date  2018/10/30
 */
class NetworkError(override val message: String) : GoldStoneError(message) {
	companion object {
		val WithOutNetwork = NetworkError(DialogText.networkDescription)
	}
}