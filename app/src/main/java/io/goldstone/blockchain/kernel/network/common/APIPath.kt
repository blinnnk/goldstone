package io.goldstone.blockchain.kernel.network.common

import io.goldstone.blockchain.common.value.WebUrl

/**
 * @date 31/03/2018 8:09 PM
 * @author KaySaith
 */
object APIPath {

	/** GoldStone Basic Api Address */
	var currentUrl = WebUrl.normalServer

	fun updateServerUrl(newUrl: String) {
		currentUrl = newUrl
	}

	const val serverStatus = "https://gs.blinnnk.com/index/serverStatus"
	val getCurrencyRate: (header: String) -> String = { "$it/index/exchangeRate?currency=" }
	val registerDevice: (header: String) -> String = { "$it/account/registerDevice" }
	val updateAddresses: (header: String) -> String = { "$it/account/commitAddress" }
	val getNotification: (header: String) -> String = { "$it/account/unreadMessageList" }
	val terms: (header: String) -> String = { "$it/index/agreement?md5=" }
	val marketSearch: (header: String, pair: String, marketIds: String) -> String = { header, pair, marketIds ->
		"$header/account/searchPair?pair=$pair" +
			if (marketIds.isEmpty()) "" else "&market_ids=$marketIds"
	}
	val searchPairByExactKey: (header: String) -> String = {
		"$it/account/searchPairByExactKey"
	}
	val marketList: (header: String) -> String = { header ->
		"$header/index/marketList?md5="
	}
	val getConfigList: (header: String) -> String = { "$it/index/getConfigList" }
	val getCurrencyLineChartData: (header: String) -> String = { "$it/account/lineDataByDay" }
	val getPriceByAddress: (header: String) -> String = { "$it/index/priceByAddress" }
	val getCoinInfo: (header: String) -> String = { "$it/market/coinInfo?symbol=" }
	val getUnreadCount: (header: String) -> String = { "$it/account/checkUnreadMessage" }
	val getNewVersion: (header: String) -> String = { "$it/index/getNewVersion" }
	val getShareContent: (header: String) -> String = { "$it/index/getShareContent" }
	val unregeisterDevice: (header: String) -> String = { "$it/account/unregisterDevice" }
	val getIconURL: (header: String) -> String = { "$it/index/getTokenBySymbolAndAddress" }
	val getChainNodes: (header: String) -> String = { "$it/market/getChainNodes" }
	val getMD5Info: (header: String) -> String = { "$it/index/md5Info" }
	val getEOSTokenList: (header: String, chainID: String, account: String) -> String = { header, chainID, account ->
		"$header/eos/tokenList?chainid=$chainID&account=$account"
	}
	val getEOSTokenCountInfo: (
		header: String,
		chainID: String,
		account: String,
		code: String,
		symbol: String
	) -> String = { header, chainID, account, codeName, symbol ->
		"$header/eos/txCountInfo?chainid=$chainID&account=$account&code=$codeName&symbol=$symbol"
	}
	val getEOSTransactions: (
		header: String,
		chainID: String,
		account: String,
		pageSize: Int,
		startID: Long,
		endID: Long,
		codeName: String,
		symbol: String
	) -> String = { header, chainID, account, pageSize, startID, endID, codeName, symbol ->
		"$header/eos/actionList?chainid=$chainID&account=$account&size=$pageSize&start=$startID&end=$endID&code=$codeName&symbol=$symbol"
	}
	val defaultTokenList: (
		header: String
	) -> String = { header ->
		"$header/index/defaultCoinList?md5="
	}
	val getTokenInfo: (
		header: String,
		condition: String,
		chainIDs: String
	) -> String = { header, condition, chainIDs ->
		"$header/index/searchToken?symbolOrContract=$condition&chainids=$chainIDs"
	}
	val getETCTransactions: (
		header: String,
		chainID: String,
		address: String,
		startBlock: Int
	) -> String = { header, chainID, address, startBlock ->
		"$header/tx/pageList?chainid=$chainID&address=$address&start_block=$startBlock"
	}

	val getQuotationCurrencyCandleChart: (
		header: String,
		pair: String,
		period: String,
		size: Int
	) -> String = { header, pair, period, size ->
		"$header/chart/lineData?pair=$pair&period=$period&size=$size"
	}
	val getQuotationCurrencyInfo: (header: String, pair: String) -> String = { header, pair ->
		"$header/market/coinDetail?pair=$pair"
	}

	val getRecommendDAPPs: (header: String, page: Int, pageSize: Int) -> String = { header, pageIndex, pageSize ->
		"$header/dapp/getRecommendDapp?page=$pageIndex&size=$pageSize"
	}

	val getNewDAPPs: (header: String, page: Int, pageSize: Int) -> String = { header, pageIndex, pageSize ->
		"$header/dapp/getDapps?page=$pageIndex&size=$pageSize"
	}

	// 从服务器动态更新注入 `Scatter` 的 `JS Code`
	val getDAPPJSCode: (header: String) -> String = { header ->
		"$header/index/getJSCode"
	}

	val searchDAPP: (header: String, condition: String) -> String = { header, condition ->
		"$header/dapp/searchDapp?dapp=$condition"
	}
}
