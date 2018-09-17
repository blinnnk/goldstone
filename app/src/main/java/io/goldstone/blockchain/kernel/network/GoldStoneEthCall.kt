@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package io.goldstone.blockchain.kernel.network

import android.annotation.SuppressLint
import android.content.Context
import com.blinnnk.extension.isNull
import com.blinnnk.extension.safeGet
import com.blinnnk.util.TinyNumberUtils
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.ethereum.EthereumMethod
import io.goldstone.blockchain.crypto.keystore.toJsonObject
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.MultiChainType
import io.goldstone.blockchain.crypto.utils.*
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
		errorCallback: (error: Throwable?, reason: String?) -> Unit,
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
		errorCallback: (error: Throwable?, reason: String?) -> Unit,
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
		errorCallback: (error: Throwable?, reason: String?) -> Unit,
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
			{ error, reason ->
				errorCallback(error, reason)
				LogUtil.error(EthereumMethod.GetTransactionByHash.display, error)
			},
			chainName
		) {
			holdValue(JSONObject(it).safeGet("input"))
		}
	}

	@JvmStatic
	fun getUsableNonce(
		errorCallback: (error: Throwable?, reason: String?) -> Unit,
		chainType: MultiChainType,
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
			{ error, reason ->
				errorCallback(error, reason)
				LogUtil.error(EthereumMethod.GetTransactionCount.display, error)
			},
			ChainType(chainType.id).getCurrentChainName()
		) {
			holdValue(it.hexToDecimal().toBigDecimal().toBigInteger())
		}
	}

	@JvmStatic
	fun getBlockNumber(
		errorCallback: (error: Throwable?, reason: String?) -> Unit,
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
			{ error, reason ->
				errorCallback(error, reason)
				LogUtil.error(EthereumMethod.GetBlockNumber.display, error)
			},
			chainName
		) {
			holdValue(it.hexToDecimal().toInt())
		}
	}

	@JvmStatic
	fun getBlockTimeStampByBlockHash(
		blockHash: String,
		errorCallback: (error: Throwable?, reason: String?) -> Unit,
		chainName: String,
		holdValue: (Long) -> Unit
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
			{ error, reason ->
				errorCallback(error, reason)
				LogUtil.error(EthereumMethod.GetBlockByHash.display, error)
			},
			chainName
		) {
			if (it.isNull()) LogUtil.error("getBlockTimeStampByBlockHash result is null")
			else {
				holdValue(JSONObject(it).safeGet("timestamp").hexToLong())
			}
		}
	}

	@JvmStatic
	fun getTransactionByHash(
		hash: String,
		chainName: String,
		unfinishedCallback: () -> Unit = {},
		errorCallback: (error: Throwable?, reason: String?) -> Unit,
		holdValue: (TransactionTable) -> Unit
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
			{ error, reason ->
				errorCallback(error, reason)
				LogUtil.error(EthereumMethod.GetTransactionByHash.display, error)
			},
			chainName
		) { it ->
			val data = it.toJsonObject()
			if (data.safeGet("blockNumber").toDecimalFromHex().toIntOrNull().isNull()) {
				unfinishedCallback()
			} else {
				holdValue(
					TransactionTable(
						data,
						ChainURL.etcChainName.any {
							it.equals(chainName, true)
						},
						ChainID.getChainIDByName(chainName)
					)
				)
			}
		}
	}

	@JvmStatic
	fun getReceiptByHash(
		hash: String,
		errorCallback: (error: Throwable?, reason: String?) -> Unit,
		chainName: String,
		holdValue: (Boolean) -> Unit
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
			{ error, reason ->
				errorCallback(error, reason)
				LogUtil.error(EthereumMethod.GetTransactionReceiptByHash.display, error)
			},
			chainName
		) {
			val data = it.toJsonObject()
			// Return Status hasError or Not if
			holdValue(!TinyNumberUtils.isTrue(data.safeGet("status").toIntFromHex()))
		}
	}

	@JvmStatic
	fun getTransactionExecutedValue(
		to: String,
		from: String,
		data: String,
		errorCallback: (error: Throwable?, reason: String?) -> Unit,
		chainName: String,
		holdValue: (BigInteger) -> Unit
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
			{ error, reason ->
				errorCallback(error, reason)
				LogUtil.error(EthereumMethod.GetEstimateGas.display, error)
			},
			chainName
		) {
			GoldStoneAPI.context.runOnUiThread {
				try {
					holdValue(it.toDecimalFromHex().toBigDecimal().toBigInteger())
				} catch (error: Exception) {
					LogUtil.error(this.javaClass.simpleName, error)
				}
			}
		}
	}

	@JvmStatic
	fun sendRawTransaction(
		signTransactions: String,
		errorCallback: (error: Throwable?, reason: String?) -> Unit,
		chainName: String,
		holdValue: (String) -> Unit
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
		), errorCallback, chainName) {
			holdValue(it)
		}
	}

	@JvmStatic
	fun getTokenBalanceWithContract(
		contractAddress: String,
		address: String,
		errorCallback: (error: Throwable?, reason: String?) -> Unit,
		chainName: String,
		holdValue: (Double) -> Unit
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
			{ error, reason ->
				errorCallback(error, reason)
				LogUtil.error(EthereumMethod.GetTokenBalance.display, error)
			},
			chainName
		) { holdValue(it.hexToDecimal()) }
	}

	@JvmStatic
	fun getTokenSymbolByContract(
		contractAddress: String,
		errorCallback: (error: Throwable?, reason: String?) -> Unit,
		chainName: String,
		holdValue: (String) -> Unit = {}
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
			{ error, reason ->
				errorCallback(error, reason)
				LogUtil.error(EthereumMethod.GetSymbol.display, error)
			},
			chainName
		) {
			holdValue(it.toAscii())
		}
	}

	@JvmStatic
	fun getTokenDecimal(
		contractAddress: String,
		errorCallback: (error: Throwable?, reason: String?) -> Unit,
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
			{ error, reason ->
				errorCallback(error, reason)
				LogUtil.error(EthereumMethod.GetTokenDecimal.display, error)
			},
			chainName
		) {
			holdValue(it.hexToDecimal().toInt())
		}
	}

	@JvmStatic
	fun getTokenName(
		contractAddress: String,
		errorCallback: (error: Throwable?, reason: String?) -> Unit,
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
			{ error, reason ->
				errorCallback(error, reason)
				LogUtil.error(EthereumMethod.GetTokenName.display, error)
			},
			chainName
		) {
			holdValue(it.toAscii())
		}
	}

	fun getEthBalance(
		address: String,
		errorCallback: (error: Throwable?, reason: String?) -> Unit,
		chainName: String,
		holdValue: (Double) -> Unit
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
			{ error, reason ->
				errorCallback(error, reason)
				LogUtil.error(EthereumMethod.GetBalance.display, error)
			},
			chainName
		) {
			holdValue(it.hexToDecimal())
		}
	}

	fun getTokenTotalSupply(
		contractAddress: String,
		errorCallback: (error: Throwable?, reason: String?) -> Unit,
		chainName: String,
		holdValue: (Double) -> Unit
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
			{ error, reason ->
				errorCallback(error, reason)
				LogUtil.error(EthereumMethod.GetTotalSupply.display, error)
			},
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
	private fun getCurrentEncryptStatusByChainType(type: MultiChainType): Boolean {
		return when (type) {
			MultiChainType.ETC -> Config.isEncryptETCNodeRequest()
			MultiChainType.ETH -> Config.isEncryptERCNodeRequest()
			else -> Config.isEncryptERCNodeRequest()
		}
	}
}