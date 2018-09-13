package io.goldstone.blockchain.kernel.network.eos

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.orZero
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toLongOrZero
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.common.value.PageInfo
import io.goldstone.blockchain.crypto.eos.accountregister.EOSrResponse
import io.goldstone.blockchain.crypto.eos.header.TransactionHeader
import io.goldstone.blockchain.crypto.eos.transaction.ExpirationType
import io.goldstone.blockchain.crypto.multichain.CryptoSymbol
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.kernel.network.ParameterUtil
import io.goldstone.blockchain.kernel.network.RequisitionUtil
import io.goldstone.blockchain.kernel.network.eos.commonmodel.EOSChainInfo
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject

object EOSAPI {

	fun getAccountInfoByName(
		accountName: String,
		errorCallBack: (Throwable) -> Unit,
		targetNet: String = "",
		@WorkerThread hold: (EOSAccountTable) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareObjectContent(Pair("account_name", accountName))
		).let { it ->
			val api =
				if (targetNet.isEmpty()) EOSUrl.getAccountInfo
				else EOSUrl.getAccountInfoInTargetNet(targetNet)
			RequisitionUtil.postRequest(
				it,
				api,
				errorCallBack,
				false
			) { result ->
				hold(EOSAccountTable(JSONObject(result)))
			}
		}
	}

	fun getAccountNameByPublicKey(
		publicKey: String,
		errorCallBack: (Throwable) -> Unit,
		targetNet: String = "",
		@WorkerThread hold: (accountNames: List<String>) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareObjectContent(Pair("public_key", publicKey))
		).let { it ->
			val api =
				if (targetNet.isEmpty()) EOSUrl.getKeyAccount
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
				hold(names)
			}
		}
	}

	fun getAccountEOSBalance(accountName: String, hold: (balance: Double) -> Unit) {
		getAccountBalanceBySymbol(accountName, CryptoSymbol.eos, hold)
	}

	private fun getChainInfo(@WorkerThread hold: (chainInfo: EOSChainInfo) -> Unit) {
		RequisitionUtil.requestUnCryptoData<String>(
			EOSUrl.getInfo,
			"",
			true,
			{
				LogUtil.error("getChainInfo", it)
			}
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
		@WorkerThread hold: (EOSrResponse) -> Unit
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
				EOSUrl.pushTransaction,
				errorCallBack,
				false
			) {
				if (it.contains("processed")) {
					val result = JSONObject(JSONObject(it).safeGet("processed"))
					val transactionID = result.safeGet("transaction_id")
					val receipt = JSONObject(result.safeGet("receipt"))
					hold(EOSrResponse(transactionID, receipt))
				} else errorCallBack(Exception("Empty Result Response"))
			}
		}
	}

	fun getTransactionHeaderFromChain(
		expirationType: ExpirationType,
		@WorkerThread hold: (header: TransactionHeader) -> Unit
	) {
		getChainInfo {
			hold(TransactionHeader(it, expirationType))
		}
	}

	fun getAccountBalanceBySymbol(
		accountName: String,
		symbol: String,
		hold: (balance: Double) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareObjectContent(
				Pair("code", "eosio.token"),
				Pair("account", accountName),
				Pair("symbol", symbol)
			)
		).let { it ->
			RequisitionUtil.postRequest(
				it,
				EOSUrl.getAccountEOSBalance,
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
				EOSUrl.getTransactionHistory,
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

	fun getCPUAndNETUsageByTxID(
		txID: String,
		errorCallBack: (Throwable) -> kotlin.Unit,
		@WorkerThread hold: (cpuUsage: Long, netUsage: Long, status: String) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareObjectContent(
				Pair("id", txID)
			)
		).let { it ->
			RequisitionUtil.postRequest(
				it,
				EOSUrl.getTransaction,
				errorCallBack,
				false
			) { transaction ->
				val receipt = JSONObject(transaction).getTargetObject("trx", "receipt")
				hold(
					receipt.getTargetChild("cpu_usage_us").toLongOrZero(),
					receipt.getTargetChild("net_usage_words").toLongOrZero(),
					receipt.getTargetChild("status")
				)
			}
		}
	}
}