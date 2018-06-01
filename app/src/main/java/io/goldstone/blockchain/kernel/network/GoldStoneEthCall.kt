@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package io.goldstone.blockchain.kernel.network

import android.annotation.SuppressLint
import android.content.Context
import com.blinnnk.extension.isNull
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.utils.AesCrypto
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.ChainID
import io.goldstone.blockchain.crypto.*
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
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
	
	private enum class Method(
		val method: String,
		val code: String = ""
	) {
		
		EthCall("eth_call", SolidityCode.ethCall),
		GetSymbol("eth_call", SolidityCode.ethCall),
		GetTokenBalance("eth_call", SolidityCode.getTokenBalance),
		GetBalance("eth_getBalance"),
		GetTotalSupply("eth_call", SolidityCode.getTotalSupply),
		GetTokenDecimal("eth_call", SolidityCode.getDecimal),
		GetTokenName("eth_call", SolidityCode.getTokenName),
		SendRawTransaction(
			                  "eth_sendRawTransaction",
			                  SolidityCode.getTokenName
		                  ),
		GetTransactionByHash(
			                    "eth_getTransactionByHash",
			                    SolidityCode.ethCall
		                    ),
		GetEstimateGas("eth_estimateGas", SolidityCode.ethCall),
		PendingFitler("eth_newFilter", SolidityCode.ethCall)
	}
	
	@JvmStatic
	private val contentType = MediaType.parse("application/json; charset=utf-8")
	
	@JvmStatic
	private infix fun String.withAddress(address: String) =
		this + address.checkAddressInRules()
	
	@JvmStatic
	private fun String.checkAddressInRules() =
		if (substring(0, 2) == "0x") substring(2 until length) else this
	
	/**
	 * @description 通过 [contractAddress] 和 [walletAddress] 从节点获取全部的 `Token` 信息
	 */
	@JvmStatic
	fun getAddressInfoInToken(
		contractAddress: String,
		walletAddress: String
	) {
		getTokenSymbolByContract(contractAddress) { symbol ->
			getTokenName(contractAddress) { name ->
				getTokenDecimal(contractAddress) { decimal ->
					getTokenTotalSupply(contractAddress) { totalSupply ->
						getTokenBalanceWithContract(contractAddress, walletAddress) { tokenBalance ->
							// 用的时候再完善这里
							println(symbol + name + decimal + totalSupply + tokenBalance)
						}
					}
				}
			}
		}
	}
	
	@JvmStatic
	fun getTokenInfoByContractAddress(
		contractAddress: String,
		hold: (symbol: String, name: String, decimal: Double) -> Unit
	) {
		getTokenSymbolByContract(contractAddress) { symbol ->
			getTokenName(contractAddress) { name ->
				getTokenDecimal(contractAddress) { decimal ->
					hold(symbol, name, decimal)
				}
			}
		}
	}
	
	@JvmStatic
	fun getTokenSymbolAndDecimalByContract(
		contractAddress: String,
		hold: (symbol: String, decimal: Double) -> Unit
	) {
		getTokenSymbolByContract(contractAddress) { symbol ->
			getTokenDecimal(contractAddress) { decimal ->
				hold(symbol, decimal)
			}
		}
	}
	
	fun getTokenCountWithDecimalByContract(
		contractAddress: String,
		walletAddress: String,
		hold: (Double) -> Unit
	) {
		getTokenBalanceWithContract(contractAddress, walletAddress) { tokenBalance ->
			getTokenDecimal(contractAddress) {
				hold(tokenBalance / Math.pow(10.0, it))
			}
		}
	}
	
	@JvmStatic
	fun getInputCodeByHash(
		hash: String,
		holdValue: (String) -> Unit = {}
	) {
		RequestBody.create(
			contentType, AesCrypto.encrypt(
			"{\"jsonrpc\":\"2.0\", \"method\":\"${Method.GetTransactionByHash.method}\", \"params\":[\"$hash\"], \"id\":1}"
		)
		).let {
			callEthBy(it) {
				holdValue(JSONObject(it).safeGet("input"))
			}
		}
	}
	
	@JvmStatic
	fun getTransactionByHash(
		hash: String,
		unfinishedCallback: () -> Unit = {},
		holdValue: (TransactionTable) -> Unit
	) {
		RequestBody.create(
			contentType, AesCrypto.encrypt(
			"{\"jsonrpc\":\"2.0\", \"method\":\"${Method.GetTransactionByHash.method}\", \"params\":[\"$hash\"], \"id\":1}"
		)
		).let {
			callEthBy(it) {
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
	fun getTransactionExecutedValue(
		to: String,
		from: String,
		data: String,
		holdValue: (BigInteger) -> Unit = {}
	) {
		System.out.println("to$to+from$from+$data")
		RequestBody.create(
			contentType, AesCrypto.encrypt(
			"{\"jsonrpc\":\"2.0\", \"method\":\"${Method.GetEstimateGas.method}\",  \"params\":[{\"to\": \"$to\", \"from\": \"$from\", \"data\": \"$data\"}],\"id\":1}"
		)
		).let {
			callEthBy(it) {
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
		holdValue: (String) -> Unit
	) {
		RequestBody.create(
			contentType, AesCrypto.encrypt(
			"{\"jsonrpc\":\"2.0\", \"method\":\"${Method.SendRawTransaction.method}\", \"params\":[\"$signTransactions\"], \"id\":1}"
		)
		).let {
			callEthBy(it) { holdValue(it) }
		}
	}
	
	@JvmStatic
	fun getTokenBalanceWithContract(
		contractAddress: String,
		address: String,
		holdValue: (Double) -> Unit
	) {
		RequestBody.create(
			contentType, AesCrypto.encrypt(
			"{\"jsonrpc\":\"2.0\", \"method\":\"${Method.GetTokenBalance.method}\", \"params\":[{ \"to\": \"$contractAddress\", \"data\": \"${Method.GetTokenBalance.code withAddress address}\"}, \"latest\"], \"id\":1}"
		)
		).let {
			callEthBy(it) { holdValue(it.hexToDecimal()) }
		}
	}
	
	@JvmStatic
	fun getTokenSymbolByContract(
		contractAddress: String,
		holdValue: (String) -> Unit = {}
	) {
		RequestBody.create(
			contentType,
			AesCrypto.encrypt("{\"jsonrpc\":\"2.0\", \"method\":\"${Method.GetSymbol.method}\", \"params\":[{ \"to\": \"$contractAddress\", \"data\": \"${Method.GetSymbol.code}\"}, \"latest\"], \"id\":1}")
		).let {
			callEthBy(it) { holdValue(it.toAscii()) }
		}
	}
	
	@JvmStatic
	private fun getTokenDecimal(
		contractAddress: String,
		holdValue: (Double) -> Unit = {}
	) {
		RequestBody.create(
			contentType, AesCrypto.encrypt(
			"{\"jsonrpc\":\"2.0\", \"method\":\"${Method.GetSymbol.method}\", \"params\":[{ \"to\": \"$contractAddress\", \"data\": \"${Method.GetTokenDecimal.code}\"}, \"latest\"], \"id\":1}"
		)
		).let {
			callEthBy(it) { holdValue(it.hexToDecimal()) }
		}
	}
	
	@JvmStatic
	private fun getTokenName(
		contractAddress: String,
		holdValue: (String) -> Unit = {}
	) {
		RequestBody.create(
			contentType,
			AesCrypto.encrypt("{\"jsonrpc\":\"2.0\", \"method\":\"${Method.GetTokenName.method}\", \"params\":[{ \"to\": \"$contractAddress\", \"data\": \"${Method.GetTokenName.code}\"}, \"latest\"], \"id\":1}")
		).let {
			callEthBy(it) { holdValue(it.toAscii()) }
		}
	}
	
	fun getEthBalance(
		address: String,
		holdValue: (Double) -> Unit = {}
	) {
		RequestBody.create(
			contentType,
			AesCrypto.encrypt("{\"jsonrpc\":\"2.0\", \"method\":\"${Method.GetBalance.method}\", \"params\":[\"$address\", \"latest\"],\"id\":1}")
		).let {
			callEthBy(it) {
				holdValue(it.hexToDecimal())
			}
		}
	}
	
	private fun getTokenTotalSupply(
		contractAddress: String,
		holdValue: (Double) -> Unit = {}
	) {
		RequestBody.create(
			contentType, AesCrypto.encrypt(
			"{\"jsonrpc\":\"2.0\", \"method\":\"${Method.GetTotalSupply.method}\", \"params\":[{ \"to\": \"$contractAddress\", \"data\": \"${Method.GetTotalSupply.code}\"}, \"latest\"], \"id\":1}"
		)
		).let {
			callEthBy(it) { holdValue(it.hexToDecimal()) }
		}
	}
	
	private val currentChain: (currentChainID: String) -> String = {
		when (it) {
			ChainID.Main.id -> APIPath.main
			ChainID.Ropstan.id -> APIPath.ropstan
			ChainID.Rinkeby.id -> APIPath.rinkeyb
			ChainID.Kovan.id -> APIPath.koven
			else -> APIPath.main
		}
	}
	
	private fun callEthBy(
		body: RequestBody,
		errorCallback: () -> Unit = {},
		hold: (String) -> Unit
	) {
		val client = OkHttpClient
			.Builder()
			.connectTimeout(60, TimeUnit.SECONDS)
			.readTimeout(90, TimeUnit.SECONDS)
			.build()
		
		GoldStoneAPI.getcryptoRequest(body, currentChain(GoldStoneApp.currentChain)) {
			client.newCall(it).enqueue(object : Callback {
				override fun onFailure(call: Call, error: IOException) {
					LogUtil.error(this.javaClass.simpleName, error)
				}
				
				@SuppressLint("SetTextI18n")
				override fun onResponse(
					call: Call,
					response: Response
				) {
					val data = AesCrypto.decrypt(response.body()?.string().orEmpty())
					System.out.println("_____$data")
					try {
						val dataObject =
							JSONObject(data?.substring(data.indexOf("{"), data.lastIndexOf("}") + 1))
						hold(dataObject["result"].toString())
					} catch (error: Exception) {
						errorCallback()
						LogUtil.error(this.javaClass.simpleName, error)
					}
				}
			})
		}
	}
}