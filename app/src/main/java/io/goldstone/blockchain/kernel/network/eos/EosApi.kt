package io.goldstone.blockchain.kernel.network.eos

import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.eos.header.TransactionHeader
import io.goldstone.blockchain.crypto.eos.transaction.EOSChain
import io.goldstone.blockchain.crypto.eos.transaction.ExpirationType
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.kernel.commontable.EOSTransactionTable
import io.goldstone.blockchain.kernel.network.ParameterUtil
import io.goldstone.blockchain.kernel.network.common.APIPath
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
import io.goldstone.blockchain.kernel.network.eos.commonmodel.EOSChainInfo
import io.goldstone.blockchain.kernel.network.eos.commonmodel.EOSRAMMarket
import io.goldstone.blockchain.kernel.network.eos.commonmodel.EOSTokenBalance
import io.goldstone.blockchain.kernel.network.eos.thirdparty.EOSPark
import io.goldstone.blockchain.kernel.network.eos.thirdparty.NewDexPair
import io.goldstone.blockchain.kernel.network.ethereum.ETHJsonRPC
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.DelegateBandWidthInfo
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.RefundRequestInfo
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.TotalResources
import io.goldstone.blockchain.module.common.tokendetail.tokeninfo.model.EOSTokenCountInfo
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.EOSAccountInfo
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigInteger

object EOSAPI {

