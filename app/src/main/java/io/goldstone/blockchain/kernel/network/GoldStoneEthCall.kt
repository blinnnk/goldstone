@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package io.goldstone.blockchain.kernel.network

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
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.ethereum.EthereumMethod
import io.goldstone.blockchain.crypto.keystore.toJsonObject
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.utils.hexToDecimal
import io.goldstone.blockchain.crypto.utils.toAscii
import io.goldstone.blockchain.crypto.utils.toDecimalFromHex
import io.goldstone.blockchain.crypto.utils.toIntFromHex
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.RequisitionUtil.callChainBy
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
		chainName: String,
		hold: (
			symbol: String,
			name: String,
			decimal: Int
		) -> Unit
	) {
		getTokenSymbolByContract(contractAddress, errorCallback, chainName) { symbol ->
			getTokenName(contractAddress, errorCallback, chainName) { name ->
				getTokenDecimal(contractAddress, errorCallback, chainName) { decimal ->
					hold(symbol, name, decimal)
				}
			}
		}
	}

	@JvmStatic
	fun getSymbolAndDecimalByContract(
		contractAddress: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainName: String,
		hold: (
			symbol: String,
			decimal: Int
		) -> Unit
	) {
		getTokenSymbolByContract(contractAddress, errorCallback, chainName) { symbol ->
			getTokenDecimal(contractAddress, errorCallback, chainName) { decimal ->
				hold(symbol, decimal)
			}
		}
	}

	@JvmStatic
	fun getInputCodeByHash(
		hash: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainName: String,
		holdValue: (String) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.prepareJsonRPC(
					ChainURL.getCurrentEncryptStatusByNodeName(chainName),
					EthereumMethod.GetTransactionByHash.method,
					1,
					false,
					true,
					hash
				)
			),
			{ errorCallback(EthereumRPCError.GetInputCode(it)) },
			chainName
		) {
			holdValue(JSONObject(it).safeGet("input"))
		}
	}

	@JvmStatic
	fun getUsableNonce(
		errorCallback: (EthereumRPCError) -> Unit,
		chainType: ChainType,
		address: String,
		holdValue: (BigInteger) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.prepareJsonRPC(
					getCurrentEncryptStatusByChainType(chainType),
					EthereumMethod.GetTransactionCount.method,
					1,
					true,
					true,
					address
				)
			),
			{ errorCallback(EthereumRPCError.GetUsableNonce(it)) },
			ChainType(chainType.id).getCurrentChainName()
		) {
			holdValue(it.hexToDecimal().toBigDecimal().toBigInteger())
		}
	}

	@JvmStatic
	fun getBlockNumber(
		errorCallback: (EthereumRPCError) -> Unit,
		chainName: String,
		holdValue: (Int) -> Unit
	) {

		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.prepareJsonRPC(
					ChainURL.getCurrentEncryptStatusByNodeName(chainName),
					EthereumMethod.GetBlockNumber.method,
					83,
					false,
					true,
					null
				)
			),
			{ errorCallback(EthereumRPCError.GetBlockNumber(it)) },
			chainName
		) {
			holdValue(it.hexToDecimal().toInt())
		}
	}

	@JvmStatic
	fun getBlockTimeStampByBlockHash(
		blockHash: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainName: String,
		holdValue: (BigInteger) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.prepareJsonRPC(
					ChainURL.getCurrentEncryptStatusByNodeName(chainName),
					EthereumMethod.GetBlockByHash.method,
					1,
					false,
					true,
					blockHash,
					true
				)
			),
			{ errorCallback(EthereumRPCError.GetBlockTimeByBlockHash(it)) },
			chainName
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
		chainName: String,
		unfinishedCallback: () -> Unit = {},
		errorCallback: (EthereumRPCError) -> Unit,
		hold: (TransactionTable) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.prepareJsonRPC(
					ChainURL.getCurrentEncryptStatusByNodeName(chainName),
					EthereumMethod.GetTransactionByHash.method,
					1,
					false,
					true,
					hash
				)
			),
			{ errorCallback(EthereumRPCError.GetTransactionByHash(it)) },
			chainName
		) { it ->
			val data = it.toJsonObject()
			if (data.safeGet("blockNumber").isNullValue()) {
				unfinishedCallback()
			} else {
				hold(
					TransactionTable(
						data,
						ChainURL.etcChainName.any { it.equals(chainName, true) },
						ChainID.getChainIDByName(chainName)
					)
				)
			}
		}
	}

	@JvmStatic
	fun getReceiptByHash(
		hash: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainName: String,
		hold: (Boolean) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.prepareJsonRPC(
					ChainURL.getCurrentEncryptStatusByNodeName(chainName),
					EthereumMethod.GetTransactionReceiptByHash.method,
					1,
					false,
					true,
					hash
				)
			),
			{ errorCallback(EthereumRPCError.GetReceiptByHash(it)) },
			chainName
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
		chainName: String,
		hold: (BigInteger) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.preparePairJsonRPC(
					ChainURL.getCurrentEncryptStatusByNodeName(chainName),
					EthereumMethod.GetEstimateGas.method,
					false,
					true,
					Pair("to", to),
					Pair("from", from),
					Pair("data", data)
				)
			),
			{ errorCallback(EthereumRPCError.GetTransactionExecutedValue(it)) },
			chainName
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
		chainName: String,
		@WorkerThread hold: (String) -> Unit
	) {
		callChainBy(RequestBody.create(
			contentType,
			ParameterUtil.prepareJsonRPC(
				ChainURL.getCurrentEncryptStatusByNodeName(chainName),
				EthereumMethod.SendRawTransaction.method,
				1,
				false,
				true,
				signTransactions
			)
		),
			errorCallback,
			chainName
		) {
			hold(it)
		}
	}

	@JvmStatic
	fun getTokenBalanceWithContract(
		contractAddress: String,
		address: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainName: String,
		hold: (BigInteger) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.preparePairJsonRPC(
					ChainURL.getCurrentEncryptStatusByNodeName(chainName),
					EthereumMethod.GetTokenBalance.method,
					true,
					true,
					Pair("to", contractAddress),
					Pair("data", EthereumMethod.GetTokenBalance.code withAddress address)
				)
			),
			{ errorCallback(EthereumRPCError.GetTokenBalance(it)) },
			chainName
		) { hold(it.hexToDecimal()) }
	}

	@JvmStatic
	fun getTokenSymbolByContract(
		contractAddress: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainName: String,
		hold: (String) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.preparePairJsonRPC(
					ChainURL.getCurrentEncryptStatusByNodeName(chainName),
					EthereumMethod.GetSymbol.method,
					true,
					true,
					Pair("to", contractAddress),
					Pair("data", EthereumMethod.GetSymbol.code)
				)
			),
			{ errorCallback(EthereumRPCError.GetSymbol(it)) },
			chainName
		) {
			hold(it.toAscii())
		}
	}

	@JvmStatic
	fun getTokenDecimal(
		contractAddress: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainName: String,
		holdValue: (Int) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.preparePairJsonRPC(
					ChainURL.getCurrentEncryptStatusByNodeName(chainName),
					EthereumMethod.GetTokenDecimal.method,
					true,
					true,
					Pair("to", contractAddress),
					Pair("data", EthereumMethod.GetTokenDecimal.code)
				)
			),
			{ errorCallback(EthereumRPCError.GetTokenDecimal(it)) },
			chainName
		) {
			holdValue(it.toIntFromHex())
		}
	}

	@JvmStatic
	fun getTokenName(
		contractAddress: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainName: String,
		holdValue: (String) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.preparePairJsonRPC(
					ChainURL.getCurrentEncryptStatusByNodeName(chainName),
					EthereumMethod.GetTokenName.method,
					true,
					true,
					Pair("to", contractAddress),
					Pair("data", EthereumMethod.GetTokenName.code)
				)
			),
			{ errorCallback(EthereumRPCError.GetTokenName(it)) },
			chainName
		) {
			holdValue(it.toAscii())
		}
	}

	fun getEthBalance(
		address: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainName: String,
		@WorkerThread holdValue: (BigInteger) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.prepareJsonRPC(
					ChainURL.getCurrentEncryptStatusByNodeName(chainName),
					EthereumMethod.GetBalance.method,
					1,
					true,
					true,
					address
				)
			),
			{ errorCallback(EthereumRPCError.GetETHBalance(it)) },
			chainName
		) {
			holdValue(it.hexToDecimal())
		}
	}

	fun getTokenTotalSupply(
		contractAddress: String,
		errorCallback: (EthereumRPCError) -> Unit,
		chainName: String,
		holdValue: (BigInteger) -> Unit
	) {
		callChainBy(
			RequestBody.create(
				contentType,
				ParameterUtil.preparePairJsonRPC(
					ChainURL.getCurrentEncryptStatusByNodeName(chainName),
					EthereumMethod.GetTotalSupply.method,
					true,
					true,
					Pair("to", contractAddress),
					Pair("data", EthereumMethod.GetTotalSupply.code)
				)
			),
			{ errorCallback(EthereumRPCError.GetTokenTotalSupply(it)) },
			chainName
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

	@JvmStatic
	private fun getCurrentEncryptStatusByChainType(type: ChainType): Boolean {
		return when (type) {
			ChainType.ETC -> Config.isEncryptETCNodeRequest()
			ChainType.ETH -> Config.isEncryptERCNodeRequest()
			else -> Config.isEncryptERCNodeRequest()
		}
	}
}