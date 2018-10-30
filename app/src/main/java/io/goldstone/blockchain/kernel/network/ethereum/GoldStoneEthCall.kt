@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package io.goldstone.blockchain.kernel.network.ethereum

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNullValue
import com.blinnnk.extension.orZero
import com.blinnnk.extension.safeGet
import com.blinnnk.util.TinyNumberUtils
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.crypto.ethereum.EthereumMethod
import io.goldstone.blockchain.crypto.keystore.toJsonObject
import io.goldstone.blockchain.crypto.multichain.isETC
import io.goldstone.blockchain.crypto.multichain.node.ChainURL
import io.goldstone.blockchain.crypto.utils.hexToDecimal
import io.goldstone.blockchain.crypto.utils.toAscii
import io.goldstone.blockchain.crypto.utils.toDecimalFromHex
import io.goldstone.blockchain.crypto.utils.toIntFromHex
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.ParameterUtil
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil.callChainBy
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import java.math.BigInteger

@SuppressLint("StaticFieldLeak")
/**
 * @date 31/03/2018 2:44 PM
 * @author KaySaith
 */
object GoldStoneEthCall {

	lateinit var context: Context
	@JvmStatic
	val contentType = MediaType.parse("application/json; charset=utf-8")

	/**
	 * @description 通过 [contractAddress] 从节点获取全部的 `Token` 信息
	 */
	@JvmStatic
	fun getTokenInfoByContractAddress(
		contractAddress: String,
		chainURL: ChainURL,
		hold: (symbol: String?, name: String?, decimal: Int?, error: RequestError) -> Unit
	) {
		getTokenSymbolByContract(contractAddress, chainURL) { symbol, error ->
			getTokenName(contractAddress, chainURL) { name, nameError ->
				getTokenDecimal(contractAddress, chainURL) { decimal, decimalError ->
					hold(
						symbol,
						name,
						decimal,
						when {
							error.isNone() && nameError.isNone() -> decimalError
							!nameError.isNone() -> nameError
							else -> error
						}
					)
				}
			}
		}
	}

	@JvmStatic
	fun getSymbolAndDecimalByContract(
		contractAddress: String,
		chainURL: ChainURL,
		hold: (symbol: String?, decimal: Int?, error: RequestError) -> Unit
	) {
		getTokenSymbolByContract(contractAddress, chainURL) { symbol, symbolError ->
			getTokenDecimal(contractAddress, chainURL) { decimal, decimalError ->
				hold(symbol, decimal, if (symbolError.isNone()) decimalError else symbolError)
			}
		}
	}

