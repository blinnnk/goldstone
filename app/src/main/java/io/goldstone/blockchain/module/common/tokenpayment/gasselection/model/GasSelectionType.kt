package io.goldstone.blockchain.module.common.tokenpayment.gasselection.model

import com.blinnnk.extension.suffix
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.isBTCSeries
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.crypto.utils.scaleToGwei
import io.goldstone.blockchain.crypto.utils.toBTCCount
import io.goldstone.blockchain.crypto.utils.toEthCount
import io.goldstone.blockchain.module.common.tokenpayment.gaseditor.presenter.GasFee
import java.io.Serializable
import java.math.BigInteger

/**
 * @date 2018/5/16 11:37 PM
 * @author KaySaith
 */
data class GasSelectionModel(
	val count: String,
	val info: String,
	var type: MinerFeeType,
	val unitSymbol: CoinSymbol
) : Serializable {

	constructor(
		fee: GasFee,
		symbol: CoinSymbol
	) : this(
		generateChainCount(symbol, fee),
		generateDescription(symbol.isBTCSeries(), fee),
		fee.type,
		symbol
	)

	companion object {

		fun generateChainCount(symbol: CoinSymbol, fee: GasFee): String {
			return if (symbol.isBTCSeries()) "${(fee.gasPrice * fee.gasLimit).toBTCCount().toBigDecimal()} ${symbol.symbol}"
			else BigInteger.valueOf((fee.gasPrice.scaleToGwei() * fee.gasLimit)).toEthCount().formatCount(6) suffix symbol.symbol
		}

		fun generateDescription(isBTCSeries: Boolean, fee: GasFee): String {
			return if (isBTCSeries) "≈ ${fee.gasPrice} Satoshi  * ${fee.gasLimit} bytes"
			else "≈ ${fee.gasPrice} Gwei (${TransactionText.gasPrice}) * ${fee.gasLimit} (${TransactionText.gasLimit})"
		}
	}
}