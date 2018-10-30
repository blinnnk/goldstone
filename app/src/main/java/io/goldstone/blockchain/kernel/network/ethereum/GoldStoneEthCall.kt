@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package io.goldstone.blockchain.kernel.network.ethereum

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isNullValue
import com.blinnnk.extension.safeGet
import com.blinnnk.util.TinyNumberUtils
import io.goldstone.blockchain.common.error.EthereumRPCError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.utils.LogUtil
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
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil.callChainBy
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.runOnUiThread
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
		errorCallback: (EthereumRPCError) -> Unit,
		chainURL: ChainURL,
		hold: (
			symbol: String,
			name: String,
			decimal: Int
		) -> Unit
	) {
		getTokenSymbolByContract(contractAddress, errorCallback, chainURL) { symbol ->
			getTokenName(contractAddress, errorCallback, chainURL) { name ->
				getTokenDecimal(contractAddress, errorCallback, chainURL) { decimal ->
					hold(symbol, name, decimal)
				}
			}
		}
	}

	@JvmStatic
	fun getSymbolAndDecimalByContract(
		contractAddress: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainURL: ChainURL,
		hold: (
			symbol: String,
			decimal: Int
		) -> Unit
	) {
		getTokenSymbolByContract(contractAddress, errorCallback, chainURL) { symbol ->
			getTokenDecimal(contractAddress, errorCallback, chainURL) { decimal ->
				hold(symbol, decimal)
			}
		}
	}

	@JvmStatic
	fun getInputCodeByHash(
		hash: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainURL: ChainURL,
		holdValue: (String) -> Unit
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
			{
				errorCallback(EthereumRPCError.GetInputCode(it))
			},
			chainURL
		) {
			holdValue(JSONObject(it).safeGet("input"))
		}
	}

	@JvmStatic
	fun getUsableNonce(
		errorCallback: (EthereumRPCError) -> Unit,
		chainURL: ChainURL,
		address: String,
		holdValue: (BigInteger) -> Unit
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
			{ errorCallback(EthereumRPCError.GetUsableNonce(it)) },
			chainURL
		) {
			holdValue(it.hexToDecimal().toBigDecimal().toBigInteger())
		}
	}

	@JvmStatic
	fun getBlockNumber(
		errorCallback: (EthereumRPCError) -> Unit,
		chainURL: ChainURL,
		holdValue: (Int) -> Unit
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
			{ errorCallback(EthereumRPCError.GetBlockNumber(it)) },
			chainURL
		) {
			holdValue(it.hexToDecimal().toInt())
		}
	}

	@JvmStatic
	fun getBlockTimeStampByBlockHash(
		blockHash: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainURL: ChainURL,
		holdValue: (BigInteger) -> Unit
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
			{ errorCallback(EthereumRPCError.GetBlockTimeByBlockHash(it)) },
			chainURL
		) {
			if (it.isNull()) LogUtil.error("getBlockTimeStampByBlockHash result is null")
			else {
				holdValue(JSONObject(it).safeGet("timestamp").hexToDecimal())
			}
		}
	}

	@JvmStatic
	fun getTransactionByHash(
		hash: String,
		chainURL: ChainURL,
		unfinishedCallback: () -> Unit = {},
		errorCallback: (EthereumRPCError) -> Unit,
		hold: (TransactionTable) -> Unit
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
			{ errorCallback(EthereumRPCError.GetTransactionByHash(it)) },
			chainURL
		) { it ->
			val data = it.toJsonObject()
			if (data.safeGet("blockNumber").isNullValue()) {
				unfinishedCallback()
			} else {
				hold(
					TransactionTable(data, chainURL.chainType.isETC(), chainURL.chainID.id)
				)
			}
		}
	}

	@JvmStatic
	fun getReceiptByHash(
		hash: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainURL: ChainURL,
		hold: (Boolean) -> Unit
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
			{ errorCallback(EthereumRPCError.GetReceiptByHash(it)) },
			chainURL
		) {
			val data = it.toJsonObject()
			// Return Status hasError or Not if
			hold(!TinyNumberUtils.isTrue(data.safeGet("status").toIntFromHex()))
		}
	}

	@JvmStatic
	fun getTransactionExecutedValue(
		to: String,
		from: String,
		data: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainURL: ChainURL,
		hold: (BigInteger) -> Unit
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
			{ errorCallback(EthereumRPCError.GetTransactionExecutedValue(it)) },
			chainURL
		) {
			GoldStoneAPI.context.runOnUiThread {
				try {
					hold(it.toDecimalFromHex().toBigDecimal().toBigInteger())
				} catch (error: Exception) {
					LogUtil.error(this.javaClass.simpleName, error)
				}
			}
		}
	}

	@JvmStatic
	fun sendRawTransaction(
		signTransactions: String,
		errorCallback: (RequestError) -> Unit,
		chainURL: ChainURL,
		@WorkerThread hold: (String) -> Unit
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
			errorCallback,
			chainURL
		) {
			hold(it)
		}
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
			{ hold(null, it) },
			chainURL
		) {
			hold(it.hexToDecimal(), RequestError.None)
		}
	}

	@JvmStatic
	fun getTokenSymbolByContract(
		contractAddress: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainURL: ChainURL,
		hold: (String) -> Unit
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
			{ errorCallback(EthereumRPCError.GetSymbol(it)) },
			chainURL
		) {
			hold(it.toAscii())
		}
	}

	@JvmStatic
	fun getTokenDecimal(
		contractAddress: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainURL: ChainURL,
		holdValue: (Int) -> Unit
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
			{ errorCallback(EthereumRPCError.GetTokenDecimal(it)) },
			chainURL
		) {
			holdValue(it.toIntFromHex())
		}
	}

	@JvmStatic
	fun getTokenName(
		contractAddress: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainURL: ChainURL,
		holdValue: (String) -> Unit
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
			{ errorCallback(EthereumRPCError.GetTokenName(it)) },
			chainURL
		) {
			holdValue(it.toAscii())
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
			{
				holdValue(null, it)
			},
			chainURL
		) {
			holdValue(it.hexToDecimal(), RequestError.None)
		}
	}

	fun getTokenTotalSupply(
		contractAddress: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainURL: ChainURL,
		holdValue: (BigInteger) -> Unit
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
			{ errorCallback(EthereumRPCError.GetTokenTotalSupply(it)) },
			chainURL
		) {
			holdValue(it.hexToDecimal())
		}
	}

	@JvmStatic
	private infix fun String.withAddress(address: String) =
		this + address.checkAddressInRules()

	@JvmStatic
	private fun String.checkAddressInRules() =
		if (substring(0, 2) == "0x") substring(2 until length) else this

}