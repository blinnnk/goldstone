@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package io.goldstone.blockchain.kernel.network

import android.annotation.SuppressLint
import android.content.Context
import com.blinnnk.extension.isNull
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.TinyNumberUtils
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.EthereumMethod
import io.goldstone.blockchain.crypto.toJsonObject
import io.goldstone.blockchain.crypto.utils.*
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.RequisitionUtil.callEthBy
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
	private val contentType = MediaType.parse("application/json; charset=utf-8")
	
	/**
	 * @description 通过 [contractAddress] 和 [walletAddress] 从节点获取全部的 `Token` 信息
	 */
	@JvmStatic
	fun getTokenInfoByContractAddress(
		contractAddress: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainName: String = Config.getCurrentChainName(),
		hold: (
			symbol: String,
			name: String,
			decimal: Double
		) -> Unit
	) {
		getTokenSymbolByContract(contractAddress, errorCallback, chainName) { symbol ->
			getTokenName(contractAddress, errorCallback) { name ->
				getTokenDecimal(contractAddress, errorCallback, chainName) { decimal ->
					hold(symbol, name, decimal)
				}
			}
		}
	}
	
	@JvmStatic
	fun getSymbolAndDecimalByContract(
		contractAddress: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainName: String = Config.getCurrentChainName(),
		hold: (
			symbol: String,
			decimal: Double
		) -> Unit
	) {
		getTokenSymbolByContract(contractAddress, errorCallback, chainName) { symbol ->
			getTokenDecimal(contractAddress, errorCallback, chainName) { decimal ->
				hold(symbol, decimal)
			}
		}
	}
	
	fun getTokenCountWithDecimalByContract(
		contractAddress: String,
		walletAddress: String,
		chainName: String = Config.getCurrentChainName(),
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		hold: (Double) -> Unit
	) {
		getTokenBalanceWithContract(contractAddress, walletAddress, errorCallback) { tokenBalance ->
			getTokenDecimal(contractAddress, errorCallback, chainName) {
				hold(tokenBalance / Math.pow(10.0, it))
			}
		}
	}
	
	@JvmStatic
	fun getInputCodeByHash(
		hash: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainName: String = Config.getCurrentChainName(),
		holdValue: (String) -> Unit = {}
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.prepareJsonRPC(
				Config.isEncryptERCNodeRequest(),
				EthereumMethod.GetTransactionByHash.method,
				1,
				false,
				hash
			)
		).let {
			callEthBy(
				it,
				{ error, reason ->
					errorCallback(error, reason)
					LogUtil.error(EthereumMethod.GetTransactionByHash.display, error)
				},
				chainName
			) {
				holdValue(JSONObject(it).safeGet("input"))
			}
		}
	}
	
	@JvmStatic
	fun getBlockNumber(
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainName: String = Config.getCurrentChainName(),
		holdValue: (Int) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.prepareJsonRPC(
				Config.isEncryptERCNodeRequest(),
				EthereumMethod.GetBlockNumber.method,
				83,
				false,
				null
			)
		).let {
			callEthBy(
				it,
				{ error, reason ->
					errorCallback(error, reason)
					LogUtil.error(EthereumMethod.GetBlockNumber.display, error)
				},
				chainName
			) {
				holdValue(it.hexToDecimal().toInt())
			}
		}
	}
	
	@JvmStatic
	fun getBlockTimeStampByBlockHash(
		blockHash: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainName: String = Config.getCurrentChainName(),
		holdValue: (Long) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.prepareJsonRPC(
				Config.isEncryptERCNodeRequest(),
				EthereumMethod.GetBlockByHash.method,
				1,
				false,
				blockHash,
				true
			)
		).let {
			callEthBy(
				it,
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
	}
	
	@JvmStatic
	fun getTransactionByHash(
		hash: String,
		chainName: String = Config.getCurrentChainName(),
		unfinishedCallback: () -> Unit = {},
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		holdValue: (TransactionTable) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.prepareJsonRPC(
				Config.isEncryptERCNodeRequest(),
				EthereumMethod.GetTransactionByHash.method,
				1,
				false,
				hash
			)
		).let {
			callEthBy(
				it,
				{ error, reason ->
					errorCallback(error, reason)
					LogUtil.error(EthereumMethod.GetTransactionByHash.display, error)
				},
				chainName
			) {
				val data = it.toJsonObject()
				if (data.safeGet("blockNumber").toDecimalFromHex().toIntOrNull().isNull()) {
					unfinishedCallback()
				} else {
					holdValue(TransactionTable(data))
				}
			}
		}
	}
	
	@JvmStatic
	fun getReceiptByHash(
		hash: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainName: String = Config.getCurrentChainName(),
		holdValue: (Boolean) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.prepareJsonRPC(
				Config.isEncryptERCNodeRequest(),
				EthereumMethod.GetTransactionReceiptByHash.method,
				1,
				false,
				hash
			)
		).let {
			callEthBy(
				it,
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
	}
	
	@JvmStatic
	fun getTransactionExecutedValue(
		to: String,
		from: String,
		data: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainName: String = Config.getCurrentChainName(),
		holdValue: (BigInteger) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.preparePairJsonRPC(
				Config.isEncryptERCNodeRequest(),
				EthereumMethod.GetEstimateGas.method,
				false,
				Pair("to", to),
				Pair("from", from),
				Pair("data", data)
			)
		).let {
			callEthBy(
				it,
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
	}
	
	@JvmStatic
	fun sendRawTransaction(
		signTransactions: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		holdValue: (String) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.prepareJsonRPC(
				true,
				EthereumMethod.SendRawTransaction.method,
				1,
				false,
				signTransactions
			)
		).let {
			callEthBy(it, errorCallback) {
				holdValue(it)
			}
		}
	}
	
	@JvmStatic
	fun getTokenBalanceWithContract(
		contractAddress: String,
		address: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainName: String = Config.getCurrentChainName(),
		holdValue: (Double) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.preparePairJsonRPC(
				Config.isEncryptERCNodeRequest(),
				EthereumMethod.GetTokenBalance.method,
				true,
				Pair("to", contractAddress),
				Pair("data", EthereumMethod.GetTokenBalance.code withAddress address)
			)
		).let {
			callEthBy(
				it,
				{ error, reason ->
					errorCallback(error, reason)
					LogUtil.error(EthereumMethod.GetTokenBalance.display, error)
				},
				chainName
			) { holdValue(it.hexToDecimal()) }
		}
	}
	
	@JvmStatic
	fun getTokenSymbolByContract(
		contractAddress: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainName: String = Config.getCurrentChainName(),
		holdValue: (String) -> Unit = {}
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.preparePairJsonRPC(
				Config.isEncryptERCNodeRequest(),
				EthereumMethod.GetSymbol.method,
				true,
				Pair("to", contractAddress),
				Pair("data", EthereumMethod.GetSymbol.code)
			)
		).let {
			callEthBy(
				it,
				{ error, reason ->
					errorCallback(error, reason)
					LogUtil.error(EthereumMethod.GetSymbol.display, error)
				},
				chainName
			) {
				holdValue(it.toAscii())
			}
		}
	}
	
	@JvmStatic
	fun getTokenDecimal(
		contractAddress: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainName: String = Config.getCurrentChainName(),
		holdValue: (Double) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.preparePairJsonRPC(
				Config.isEncryptERCNodeRequest(),
				EthereumMethod.GetTokenDecimal.method,
				true,
				Pair("to", contractAddress),
				Pair("data", EthereumMethod.GetTokenDecimal.code)
			)
		).let {
			callEthBy(
				it,
				{ error, reason ->
					errorCallback(error, reason)
					LogUtil.error(EthereumMethod.GetTokenDecimal.display, error)
				},
				chainName
			) {
				holdValue(it.hexToDecimal())
			}
		}
	}
	
	@JvmStatic
	fun getTokenName(
		contractAddress: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainName: String = Config.getCurrentChainName(),
		holdValue: (String) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.preparePairJsonRPC(
				Config.isEncryptERCNodeRequest(),
				EthereumMethod.GetTokenName.method,
				true,
				Pair("to", contractAddress),
				Pair("data", EthereumMethod.GetTokenName.code)
			)
		).let {
			callEthBy(
				it,
				{ error, reason ->
					errorCallback(error, reason)
					LogUtil.error(EthereumMethod.GetTokenName.display, error)
				},
				chainName
			) {
				holdValue(it.toAscii())
			}
		}
	}
	
	fun getEthBalance(
		address: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainName: String = Config.getCurrentChainName(),
		holdValue: (Double) -> Unit
	) {
		val isEncrypt = ChainURL.uncryptChainName.none { it.equals(chainName, true) }
		RequestBody.create(
			contentType,
			ParameterUtil.prepareJsonRPC(
				isEncrypt,
				EthereumMethod.GetBalance.method,
				1,
				true,
				address
			)
		).let {
			callEthBy(
				it,
				{ error, reason ->
					errorCallback(error, reason)
					LogUtil.error(EthereumMethod.GetBalance.display, error)
				},
				chainName
			) {
				holdValue(it.hexToDecimal())
			}
		}
	}
	
	fun getTokenTotalSupply(
		contractAddress: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainName: String = Config.getCurrentChainName(),
		holdValue: (Double) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.preparePairJsonRPC(
				Config.isEncryptERCNodeRequest(),
				EthereumMethod.GetTotalSupply.method,
				true,
				Pair("to", contractAddress),
				Pair("data", EthereumMethod.GetTotalSupply.code)
			)
		).let {
			callEthBy(
				it,
				{ error, reason ->
					errorCallback(error, reason)
					LogUtil.error(EthereumMethod.GetTotalSupply.display, error)
				},
				chainName
			) {
				holdValue(it.hexToDecimal())
			}
		}
	}
	
	@JvmStatic
	private infix fun String.withAddress(address: String) =
		this + address.checkAddressInRules()
	
	@JvmStatic
	private fun String.checkAddressInRules() =
		if (substring(0, 2) == "0x") substring(2 until length) else this
}