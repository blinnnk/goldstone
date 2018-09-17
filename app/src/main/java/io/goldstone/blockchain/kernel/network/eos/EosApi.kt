package io.goldstone.blockchain.kernel.network.eos

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.common.value.PageInfo
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.eos.accountregister.EOSResponse
import io.goldstone.blockchain.crypto.eos.header.TransactionHeader
import io.goldstone.blockchain.crypto.eos.transaction.ExpirationType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.network.*
import io.goldstone.blockchain.kernel.network.eos.commonmodel.EOSChainInfo
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.EOSAccountInfo
import okhttp3.RequestBody
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigInteger

object EOSAPI {

	var hasRetry = false
	fun getAccountInfoByName(
		accountName: String,
		errorCallBack: (Throwable) -> Unit,
		targetNet: String = "",
		@WorkerThread hold: (EOSAccountTable) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareObjectContent(Pair("account_name", accountName))
		).let { requestBody ->
			val api =
				if (targetNet.isEmpty()) EOSUrl.getAccountInfo()
				else EOSUrl.getAccountInfoInTargetNet(targetNet)
			RequisitionUtil.postRequest(
				requestBody,
				api,
				errorCallBack,
				false
			) { result ->
				// 测试网络挂了的时候, 换一个网络请求接口. 目前值处理了测试网络的情况
				// TODO 整体的链重试需要思考怎么封装
				if (!hasRetry && result.contains("NODEOS_UNREACHABLE")) {
					EOSUrl.currentEOSTestUrl = ChainURL.eosTestBackUp
					getAccountInfoByName(accountName, errorCallBack, "", hold)
					hasRetry = true
				}
				hold(EOSAccountTable(JSONObject(result)))
			}
		}
	}

	fun getAccountNameByPublicKey(
		publicKey: String,
		errorCallBack: (Throwable) -> Unit,
		targetNet: String = "",
		@UiThread hold: (accountNames: List<EOSAccountInfo>) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareObjectContent(Pair("public_key", publicKey))
		).let { it ->
			val api =
				if (targetNet.isEmpty()) EOSUrl.getKeyAccount()
				else EOSUrl.getKeyAccountInTargetNet(targetNet)
			RequisitionUtil.postRequest(
				it,
				api,
				errorCallBack,
				false
			) { result ->
				val namesJsonArray = JSONArray(JSONObject(result).safeGet("account_names"))
				var names = listOf<String>()
				(0 until namesJsonArray.length()).forEach {
					names += namesJsonArray.get(it).toString()
				}
				// 生成指定的包含链信息的结果类型
				val accountNames =
					names.map { EOSAccountInfo(it, Config.getEOSCurrentChain()) }
				GoldStoneAPI.context.runOnUiThread { hold(accountNames) }
			}
		}
	}

	fun getAccountEOSBalance(accountName: String, hold: (balance: Double) -> Unit) {
		getAccountBalanceBySymbol(accountName, CoinSymbol.eos, EOSCodeName.EOSIOToken, hold)
	}

	fun getChainInfo(
		errorCallBack: (Throwable) -> Unit,
		@WorkerThread hold: (chainInfo: EOSChainInfo) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<String>(
			EOSUrl.getInfo(),
			"",
			true,
			errorCallBack
		) {
			isNotEmpty() isTrue {
				hold(EOSChainInfo(JSONObject(first())))
			}
		}
	}

	fun pushTransaction(
		signatures: List<String>,
		packedTrxCode: String,
		errorCallBack: (Throwable) -> Unit,
		hold: (EOSResponse) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareObjectContent(
				Pair("signatures", signatures.toJsonArray()),
				Pair("packed_trx", packedTrxCode),
				Pair("compression", "none"),
				Pair("packed_context_free_data", "00")
			)
		).let { it ->
			RequisitionUtil.postRequest(
				it,
				EOSUrl.pushTransaction(),
				errorCallBack,
				false
			) {
				val response = JSONObject(it)
				if (it.contains("processed")) {
					val result = JSONObject(response.safeGet("processed"))
					val transactionID = response.safeGet("transaction_id")
					val receipt = JSONObject(result.safeGet("receipt"))
					hold(EOSResponse(transactionID, receipt))
				} else errorCallBack(Exception("Empty Result Response"))
			}
		}
	}

	fun getTransactionHeaderFromChain(
		expirationType: ExpirationType,
		errorCallBack: (Throwable) -> Unit,
		@WorkerThread hold: (header: TransactionHeader) -> Unit
	) {
		getChainInfo(errorCallBack) { chainInfo ->
			hold(TransactionHeader(chainInfo, expirationType))
		}
	}

	// `EOS` 对 `token` 做任何操作的时候 需要在操作其 `Code Name`
	fun getAccountBalanceBySymbol(
		accountName: String,
		symbol: String,
		tokenCodeName: EOSCodeName = EOSCodeName.EOSIOToken,
		hold: (balance: Double) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareObjectContent(
				Pair("code", tokenCodeName.value),
				Pair("account", accountName),
				Pair("symbol", symbol)
			)
		).let { it ->
			RequisitionUtil.postRequest(
				it,
				EOSUrl.getAccountEOSBalance(),
				{
					LogUtil.error("getAccountEOSBalance", it)
				},
				false
			) {
				val balances = JSONArray(it)
				val balance = if (balances.length() == 0) "" else balances.get(0).toString().substringBefore(" ")
				hold(balance.toDoubleOrNull().orZero())
			}
		}
	}

	fun getTransactionsLastIndex(
		accountName: String,
		errorCallBack: (Throwable) -> Unit,
		hold: (count: Int?) -> Unit
	) {
		// 传 `pos -1` 是倒序拉取最近一条, `offset` 是拉取倒序的第一条, 通过这个方法
		// 拉取下来最近一条获取到 `dataIndex` 即这个 `account` 的账单总数, 并计算映射出
		// 实际的翻页参数
		getAccountTransactionHistory(
			accountName,
			-1,
			-1,
			errorCallBack
		) {
			hold(it.firstOrNull()?.dataIndex)
		}
	}

	/** 拉取足页的最近的一页数据 */
	fun getPageInfo(localDataMaxIndex: Int, totalCount: Int): PageInfo {
		// 如果需要获取的数据个数为 `0` 那么直接返回 `0`
		val notInLocalDataCount = totalCount - localDataMaxIndex
		if (notInLocalDataCount == 0) return PageInfo(0, 0, 0)
		// totalCount 的值是最新一条数据的值, from 的值通过这个倒退出来
		val from =
			when {
				notInLocalDataCount > DataValue.pageCount -> totalCount - DataValue.pageCount
				localDataMaxIndex > 0 -> notInLocalDataCount
				else -> localDataMaxIndex
			}
		return PageInfo(from, totalCount, localDataMaxIndex)
	}

	fun getAccountTransactionHistory(
		accountName: String,
		from: Int,
		to: Int,
		errorCallBack: (Throwable) -> Unit,
		hold: (data: List<EOSTransactionTable>) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareObjectContent(
				Pair("account_name", accountName),
				Pair("pos", from),
				Pair("offset", to)
			)
		).let { it ->
			RequisitionUtil.postRequest<String>(
				it,
				"actions",
				EOSUrl.getTransactionHistory(),
				true,
				errorCallBack,
				false
			) { transactions ->
				JSONArray(transactions.first()).toList().map {
					EOSTransactionTable(JSONObject(it), Config.getCurrentEOSName())
				}.let(hold)
			}
		}
	}

	fun getBlockNumberByTxID(
		txID: String,
		errorCallBack: (Throwable) -> kotlin.Unit,
		@WorkerThread hold: (blockNumber: Int?) -> Unit
	) {
		getTransactionJSONObjectByTxID(txID, errorCallBack) {
			hold(it.safeGet("block_num").toIntOrNull())
		}
	}

	fun getCPUAndNETUsageByTxID(
		txID: String,
		errorCallBack: (Throwable) -> kotlin.Unit,
		@WorkerThread hold: (cpuUsage: BigInteger, netUsage: BigInteger, status: String) -> Unit
	) {
		getTransactionJSONObjectByTxID(txID, errorCallBack) { transaction ->
			val receipt = transaction.getTargetObject("trx", "receipt")
			hold(
				receipt.getTargetChild("cpu_usage_us").toBigIntegerOrZero(),
				receipt.getTargetChild("net_usage_words").toBigIntegerOrZero(),
				receipt.getTargetChild("status")
			)
		}
	}

	private fun getTransactionJSONObjectByTxID(
		txID: String,
		errorCallBack: (Throwable) -> Unit,
		@WorkerThread hold: (JSONObject) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareObjectContent(
				Pair("id", txID)
			)
		).let { it ->
			RequisitionUtil.postRequest(
				it,
				EOSUrl.getTransaction(),
				errorCallBack,
				false
			) {
				hold(JSONObject(it))
			}
		}
	}
}