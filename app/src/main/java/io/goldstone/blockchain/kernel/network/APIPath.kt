package io.goldstone.blockchain.kernel.network

import io.goldstone.blockchain.crypto.SolidityCode
import io.goldstone.blockchain.crypto.toAddressCode

/**
 * @date 31/03/2018 8:09 PM
 * @author KaySaith
 */

object APIPath {
  // 常规业务的 `API` 地址
  private const val url = "http://118.89.147.176:8001"
  const val defaultTokenList = "$url/index/defaultCoinList"
  // ROPSTAN 节点请求地址
  const val ropstan = "http://118.89.147.176:8500"
}


object EtherScanApi {
  private const val apikey = "E8AW54SAFUGK6KPCDTHZSBT6NF4KZHV25E"
  private const val header = "http://api-ropsten.etherscan.io"
  private const val logHeader = "http://ropsten.etherscan.io"

  @JvmStatic val transactions: (address: String, startBlock: String) -> String = {  address, startBlock ->
    "$header/api?module=account&action=txlist&address=$address&startblock=$startBlock&endblock=99999999&sort=desc&apikey=$apikey"
  }
  @JvmStatic val transactionsByHash: (taxHash: String) -> String = {
    "$header/api?module=proxy&action=eth_getTransactionByHash&txhash=$it&apikey=$apikey"
  }
  @JvmStatic val singleTransactionHas: (hash: String) -> String = {
    "$header/api?module=proxy&action=eth_getTransactionByHash&txhash=$it&apikey=$apikey"
  }
  @JvmStatic val getTokenIncomingTransaction: (address: String, startBlock: String) -> String = { address, startBlock ->
    "$logHeader/api?module=logs&action=getLogs&fromBlock=$startBlock&toBlock=latest&topic0=${SolidityCode.logTransferFilter}&topic2=${address.toAddressCode()}"
  }
  @JvmStatic val getTokenDefrayTransaction: (address: String, startBlock: String) -> String = { address, startBlock ->
    "$logHeader/api?module=logs&action=getLogs&fromBlock=$startBlock&toBlock=latest&topic0=${SolidityCode.logTransferFilter}&topic1=${address.toAddressCode()}"
  }
  @JvmStatic val getAllTokenTransaction: (address: String, startBlock: String) -> String = { address, startBlock ->
    "$logHeader/api?module=logs&action=getLogs&fromBlock=$startBlock&toBlock=latest&topic0=${SolidityCode.logTransferFilter}&topic1=${address.toAddressCode()}&topic1_2_opr=or&topic2=${address.toAddressCode()}"
  }
  @JvmStatic val getTokenTransactionBetween: (sendAddress: String, receiveAddress: String, startBlock: String) -> String = { sendAddress, receiveAddress, startBlock ->
    "$logHeader/api?module=logs&action=getLogs&fromBlock=$startBlock&toBlock=latest&topic0=${SolidityCode.logTransferFilter}&topic1=${sendAddress.toAddressCode()}&topic2=${receiveAddress.toAddressCode()}"
  }
}