	@JvmStatic
	fun getInputCodeByHash(
		hash: String,
		chainURL: ChainURL,
		holdValue: (inputCode: String?, error: RequestError) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.prepareJsonRPC(
					chainURL.isEncrypt,
					EthereumMethod.GetTransactionByHash.method,
					1,
					false,
					true,
					hash
				)
			),
			chainURL
		) { result, error ->
			holdValue(JSONObject(result).safeGet("input"), error)
		}
	}

	@JvmStatic
	fun getUsableNonce(
		chainURL: ChainURL,
		address: String,
		@WorkerThread hold: (nonce: BigInteger?, error: RequestError) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.prepareJsonRPC(
					chainURL.isEncrypt,
					EthereumMethod.GetTransactionCount.method,
					1,
					true,
					true,
					address
				)
			),
			chainURL
		) { result, error ->
			hold(result?.hexToDecimal()?.toBigDecimal()?.toBigInteger(), error)
		}
	}

	@JvmStatic
	fun getBlockNumber(
		chainURL: ChainURL,
		holdValue: (blockNumber: Int?, error: RequestError) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.prepareJsonRPC(
					chainURL.isEncrypt,
					EthereumMethod.GetBlockNumber.method,
					83,
					false,
					true,
					null
				)
			),
			chainURL
		) { result, error ->
			holdValue(result?.hexToDecimal()?.toInt(), error)
		}
	}

	@JvmStatic
	fun getBlockTimeStampByBlockHash(
		blockHash: String,
		chainURL: ChainURL,
		@WorkerThread hold: (timeStamp: BigInteger?, error: RequestError) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.prepareJsonRPC(
					chainURL.isEncrypt,
					EthereumMethod.GetBlockByHash.method,
					1,
					false,
					true,
					blockHash,
					true
				)
			),
			chainURL
		) { result, error ->
			val timeStamp = JSONObject(result).safeGet("timestamp")
			if (timeStamp.isEmpty()) hold(null, error)
			else hold(timeStamp.hexToDecimal(), error)
		}
	}

	@JvmStatic
	fun getTransactionByHash(
		hash: String,
		chainURL: ChainURL,
		unfinishedCallback: () -> Unit = {},
		@WorkerThread hold: (transaction: TransactionTable?, error: RequestError) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.prepareJsonRPC(
					chainURL.isEncrypt,
					EthereumMethod.GetTransactionByHash.method,
					1,
					false,
					true,
					hash
				)
			),
			chainURL
		) { result, error ->
			val data = result?.toJsonObject()
			if (data?.safeGet("blockNumber")?.isNullValue() == true) unfinishedCallback()
			else hold(TransactionTable(data!!, chainURL.chainType.isETC(), chainURL.chainID.id), error)
		}
	}

	@JvmStatic
	fun getReceiptByHash(
		hash: String,
		chainURL: ChainURL,
		hold: (success: Boolean?, error: RequestError) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.prepareJsonRPC(
					chainURL.isEncrypt,
					EthereumMethod.GetTransactionReceiptByHash.method,
					1,
					false,
					true,
					hash
				)
			),
			chainURL
		) { result, error ->
			val data = result?.toJsonObject()
			// Return Status hasError or Not if
			hold(!TinyNumberUtils.isTrue(data?.safeGet("status")?.toIntFromHex().orZero()), error)
		}
	}

	@JvmStatic
	fun getTransactionExecutedValue(
		to: String,
		from: String,
		data: String,
		chainURL: ChainURL,
		@WorkerThread hold: (executedValue: BigInteger?, error: RequestError) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.preparePairJsonRPC(
					chainURL.isEncrypt,
					EthereumMethod.GetEstimateGas.method,
					false,
					true,
					Pair("to", to),
					Pair("from", from),
					Pair("data", data)
				)
			),
			chainURL
		) { result, error ->
			hold(result?.toDecimalFromHex()?.toBigDecimal()?.toBigInteger(), error)
		}
	}

	@JvmStatic
	fun sendRawTransaction(
		signTransactions: String,
		chainURL: ChainURL,
		@WorkerThread hold: (hash: String?, error: RequestError) -> Unit
	) {
		callChainBy(RequestBody.create(
			contentType,
			ParameterUtil.prepareJsonRPC(
				chainURL.isEncrypt,
				EthereumMethod.SendRawTransaction.method,
				1,
				false,
				true,
				signTransactions
			)
		),
			chainURL,
			hold
		)
	}

	@JvmStatic
	fun getTokenBalanceWithContract(
		contractAddress: String,
		address: String,
		chainURL: ChainURL,
		hold: (amount: BigInteger?, error: RequestError) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.preparePairJsonRPC(
					chainURL.isEncrypt,
					EthereumMethod.GetTokenBalance.method,
					true,
					true,
					Pair("to", contractAddress),
					Pair("data", EthereumMethod.GetTokenBalance.code withAddress address)
				)
			),
			chainURL
		) { result, error ->
			hold(result?.hexToDecimal(), error)
		}
	}

	@JvmStatic
	fun getTokenSymbolByContract(
		contractAddress: String,
		chainURL: ChainURL,
		hold: (symbol: String?, error: RequestError) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.preparePairJsonRPC(
					chainURL.isEncrypt,
					EthereumMethod.GetSymbol.method,
					true,
					true,
					Pair("to", contractAddress),
					Pair("data", EthereumMethod.GetSymbol.code)
				)
			),
			chainURL
		) { result, error ->
			hold(result?.toAscii(), error)
		}
	}

	@JvmStatic
	fun getTokenDecimal(
		contractAddress: String,
		chainURL: ChainURL,
		holdValue: (decimal: Int?, error: RequestError) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.preparePairJsonRPC(
					chainURL.isEncrypt,
					EthereumMethod.GetTokenDecimal.method,
					true,
					true,
					Pair("to", contractAddress),
					Pair("data", EthereumMethod.GetTokenDecimal.code)
				)
			),
			chainURL
		) { result, error ->
			holdValue(result?.toIntFromHex(), error)
		}
	}

	@JvmStatic
	fun getTokenName(
		contractAddress: String,
		chainURL: ChainURL,
		holdValue: (name: String?, error: RequestError) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.preparePairJsonRPC(
					chainURL.isEncrypt,
					EthereumMethod.GetTokenName.method,
					true,
					true,
					Pair("to", contractAddress),
					Pair("data", EthereumMethod.GetTokenName.code)
				)
			),
			chainURL
		) { result, error ->
			holdValue(result?.toAscii(), error)
		}
	}

	fun getEthBalance(
		address: String,
		chainURL: ChainURL,
		@WorkerThread holdValue: (amount: BigInteger?, error: RequestError) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.prepareJsonRPC(
					chainURL.isEncrypt,
					EthereumMethod.GetBalance.method,
					1,
					true,
					true,
					address
				)
			),
			chainURL
		) { result, error ->
			holdValue(result?.hexToDecimal(), error)
		}
	}

	fun getTokenTotalSupply(
		contractAddress: String,
		chainURL: ChainURL,
		holdValue: (supply: BigInteger?, error: RequestError) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.preparePairJsonRPC(
					chainURL.isEncrypt,
					EthereumMethod.GetTotalSupply.method,
					true,
					true,
					Pair("to", contractAddress),
					Pair("data", EthereumMethod.GetTotalSupply.code)
				)
			),
			chainURL
		) { result, error ->
			holdValue(result?.hexToDecimal(), error)
		}
	}

	@JvmStatic
	private infix fun String.withAddress(address: String) =
		this + address.checkAddressInRules()

	@JvmStatic
	private fun String.checkAddressInRules() =
		if (substring(0, 2) == "0x") substring(2 until length) else this

}