	fun getTokenBalance(
		account: EOSAccount,
		@WorkerThread hold: (tokenBalance: List<EOSTokenBalance>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData(
			EOSPark.getAccountBalance(account),
			listOf("data", "symbol_list"),
			hold = hold
		)
	}

	fun getBlockNumberByTxIDFromEOSPark(
		txID: String,
		@WorkerThread hold: (blockNumber: Int?, error: GoldStoneError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<String>(
			EOSPark.getTransactionByTXID(txID),
			listOf("data"),
			true
		) { data, error ->
			if (data?.isEmpty() == false && error.isNone()) {
				hold(JSONObject(data.first()).safeGet("block_num").toIntOrNull(), error)
			} else hold(null, error)
		}
	}

	/**
	 * 本地转账临时插入的 Pending Data 需要填充, 服务器自定义的 ServerID 格式
	 * unique_id = block_num * 1000000 + tx_index * 1000 + action_index
	 */
	fun getTransactionServerID(
		blockNumber: Int,
		txID: String,
		fromAccount: EOSAccount,
		hold: (Long?) -> Unit
	) {
		getBlockByNumber(blockNumber) { jsonString, error ->
			if (jsonString.isNotNull() && error.isNone()) {
				val json = JSONObject(jsonString)
				val blockTransactions = JSONArray(json.safeGet("transactions")).toJSONObjectList()
				var txIndex: Int? = null
				blockTransactions.forEachIndexed { index, jsonObject ->
					try {
						// `block` 的 数据不确定有的时候会有不同结构体的内容, 这里用 `try catch` 跳过错误的情况
						if (jsonObject.getTargetChild("trx", "id").equals(txID, true)) txIndex = index
					} catch (error: Exception) {
					}
				}
				val actions = JSONArray(blockTransactions[txIndex!!].getTargetChild("trx", "transaction", "actions")).toJSONObjectList()
				var actionIndex: Int? = null
				actions.forEachIndexed { index, jsonObject ->
					if (
						jsonObject.safeGet("name").equals("transfer", true) &&
						jsonObject.getTargetChild("data", "from").equals(fromAccount.name, true)
					) {
						actionIndex = index
					}
				}
				if (txIndex.isNotNull() && actionIndex.isNotNull()) {
					val uniqueID = blockNumber * 1000000L + txIndex!! * 1000L + actionIndex!!
					hold(uniqueID)
				}
			}
		}
	}

	private fun getBlockByNumber(
		blockNumber: Int,
		@WorkerThread hold: (jsonString: String?, error: RequestError) -> Unit
	) {
		RequestBody.create(
			ETHJsonRPC.contentType,
			ParameterUtil.prepareObjectContent(Pair("block_num_or_id", blockNumber))
		).let { requestBody ->
			val api = EOSUrl.getBlock()
			RequisitionUtil.postRequest(
				requestBody,
				api,
				false,
				hold
			)
		}
	}

	fun getAccountInfo(
		account: EOSAccount,
		targetNet: String = "",
		@WorkerThread hold: (accountInfo: EOSAccountTable?, error: GoldStoneError) -> Unit
	) {
		RequestBody.create(
			ETHJsonRPC.contentType,
			ParameterUtil.prepareObjectContent(Pair("account_name", account.name))
		).let { requestBody ->
			val api =
				if (targetNet.isEmpty()) EOSUrl.getAccountInfo()
				else EOSUrl.getAccountInfoInTargetNet(targetNet)
			RequisitionUtil.postRequest(
				requestBody,
				api,
				false
			) { result, error ->
				// 测试网络挂了的时候, 换一个网络请求接口. 目前值处理了测试网络的情况
				// 这个库还承载着本地查询是否是激活的账号的用户所以会额外存储公钥地址
				if (result?.isNotEmpty() == true && error.isNone()) {
					hold(
						EOSAccountTable(
							JSONObject(result),
							SharedAddress.getCurrentEOS(),
							SharedChain.getEOSCurrent().chainID
						),
						error
					)
				} else hold(null, error)
			}
		}
	}

	// 服务 Javascript DAPP, 只查主网
	fun getStringAccountInfo(
		account: EOSAccount,
		@WorkerThread hold: (result: String?, error: GoldStoneError) -> Unit
	) {
		RequestBody.create(
			ETHJsonRPC.contentType,
			ParameterUtil.prepareObjectContent(Pair("account_name", account.name))
		).let { requestBody ->
			RequisitionUtil.postRequest(
				requestBody,
				EOSUrl.getAccountInfo(),
				false,
				hold
			)
		}
	}

	fun getAvailableRamBytes(
		accountName: EOSAccount,
		@WorkerThread hold: (ramAvailable: BigInteger?, error: GoldStoneError) -> Unit
	) {
		getAccountInfo(accountName) { account, error ->
			if (account.isNotNull() && error.isNone()) {
				val availableRAM = account.ramQuota - account.ramUsed
				hold(availableRAM, GoldStoneError.None)
			} else hold(null, error)
		}
	}

	fun getAccountNameByPublicKey(
		publicKey: String,
		targetNet: String = "",
		@WorkerThread hold: (accountNames: List<EOSAccountInfo>?, error: RequestError) -> Unit
	) {
		val api =
			if (targetNet.isEmpty()) EOSUrl.getKeyAccount()
			else EOSUrl.getKeyAccountInTargetNet(targetNet)
		RequisitionUtil.post(
			ParameterUtil.prepareObjectContent(Pair("public_key", publicKey)),
			api,
			false
		) { result, error ->
			if (result.isNullOrEmpty() || error.hasError()) {
				hold(null, error)
				return@post
			}
			val namesJsonArray = JSONArray(JSONObject(result).safeGet("account_names"))
			var names = listOf<String>()
			(0 until namesJsonArray.length()).forEach {
				names += namesJsonArray.get(it).toString()
			}
			// 生成指定的包含链信息的结果类型
			val accountNames =
				names.map {
					EOSAccountInfo(it, SharedChain.getEOSCurrent().chainID.id, publicKey)
				}
			hold(accountNames, RequestError.None)
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
		@WorkerThread hold: (chainInfo: EOSChainInfo?, error: GoldStoneError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<String>(
			EOSUrl.getInfo(),
			listOf(),
			true
		) { result, error ->
			hold(EOSChainInfo(JSONObject(result?.firstOrNull())), error)
		}
	}

	fun pushTransaction(
		signatures: List<String>,
		packedTrxCode: String,
		@WorkerThread hold: (response: EOSResponse?, error: GoldStoneError) -> Unit
	) {
		RequisitionUtil.post(
			ParameterUtil.prepareObjectContent(
				Pair("signatures", signatures.toJsonArray()),
				Pair("packed_trx", packedTrxCode),
				Pair("compression", "none"),
				Pair("packed_context_free_data", "00")
			),
			EOSUrl.pushTransaction(),
			false
		) { result, error ->
			if (result.isNullOrEmpty() || error.hasError()) {
				hold(null, error)
			} else {
				val response = JSONObject(result)
				when {
					result.contains("processed") -> {
						val data = JSONObject(response.safeGet("processed"))
						val transactionID = response.safeGet("transaction_id")
						val receipt = JSONObject(data.safeGet("receipt"))
						hold(EOSResponse(transactionID, receipt, result), GoldStoneError.None)
					}
					else -> hold(null, RequestError.ResolveDataError(GoldStoneError(result)))
				}
			}
		}
	}

	fun getTransactionHeader(
		expirationType: ExpirationType,
		@WorkerThread hold: (header: TransactionHeader?, error: GoldStoneError) -> Unit
	) {
		getChainInfo { chainInfo, error ->
			if (chainInfo.isNotNull() && error.isNone()) {
				hold(TransactionHeader(chainInfo, expirationType), error)
			} else hold(null, error)
		}
	}

	// `EOS` 对 `token` 做任何操作的时候 需要在操作其 `Code Name`
	fun getAccountBalanceBySymbol(
		account: EOSAccount,
		symbol: CoinSymbol,
		tokenCodeName: String,
		@WorkerThread hold: (balance: Double?, error: RequestError) -> Unit
	) {
		RequisitionUtil.post(
			ParameterUtil.prepareObjectContent(
				Pair("code", tokenCodeName),
				Pair("account", account.name),
				Pair("symbol", symbol.symbol)
			),
			EOSUrl.getAccountEOSBalance(),
			false
		) { result, error ->
			if (!result.isNullOrEmpty() && error.isNone()) {
				val balances = JSONArray(result)
				val balance = if (balances.length() == 0) "" else balances.get(0).toString().substringBefore(" ")
				hold(balance.toDoubleOrNull().orZero(), RequestError.None)
			} else {
				hold(null, error)
			}
		}
	}

	// `EOS` 对 `token` 做任何操作的时候 需要在操作其 `Code Name`
	fun getAccountResource(
		account: EOSAccount,
		tokenCodeName: EOSCodeName = EOSCodeName.EOSIO,
		@WorkerThread hold: (resource: TotalResources?, error: RequestError) -> Unit
	) {
		RequisitionUtil.postSingle<TotalResources>(
			ParameterUtil.prepareObjectContent(
				Pair("scope", account.name),
				Pair("code", tokenCodeName.value),
				Pair("table", "userres"),
				Pair("json", true)
			),
			EOSUrl.getTableRows(),
			"rows",
			false,
			hold
		)
	}

	fun getRecycledBandWidthList(
		account: EOSAccount,
		tokenCodeName: EOSCodeName = EOSCodeName.EOSIO,
		@WorkerThread hold: (data: List<RefundRequestInfo>?, error: GoldStoneError) -> Unit
	) {
		if (!account.isValid(false)) hold(null, AccountError.InvalidAccountName)
		else RequisitionUtil.post(
			ParameterUtil.prepareObjectContent(
				Pair("scope", account.name),
				Pair("code", tokenCodeName.value),
				Pair("table", "refunds"),
				Pair("json", true)
			),
			EOSUrl.getTableRows(),
			"rows",
			false,
			hold
		)
	}

	fun getDelegateBandWidthList(
		account: EOSAccount,
		tokenCodeName: EOSCodeName = EOSCodeName.EOSIO,
		@WorkerThread hold: (delegateBandWidths: List<DelegateBandWidthInfo>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.post(
			ParameterUtil.prepareObjectContent(
				Pair("scope", account.name),
				Pair("code", tokenCodeName.value),
				Pair("table", "delband"),
				Pair("json", true)
			),
			EOSUrl.getTableRows(),
			"rows",
			false,
			hold
		)
	}

	fun getTableRows(
		scope: String,
		code: String,
		tableName: String,
		option: Pair<String, String>?,
		limit: Pair<String, Int>?,
		indexPosition: Pair<String, Int>?,
		keyType: Pair<String, String>?,
		@WorkerThread hold: (result: String?, error: RequestError) -> Unit
	) {
		val params = arrayListOf(
			Pair("scope", scope),
			Pair("code", code),
			Pair("table", tableName),
			Pair("json", true)
		)
		if (option.isNotNull()) params.add(option)
		if (limit.isNotNull()) params.add(limit)
		if (indexPosition.isNotNull()) params.add(indexPosition)
		if (keyType.isNotNull()) params.add(keyType)

		RequisitionUtil.postString(
			ParameterUtil.prepareObjectContent(params),
			EOSUrl.getTableRows(),
			"",
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
		contract: TokenContract,
		@WorkerThread hold: (tokens: List<EOSTransactionTable>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			APIPath.getEOSTransactions(
				APIPath.currentUrl,
				chainid.id,
				account.name,
				pageSize,
				starID,
				endID,
				contract.contract.orEmpty(),
				contract.symbol
			),
			"action_list",
			true,
			isEncrypt = true
		) { result, error ->
			if (result.isNull() || error.hasError()) {
				hold(null, error)
				return@requestData
			}
			val data = result.firstOrNull()
			if (!data.isNullOrEmpty()) hold(
				JSONArray(data).toJSONObjectList().map {
					EOSTransactionTable(it, SharedAddress.getCurrentEOSAccount().name)
				},
				RequestError.None
			) else hold(null, RequestError.NullResponse("Empty or Null Result"))
		}
	}

	fun getEOSCountInfo(
		chainid: ChainID,
		account: EOSAccount,
		codeName: String,
		symbol: CoinSymbol,
		@WorkerThread hold: (info: EOSTokenCountInfo?, error: RequestError) -> Unit
	) {
		getEOSTokenCountInfo(chainid, account, codeName, symbol) { data, error ->
			if (data?.isNotEmpty() == true && error.isNone()) {
				getTransactionCount(chainid, account, codeName, symbol) { count, totalCountError ->
					if (!count.isNull() && totalCountError.isNone()) {
						hold(
							EOSTokenCountInfo(
								JSONObject(data).safeGet("total_send").toIntOrNull().orZero(),
								JSONObject(data).safeGet("total_receive").toIntOrNull().orZero(),
								count.orZero()
							),
							RequestError.None
						)
					} else hold(null, totalCountError)
				}
			} else hold(null, error)
		}
	}

	@JvmStatic
	fun getEOSTokenList(
		chainid: ChainID,
		account: EOSAccount,
		@WorkerThread hold: (info: List<TokenContract>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData(
			APIPath.getEOSTokenList(
				APIPath.currentUrl,
				chainid.id,
				account.name
			),
			"token_list",
			false,
			isEncrypt = true,
			hold = hold
		)
	}

	@JvmStatic
	fun getEOSTokenCountInfo(
		chainid: ChainID,
		account: EOSAccount,
		codeName: String,
		symbol: CoinSymbol,
		@WorkerThread hold: (info: String?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			APIPath.getEOSTokenCountInfo(
				APIPath.currentUrl,
				chainid.id,
				account.name,
				codeName,
				symbol.symbol
			),
			"",
			true,
			isEncrypt = true
		) { result, error ->
			if (result.isNotNull() && error.isNone()) {
				val data = result.firstOrNull()
				if (data.isNullOrEmpty()) hold(null, RequestError.NullResponse("Empty Result"))
				else hold(data, RequestError.None)
			} else hold(null, error)
		}
	}

	@JvmStatic
	fun getTransactionCount(
		chainid: ChainID,
		account: EOSAccount,
		codeName: String,
		symbol: CoinSymbol,
		@WorkerThread hold: (count: Int?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			APIPath.getEOSTransactions(
				APIPath.currentUrl,
				chainid.id,
				account.name,
				0,
				-1,
				-1,
				codeName,
				symbol.symbol
			),
			"total_size",
			true,
			isEncrypt = true
		) { result, error ->
			if (result.isNotNull() && error.isNone()) {
				val data = result.firstOrNull()
				if (!data.isNullOrEmpty()) hold(data.toIntOrNull(), RequestError.None)
				else hold(null, RequestError.NullResponse("Empty or Null Result"))
			} else hold(null, error)
		}
	}

	fun getBlockNumberByTxID(
		txID: String,
		@WorkerThread hold: (blockNumber: Int?, error: GoldStoneError) -> Unit
	) {
		getTransactionJSONObjectByTxID(txID) { data, error ->
			if (data.isNotNull() && error.isNone()) {
				hold(data.safeGet("block_num").toIntOrNull(), error)
			} else hold(null, error)
		}
	}

	fun getBandWidthByTxID(
		txID: String,
		@WorkerThread hold: (cpuUsage: BigInteger?, netUsage: BigInteger?, error: RequestError) -> Unit
	) {
		getTransactionJSONObjectByTxID(txID) { transaction, error ->
			if (transaction.isNotNull() && error.isNone()) {
				val receipt = transaction.getTargetObject("trx", "receipt")
				hold(
					receipt.getTargetChild("cpu_usage_us").toBigIntegerOrZero(),
					receipt.getTargetChild("net_usage_words").toBigIntegerOrZero(),
					error
				)
			} else hold(null, null, error)
		}
	}

	private fun getTransactionJSONObjectByTxID(
		txID: String,
		@WorkerThread hold: (data: JSONObject?, error: RequestError) -> Unit
	) {
		RequisitionUtil.post(
			ParameterUtil.prepareObjectContent(Pair("id", txID)),
			EOSUrl.getTransaction(),
			false
		) { jsonString, error ->
			if (jsonString.isNotNull() && error.isNone()) {
				hold(JSONObject(jsonString), error)
			} else hold(null, error)
		}
	}

	fun getRAMMarket(
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
			false
		) { result, error ->
			if (result?.isNotEmpty() == true && error.isNone()) {
				val data = JSONObject(JSONArray(result).get(0).toString())
				hold(EOSRAMMarket(data), RequestError.None)
			} else hold(null, error)
		}
	}

	// 从第三方 EOS 交易所拉取交易对
	@JvmStatic
	fun getPairsFromNewDex(@WorkerThread hold: (tokens: List<NewDexPair>?, error: RequestError) -> Unit) {
		RequisitionUtil.requestData(
			EOSUrl.getPairsFromNewDex(),
			"data",
			false,
			isEncrypt = false,
			hold = hold
		)
	}

	@JvmStatic
	fun getPriceByPair(
		pair: String,
		@WorkerThread hold: (price: Double?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			EOSUrl.getTokenPriceInEOS(pair),
			"data",
			true,
			isEncrypt = false
		) { result, error ->
			if (result.isNotNull() && error.isNone()) {
				val data = result.firstOrNull()
				if (data.isNotNull())
					hold(JSONObject(data).safeGet("price").toDoubleOrNull().orZero(), RequestError.None)
				else hold(null, RequestError.RPCResult("empty result"))
			} else hold(null, error)
		}
	}

	fun updateTokenPriceFromNewDex(contract: TokenContract, pairs: List<NewDexPair>) {
		val pair = pairs.find {
			it.symbol.equals(contract.symbol, true) &&
				it.contract.equals(contract.contract, true)
		}?.pair ?: return
		EOSAPI.getPriceByPair(pair) { priceInEOS, pairError ->
			if (priceInEOS.isNotNull() && pairError.isNone()) {
				val eosPrice = DefaultTokenTable.dao.getTokenPrice(
					TokenContract.eosContract,
					TokenContract.EOS.symbol,
					EOSChain.Main.id
				)
				val priceInUSD = priceInEOS * eosPrice.orZero()
				if (priceInUSD > 0.0) {
					DefaultTokenTable.dao.updateTokenPrice(
						priceInUSD,
						contract.contract.orEmpty(),
						contract.symbol,
						EOSChain.Main.id
					)
				}
			}
		}
	}

}