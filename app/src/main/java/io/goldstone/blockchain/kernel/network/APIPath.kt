package io.goldstone.blockchain.kernel.network

import com.blinnnk.extension.getRandom
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

/*
 * `old key`
 * 1 E8AW54SAFUGK6KPCDTHZSBT6NF4KZHV25E
 * 2 E7VD17QW5G3B545B546U9AMI4EH7FI6HF8
 * 3 E111BHZC2AVER6SQYX5IHBQ16MPY6YI6EC
 */

// `EtherScan` 对 `Key` 的限制很大, 用随机的办法临时解决, 降低测试的时候出问题的概率
private val etherScanKeys = arrayListOf(
  "E8AW54SAFUGK6KPCDTHZSBT6NF4KZHV25E",
  "E7VD17QW5G3B545B546U9AMI4EH7FI6HF8",
  "E111BHZC2AVER6SQYX5IHBQ16MPY6YI6EC",
  "CJCMRVUIQ843XGJ2JSYKNYDQJBDP3A944V",
  "8DJXHMYI1AMWEEBF2CHEFE6N2EH5GFZ1Y1",
  "96HN9BN4X33PXZDM8VKPGT7XW44T3KBMXD",
  "8YZIW86T9IN7645JJ2EV1EG5CHATV1NCG3",
  "FEV3TSAACRZWCTPSNXD9WIBZRGFKS7WJ6C",
  "TGEMAT36N7HTFI8ABJ1XQV4XCBBQH6WEQZ",
  "GQPTQVJH53ET5UBTZH9A2SE9PUNCMRGUQH",
  "F9HA5RT13FF6EFAWCUM8TPBE5V98VDKK4N",
  "YYDMYK58H41VV15ZEM4W7R63RXTKHIRFFZ",
  "VYUZP7P5TU51IJ7EPM631GCUWSK2MBXMT7",
  "79J8345NMK4II6Z1ZSRW3XBUCNFAUDW75X",
  "8S242TTFTZVKCFPGG5SC99YQWFNWU9RXDM",
  "YTMKYMD1BBFKPKCNETVHWITKSQ5HE7TERQ",
  "MCB712JF5WKJH23TX5S2UF12U9GWM5I2UY",
  "WVE42TN2U198MEMFFN2D5J9QMI6A8RP89U",
  "BPVYHKH521TB2W3V5YRT7FPMIT2Q6QB6X5",
  "FIMIQUKT54SY8W3UYHG4MVZ9FPJTYCPNEV",
  "EIAK8X7EAKUJK7MCRTPTIJJC429TUTE5JB",
  "UR6ZB33DF4GE1DUC29HNEN5CZ9JAHXID9X",
  "W9Q97HURWKX8GP8Q4767J7MUZ3HSVIK364",
  "HKNS31UNTJESSW3TUC82CPNV94Z2WC1W2C",
  "TPYVYFAR549R5ND3CDHXTED1TRRQZRVG3W",
  "FYXB5V9SJ3QSJQMF96KQ1EAX48PKSD8TP9",
  "8F528KN2UBB2624JHQWCW1NZ8X8P16UX3A",
  "KKP6VYG6FYEZ7RT1X4Q1JNGQRN91D2X14F",
  "M8HK6R277YFWQ2HPTEFX8X4ZF9PGTD2EWR",
  "4KHBYYZCFHAWY8HP9HJ68P4I5X3JETX11Y",
  "FS1U9XCGN9IJMN57DHYRNWSHY1FVFMAU16",
  "NVZV5XUAG2VWNAC7HNFS4JZHW9GG22YPJF",
  "Z87HD2XDDRM3C5I85MD833VH1VFD8F37B8",
  "9FV9WZUF9BPHGSQETR8PYV35QGPRRQ7PFC",
  "63EP59177YUAQT1N8ZY8BCCSIEA4PJ4135",
  "KN15KZPRQ5BFRVQ3G8Q82UQM5RZVHNUT8K",
  "W562XI7JXMYTPN7JTKB35NTXEPNIRBV4Y5",
  "DKZZQZE1XHW49SPHSKM278B9N9WNUMQUWI",
  "9MKTVBN4QA3BEVW9IBHXY69RCF5S7BS18R",
  "5TNWW8IPNGZUBPGRCVDTVQI6T93J5A3CPW",
  "DBM1B8DH1DFMBZYW13XU8EAH2M5ZJCEM6F",
  "3E7BSC5ZCW31F16YQ2DUJJGPFU7WD4Z11P",
  "6ZI2BNR5MD7QXZ5GHQG1JVCRA3KSG4PRT5",
  "25TYWD47BSXS747895RNWANSS82ZNIPFVT",
  "XZNIW3XCJJUHVGJ9UJ169RH27NB5JT3GVE",
  "H73IWZP5YQJE7C4T6ZMH91IP6T4Q744XGJ",
  "C4UYM2R2985JAUWQ9XA4NHNEC2VZYWKNXM",
  "1E1JQ6JIKUEZ4CN1ZUGZQTNV1G34JWZJNM"
)

object EtherScanApi {
  @JvmStatic
  private val apikey: () -> String = { etherScanKeys.getRandom() }
  private const val header = "http://api-ropsten.etherscan.io"
  private const val logHeader = "http://ropsten.etherscan.io"

  @JvmStatic
  val transactions: (address: String, startBlock: String) -> String = { address, startBlock ->
    "$header/api?module=account&action=txlist&address=$address&startblock=$startBlock&endblock=99999999&sort=desc&apikey=$apikey"
  }
  @JvmStatic
  val transactionsByHash: (taxHash: String) -> String = {
    "$header/api?module=proxy&action=eth_getTransactionByHash&txhash=$it&apikey=$apikey"
  }
  @JvmStatic
  val singleTransactionHas: (hash: String) -> String = {
    "$header/api?module=proxy&action=eth_getTransactionByHash&txhash=$it&apikey=$apikey"
  }
  @JvmStatic
  val getTokenIncomingTransaction: (address: String, startBlock: String) -> String =
    { address, startBlock ->
      "$logHeader/api?module=logs&action=getLogs&fromBlock=$startBlock&toBlock=latest&topic0=${SolidityCode.logTransferFilter}&topic2=${address.toAddressCode()}"
    }
  @JvmStatic
  val getTokenDefrayTransaction: (address: String, startBlock: String) -> String =
    { address, startBlock ->
      "$logHeader/api?module=logs&action=getLogs&fromBlock=$startBlock&toBlock=latest&topic0=${SolidityCode.logTransferFilter}&topic1=${address.toAddressCode()}"
    }
  @JvmStatic
  val getAllTokenTransaction: (address: String, startBlock: String) -> String =
    { address, startBlock ->
      "$logHeader/api?module=logs&action=getLogs&fromBlock=$startBlock&toBlock=latest&topic0=${SolidityCode.logTransferFilter}&topic1=${address.toAddressCode()}&topic1_2_opr=or&topic2=${address.toAddressCode()}"
    }
  @JvmStatic
  val getTokenTransactionBetween: (sendAddress: String, receiveAddress: String, startBlock: String) -> String =
    { sendAddress, receiveAddress, startBlock ->
      "$logHeader/api?module=logs&action=getLogs&fromBlock=$startBlock&toBlock=latest&topic0=${SolidityCode.logTransferFilter}&topic1=${sendAddress.toAddressCode()}&topic2=${receiveAddress.toAddressCode()}"
    }
}
