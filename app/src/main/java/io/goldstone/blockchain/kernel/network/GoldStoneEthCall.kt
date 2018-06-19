@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package io.goldstone.blockchain.kernel.network

import android.annotation.SuppressLint
import android.content.Context
import com.blinnnk.extension.isNull
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.utils.AesCrypto
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.TinyNumberUtils
import io.goldstone.blockchain.common.value.ChainID
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.ErrorTag
import io.goldstone.blockchain.crypto.EthereumMethod
import io.goldstone.blockchain.crypto.toJsonObject
import io.goldstone.blockchain.crypto.utils.*
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.RequisitionUtil.getcryptoRequest
import okhttp3.*
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject
import java.io.IOException
import java.math.BigInteger
import java.util.concurrent.TimeUnit

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
		chainID: String = Config.getCurrentChain(),
		hold: (
			symbol: String,
			name: String,
			decimal: Double
		) -> Unit
	) {
		getTokenSymbolByContract(contractAddress, errorCallback, chainID) { symbol ->
			getTokenName(contractAddress, errorCallback) { name ->
				getTokenDecimal(contractAddress, errorCallback, chainID) { decimal ->
					hold(symbol, name, decimal)
				}
			}
		}
	}
	
	@JvmStatic
	fun getSymbolAndDecimalByContract(
		contractAddress: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainID: String = Config.getCurrentChain(),
		hold: (
			symbol: String,
			decimal: Double
		) -> Unit
	) {
		getTokenSymbolByContract(contractAddress, errorCallback, chainID) { symbol ->
			getTokenDecimal(contractAddress, errorCallback, chainID) { decimal ->
				hold(symbol, decimal)
			}
		}
	}
	
	fun getTokenCountWithDecimalByContract(
		contractAddress: String,
		walletAddress: String,
		chainID: String = Config.getCurrentChain(),
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		hold: (Double) -> Unit
	) {
		getTokenBalanceWithContract(contractAddress, walletAddress, errorCallback) { tokenBalance ->
			getTokenDecimal(contractAddress, errorCallback, chainID) {
				hold(tokenBalance / Math.pow(10.0, it))
			}
		}
	}
	
	@JvmStatic
	fun getInputCodeByHash(
		hash: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainID: String = Config.getCurrentChain(),
		holdValue: (String) -> Unit = {}
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.prepareJsonRPC(EthereumMethod.GetTransactionByHash.method, 1, false, hash)
		).let {
			callEthBy(it, { error, reason ->
				errorCallback(error, reason)
				LogUtil.error(EthereumMethod.GetTransactionByHash.display, error)
			}, chainID) {
				holdValue(JSONObject(it).safeGet("input"))
			}
		}
	}
	
	@JvmStatic
	fun getBlockNumber(
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainID: String = Config.getCurrentChain(),
		holdValue: (Int) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.prepareJsonRPC(EthereumMethod.GetBlockNumber.method, 83, false, null)
		).let {
			callEthBy(
				it,
				{ error, reason ->
					errorCallback(error, reason)
					LogUtil.error(EthereumMethod.GetBlockNumber.display, error)
				},
				chainID
			) {
				holdValue(it.hexToDecimal().toInt())
			}
		}
	}
	
	@JvmStatic
	fun getBlockTimeStampByBlockHash(
		blockHash: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainID: String = Config.getCurrentChain(),
		holdValue: (Long) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.prepareJsonRPC(
				EthereumMethod.GetBlockByHash.method,
				1,
				false,
				blockHash,
				true
			)
		).let {
			callEthBy(it, { error, reason ->
				errorCallback(error, reason)
				LogUtil.error(EthereumMethod.GetBlockByHash.display, error)
			}, chainID) {
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
		chainID: String = Config.getCurrentChain(),
		unfinishedCallback: () -> Unit = {},
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		holdValue: (TransactionTable) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.prepareJsonRPC(
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
				chainID
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
		chainID: String = Config.getCurrentChain(),
		holdValue: (Boolean) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.prepareJsonRPC(
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
				chainID
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
		chainID: String = Config.getCurrentChain(),
		holdValue: (BigInteger) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.preparePairJsonRPC(
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
				chainID
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
		chainID: String = Config.getCurrentChain(),
		holdValue: (Double) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.preparePairJsonRPC(
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
				chainID
			) { holdValue(it.hexToDecimal()) }
		}
	}
	
	@JvmStatic
	fun getTokenSymbolByContract(
		contractAddress: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainID: String = Config.getCurrentChain(),
		holdValue: (String) -> Unit = {}
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.preparePairJsonRPC(
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
				chainID
			) {
				holdValue(it.toAscii())
			}
		}
	}
	
	@JvmStatic
	fun getTokenDecimal(
		contractAddress: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainID: String = Config.getCurrentChain(),
		holdValue: (Double) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.preparePairJsonRPC(
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
				chainID
			) {
				holdValue(it.hexToDecimal())
			}
		}
	}
	
	@JvmStatic
	fun getTokenName(
		contractAddress: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainID: String = Config.getCurrentChain(),
		holdValue: (String) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.preparePairJsonRPC(
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
				chainID
			) {
				holdValue(it.toAscii())
			}
		}
	}
	
	fun getEthBalance(
		address: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainID: String = Config.getCurrentChain(),
		holdValue: (Double) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.prepareJsonRPC(
				EthereumMethod.GetBalance.method,
				1,
				true,
				address
			)
		).let {
			callEthBy(it, { error, reason ->
				errorCallback(error, reason)
				LogUtil.error(EthereumMethod.GetBalance.display, error)
			}, chainID) {
				holdValue(it.hexToDecimal())
			}
		}
	}
	
	fun getTokenTotalSupply(
		contractAddress: String,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainID: String = Config.getCurrentChain(),
		holdValue: (Double) -> Unit
	) {
		RequestBody.create(
			contentType,
			ParameterUtil.preparePairJsonRPC(
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
				chainID
			) {
				holdValue(it.hexToDecimal())
			}
		}
	}
	
	private val currentChain: (currentChainID: String) -> String = {
		when (it) {
			ChainID.Main.id -> APIPath.main
			ChainID.Ropstan.id -> APIPath.ropstan
			ChainID.Rinkeby.id -> APIPath.rinkeyb
			ChainID.Kovan.id -> APIPath.kovan
			else -> APIPath.main
		}
	}
	
	private fun callEthBy(
		body: RequestBody,
		errorCallback: (error: Exception?, reason: String?) -> Unit,
		chainID: String = Config.getCurrentChain(),
		hold: (String) -> Unit
	) {
		val client = OkHttpClient
			.Builder()
			.connectTimeout(40, TimeUnit.SECONDS)
			.readTimeout(60, TimeUnit.SECONDS)
			.build()
		getcryptoRequest(body, currentChain(chainID)) {
			client.newCall(it).enqueue(object : Callback {
				override fun onFailure(call: Call, error: IOException) {
					GoldStoneAPI.context.runOnUiThread {
						errorCallback(error, "Call Ethereum Failured")
					}
				}
				
				@SuppressLint("SetTextI18n")
				override fun onResponse(
					call: Call,
					response: Response
				) {
					val data = AesCrypto.decrypt(response.body()?.string().orEmpty())
					checkChainErrorCode(data).let {
						if (it.isNotEmpty()) {
							GoldStoneAPI.context.runOnUiThread {
								errorCallback(null, it)
							}
							return
						}
					}
					try {
						val dataObject =
							JSONObject(data?.substring(data.indexOf("{"), data.lastIndexOf("}") + 1))
						hold(dataObject["result"].toString())
					} catch (error: Exception) {
						GoldStoneAPI.context.runOnUiThread {
							errorCallback(error, "onResponse Error")
						}
					}
				}
			})
		}
	}
	
	private fun checkChainErrorCode(data: String?): String {
		val hasError = data?.contains("error")
		val errorData: String
		if (hasError == true) {
			errorData = JSONObject(data).safeGet("error")
		} else {
			val code =
				if (data?.contains("code") == true)
					JSONObject(data).get("code")?.toString()?.toIntOrNull()
				else null
			return if (code == -10) ErrorTag.chain
			else ""
		}
		return when {
			data.isNullOrBlank() -> return ""
			errorData.isNotEmpty() -> JSONObject(errorData).safeGet("message")
			else -> ""
		}
	}
	
	@JvmStatic
	private infix fun String.withAddress(address: String) =
		this + address.checkAddressInRules()
	
	@JvmStatic
	private fun String.checkAddressInRules() =
		if (substring(0, 2) == "0x") substring(2 until length) else this
}