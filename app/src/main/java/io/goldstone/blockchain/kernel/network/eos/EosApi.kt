package io.goldstone.blockchain.kernel.network.eos

import android.support.annotation.WorkerThread
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.common.value.PageInfo
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.kernel.commonmodel.EOSTransactionTable
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.kernel.network.ParameterUtil
import io.goldstone.blockchain.kernel.network.RequisitionUtil
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject

object EosApi {
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
				EosUrl.getKeyAccount,
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
				EosUrl.getAccountEOSBalance,
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
				EosUrl.getTransactionHistory,
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