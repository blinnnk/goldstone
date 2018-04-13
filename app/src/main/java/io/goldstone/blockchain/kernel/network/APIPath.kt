package io.goldstone.blockchain.kernel.network


/**
 * @date 31/03/2018 8:09 PM
 * @author KaySaith
 */

object APIPath {
  private const val url = "http://118.89.147.176:8001"
  const val ropstan = "http://118.89.147.176:8500"
  const val defaultTokenList = "$url/index/defaultCoinList"
}


object EtherScanApi {
  private const val apikey = "E7VD17QW5G3B545B546U9AMI4EH7FI6HF8"
  private const val header = "http://api-ropsten.etherscan.io"
  @JvmStatic val transactions: (address: String, startBlock: String) -> String = {  address, startBlock ->
    "$header/api?module=account&action=txlist&address=$address&startblock=$startBlock&endblock=99999999&sort=desc&apikey=$apikey"
  }
  @JvmStatic val transactionsByHash: (taxHash: String) -> String = {
    "$header/api?module=proxy&action=eth_getTransactionByHash&txhash=$it&apikey=$apikey"
  }
  @JvmStatic val singleTransactionHas: (hash: String) -> String = {
    "$header/api?module=account&action=txlistinternal&txhash=$it&apikey=$apikey"
  }
}