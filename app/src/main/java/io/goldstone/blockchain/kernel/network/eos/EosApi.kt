package io.goldstone.blockchain.kernel.network.eos

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.common.value.PageInfo
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.eos.accountregister.EOSrResponse
import io.goldstone.blockchain.crypto.eos.header.TransactionHeader
import io.goldstone.blockchain.crypto.eos.transaction.ExpirationType
import io.goldstone.blockchain.kernel.commonmodel.EOSTransactionTable
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.kernel.network.ParameterUtil
import io.goldstone.blockchain.kernel.network.RequisitionUtil
import io.goldstone.blockchain.kernel.network.eos.commonmodel.EOSChainInfo
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject

object EOSAPI {
	fun getAccountNameByPublicKey(
		publicKey: String,
		@WorkerThread hold: (accountNames: List<String>) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareObjectContent(Pair("public_key", publicKey))
		).let { it ->
			RequisitionUtil.postRequest(
				it,
				EOSUrl.getKeyAccount,
				{
					LogUtil.error("getAccountNameByPublicKey", it)
				},
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

	fun getAccountEOSBalance(accountName: String, hold: (balance: String) -> Unit) {
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

	fun getAccountBalanceBySymbol(accountName: String, symbol: String, hold: (balance: String) -> Unit) {
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
				hold(balance)
			}
		}
	}

	fun getTransactionsLastIndex(accountName: String, hold: (count: Int) -> Unit) {
		// 传 `pos -1` 是倒序拉取最近一条, `offset` 是拉取倒序的第一条, 通过这个方法
		// 拉取下来最近一条获取到 `dataIndex` 即这个 `account` 的账单总数, 并计算映射出
		// 实际的翻页参数
		getAccountTransactionHistory(accountName, -1, -1) {
			hold(it.first().dataIndex)
		}
	}

	/** 拉取足页的最近的一页数据 */
	fun getPageInfo(localDataMaxIndex: Int, transactionsLastIndex: Int): PageInfo {
		val notInLocalDataCount = transactionsLastIndex - localDataMaxIndex
		val from =
			if (notInLocalDataCount > DataValue.pageCount)
				transactionsLastIndex - DataValue.pageCount
			else localDataMaxIndex + 1
		return PageInfo(from, transactionsLastIndex, localDataMaxIndex)
	}

	fun getAccountTransactionHistory(
		accountName: String,
		from: Int,
		to: Int,
		hold: (data: List<EOSTransactionTable>) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareObjectContent(
				Pair("account", accountName),
				Pair("pos", from),
				Pair("offset", to)
			)
		).let { it ->
			RequisitionUtil.postRequest(
				it,
				EOSUrl.getTransactionHistory,
				{
					LogUtil.error("getAccountTransactionHistory", it)
				},
				false
			) {
				// TODO 解析为 Model
				hold(listOf(EOSTransactionTable()))
			}
		}
	}
}