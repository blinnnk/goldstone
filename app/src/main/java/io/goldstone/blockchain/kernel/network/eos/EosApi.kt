package io.goldstone.blockchain.kernel.network.eos

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.common.utils.toList
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.common.value.PageInfo
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.eos.header.TransactionHeader
import io.goldstone.blockchain.crypto.eos.transaction.ExpirationType
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.network.*
import io.goldstone.blockchain.kernel.network.eos.commonmodel.EOSChainInfo
import io.goldstone.blockchain.kernel.network.eos.commonmodel.EOSRAMMarket
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.DelegateBandWidthInfo
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.RefundRequestInfo
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.TotalResources
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.EOSAccountInfo
import okhttp3.RequestBody
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigInteger

object EOSAPI {

	fun getAccountInfo(
		account: EOSAccount,
		targetNet: String = "",
		@WorkerThread hold: (accountInfo: EOSAccountTable?, error: GoldStoneError) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareObjectContent(Pair("account_name", account.accountName))
		).let { requestBody ->
			val api =
				if (targetNet.isEmpty()) EOSUrl.getAccountInfo()
				else EOSUrl.getAccountInfoInTargetNet(targetNet)
			RequisitionUtil.postRequest(
				requestBody,
				api,
				{ hold(null, it) },
				false
			) { result ->
				// 测试网络挂了的时候, 换一个网络请求接口. 目前值处理了测试网络的情况
				// 这个库还承载着本地查询是否是激活的账号的用户所以会额外存储公钥地址
				if (result.isEmpty()) {
					hold(null, AccountError.UnavailableAccountName)
				} else {
					hold(EOSAccountTable(JSONObject(result), SharedAddress.getCurrentEOS(), SharedChain.getEOSCurrent()), RequestError.None)
				}
			}
		}
	}

	fun getAvailableRamBytes(
		accountName: EOSAccount,
		@WorkerThread hold: (ramAvailable: BigInteger?, error: GoldStoneError) -> Unit
	) {
		getAccountInfo(accountName) { account, error ->
			if (!account.isNull() && error.isNone()) {
				val availableRAM = account!!.ramQuota - account.ramUsed
				hold(availableRAM, GoldStoneError.None)
			} else hold(null, error)
		}
	}

	fun getAccountNameByPublicKey(
		publicKey: String,
		errorCallBack: (RequestError) -> Unit,
		targetNet: String = "",
		@UiThread hold: (accountNames: List<EOSAccountInfo>) -> Unit
	) {
		val api =
			if (targetNet.isEmpty()) EOSUrl.getKeyAccount()
			else EOSUrl.getKeyAccountInTargetNet(targetNet)
		RequisitionUtil.post(
			ParameterUtil.prepareObjectContent(Pair("public_key", publicKey)),
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
				names.map { EOSAccountInfo(it, SharedChain.getEOSCurrent().id, SharedAddress.getCurrentEOS()) }
			GoldStoneAPI.context.runOnUiThread { hold(accountNames) }
		}
	}

	fun getAccountEOSBalance(
		account: EOSAccount,
		hold: (balance: Double?, error: RequestError) -> Unit
	) {
		getAccountBalanceBySymbol(
			account,
			CoinSymbol.EOS,
			EOSCodeName.EOSIOToken.value,
			hold
		)
	}

	fun getChainInfo(
		errorCallBack: (GoldStoneError) -> Unit,
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
		errorCallBack: (GoldStoneError) -> Unit,
		hold: (EOSResponse) -> Unit
	) {
		RequisitionUtil.post(
			ParameterUtil.prepareObjectContent(
				Pair("signatures", signatures.toJsonArray()),
				Pair("packed_trx", packedTrxCode),
				Pair("compression", "none"),
				Pair("packed_context_free_data", "00")
			),
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
			} else GoldStoneAPI.context.runOnUiThread {
				errorCallBack(RequestError.ResolveDataError(GoldStoneError(it)))
			}
		}
	}

	fun getTransactionHeaderFromChain(
		expirationType: ExpirationType,
		errorCallBack: (GoldStoneError) -> Unit,
		@WorkerThread hold: (header: TransactionHeader) -> Unit
	) {
		getChainInfo(errorCallBack) { chainInfo ->
			hold(TransactionHeader(chainInfo, expirationType))
		}
	}

	// `EOS` 对 `token` 做任何操作的时候 需要在操作其 `Code Name`
	fun getAccountBalanceBySymbol(
		account: EOSAccount,
		symbol: CoinSymbol,
		tokenCodeName: String,
		@UiThread hold: (balance: Double?, error: RequestError) -> Unit
	) {
		RequisitionUtil.post(
			ParameterUtil.prepareObjectContent(
				Pair("code", tokenCodeName),
				Pair("account", account.accountName),
				Pair("symbol", symbol.symbol)
			),
			EOSUrl.getAccountEOSBalance(),
			{ hold(null, it) },
			false
		) {
			val balances = JSONArray(it)
			val balance = if (balances.length() == 0) "" else balances.get(0).toString().substringBefore(" ")
			GoldStoneAPI.context.runOnUiThread {
				hold(balance.toDoubleOrNull().orZero(), RequestError.None)
			}
		}
	}

	// `EOS` 对 `token` 做任何操作的时候 需要在操作其 `Code Name`
	fun getAccountResource(
		account: EOSAccount,
		tokenCodeName: EOSCodeName = EOSCodeName.EOSIO,
		errorCallBack: (RequestError) -> Unit,
		@WorkerThread hold: (resource: TotalResources?) -> Unit
	) {
		RequisitionUtil.postSingle<TotalResources>(
			ParameterUtil.prepareObjectContent(
				Pair("scope", account.accountName),
				Pair("code", tokenCodeName.value),
				Pair("table", "userres"),
				Pair("json", true)
			),
			EOSUrl.getTableRows(),
			"rows",
			errorCallBack,
			false,
			hold
		)
	}

	fun getRecycledBandWidthList(
		accountName: String,
		tokenCodeName: EOSCodeName = EOSCodeName.EOSIO,
		errorCallback: (GoldStoneError) -> Unit,
		@WorkerThread hold: (data: List<RefundRequestInfo>) -> Unit
	) {
		RequisitionUtil.post(
			ParameterUtil.prepareObjectContent(
				Pair("scope", accountName),
				Pair("code", tokenCodeName.value),
				Pair("table", "refunds"),
				Pair("json", true)
			),
			EOSUrl.getTableRows(),
			"rows",
			errorCallback,
			false,
			hold
		)
	}

	fun getDelegateBandWidthList(
		accountName: String,
		tokenCodeName: EOSCodeName = EOSCodeName.EOSIO,
		errorCallback: (GoldStoneError) -> Unit,
		@WorkerThread hold: (delegateBandWidths: List<DelegateBandWidthInfo>) -> Unit
	) {
		RequisitionUtil.post(
			ParameterUtil.prepareObjectContent(
				Pair("scope", accountName),
				Pair("code", tokenCodeName.value),
				Pair("table", "delband"),
				Pair("json", true)
			),
			EOSUrl.getTableRows(),
			"rows",
			errorCallback,
			false,
			hold
		)
	}

	@JvmStatic
	fun getEOSTransactions(
		chainid: ChainID,
		account: EOSAccount,
		pageSize: Int,
		starID: Long,
		endID: Long,
		codeName: String,
		symbol: String,
		@WorkerThread hold: (tokens: List<EOSTransactionTable>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			APIPath.getEOSTransactions(
				APIPath.currentUrl,
				chainid.id,
				account.accountName,
				pageSize,
				starID,
				endID,
				codeName,
				symbol
			),
			"action_list",
			true,
			{ hold(null, it) },
			isEncrypt = true
		) {
			val data = firstOrNull()
			if (!data.isNullOrEmpty()) hold(
				JSONArray(data!!).toList().map {
					EOSTransactionTable(it, SharedAddress.getCurrentEOSAccount().accountName)
				},
				RequestError.None
			) else hold(null, RequestError.NullResponse("Empty or Null Result"))
		}
	}

	@JvmStatic
	fun getTransactionCount(
		chainid: ChainID,
		account: EOSAccount,
		codeName: String,
		symbol: String,
		@WorkerThread hold: (count: Int?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			APIPath.getEOSTransactions(
				APIPath.currentUrl,
				chainid.id,
				account.accountName,
				0,
				-1,
				-1,
				codeName,
				symbol
			),
			"total_size",
			true,
			{ hold(null, it) },
			isEncrypt = true
		) {
			val data = firstOrNull()
			if (!data.isNullOrEmpty()) hold(data?.toIntOrNull(), RequestError.None)
			else hold(null, RequestError.NullResponse("Empty or Null Result"))
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

	fun getBandWidthByTxID(
		txID: String,
		errorCallBack: (Throwable) -> kotlin.Unit,
		@WorkerThread hold: (cpuUsage: BigInteger, netUsage: BigInteger) -> Unit
	) {
		getTransactionJSONObjectByTxID(txID, errorCallBack) { transaction ->
			val receipt = transaction.getTargetObject("trx", "receipt")
			hold(
				receipt.getTargetChild("cpu_usage_us").toBigIntegerOrZero(),
				receipt.getTargetChild("net_usage_words").toBigIntegerOrZero()
			)
		}
	}

	private fun getTransactionJSONObjectByTxID(
		txID: String,
		errorCallback: (GoldStoneError) -> Unit,
		@WorkerThread hold: (JSONObject) -> Unit
	) {
		RequisitionUtil.post(
			ParameterUtil.prepareObjectContent(Pair("id", txID)),
			EOSUrl.getTransaction(),
			errorCallback,
			false
		) { jsonString ->
			hold(JSONObject(jsonString))
		}
	}

	fun getRAMMarket(
		isMainThread: Boolean = false,
		hold: (data: EOSRAMMarket?, error: RequestError) -> Unit) {
		RequisitionUtil.postString(
			ParameterUtil.prepareObjectContent(
				Pair("scope", "eosio"),
				Pair("code", "eosio"),
				Pair("table", "rammarket"),
				Pair("json", "true")
			),
			EOSUrl.getTableRows(),
			"rows",
			{ hold(null, it) },
			false
		) {
			val data = JSONObject(JSONArray(it).get(0).toString())
			if (isMainThread) GoldStoneAPI.context.runOnUiThread {
				hold(EOSRAMMarket(data), RequestError.None)
			} else hold(EOSRAMMarket(data), RequestError.None)
		}
	}

}