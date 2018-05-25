package io.goldstone.blockchain.kernel.network

import com.blinnnk.extension.getRandom
import io.goldstone.blockchain.crypto.SolidityCode
import io.goldstone.blockchain.crypto.toAddressCode

/**
 * @date 31/03/2018 8:09 PM
 * @author KaySaith
 */

object APIPath {
	/** GoldStone Basic Api Address */
	private const val url = "https://goldstone-api-test.naonaola.com"
	const val defaultTokenList = "$url/index/defaultCoinList"
	const val getCoinInfo = "$url/index/searchToken?symbolOrContract="
	const val getCurrencyRate = "$url/index/exchangeRate?currency="
	const val registerDevice = "$url/account/registerDevice"
	const val updateAddress = "$url/account/updateAddress"
	const val getNotification = "$url/account/unreadMessageList"
	const val marketSearch = "$url/account/searchPair?pair="
	const val getCurrencyLineChartData = "$url/account/lineDataByDay"
	const val getPriceByAddress = "$url/index/priceByAddress"
	const val getTokenDescription = "$url/market/coinDescription?symbol="
	const val getUnreadCount = "$url/account/checkUnreadMessage"

	/** Chain Address */
	const val ropstanInfura = "https://ropsten.infura.io/QaK7ndbTdXqQNObSiKY8"
	const val ropstan = "https://eth-node-ropsten.naonaola.com/eth"
	const val main = "https://eth-node-mainnet.naonaola.com/eth"

	@JvmField
	val getQuotationCurrencyChart: (
		pair: String, period: String, size: Int
	) -> String = { pair, period, size ->
		"$url/market/lineData?pair=$pair&period=$period&size=$size"
	}
	@JvmField
	val getQuotationCurrencyInfo: (pair: String) -> String = { pair ->
		"$url/market/coinDetail?pair=$pair"
	}
}

// `EtherScan` 对 `Key` 的限制很大, 用随机的办法临时解决, 降低测试的时候出问题的概率
private val etherScanKeys = arrayListOf(
	"E8AW54SAFUGK6KPCDTHZSBT6NF4KZHV25E", "E7VD17QW5G3B545B546U9AMI4EH7FI6HF8",
	"E111BHZC2AVER6SQYX5IHBQ16MPY6YI6EC", "CJCMRVUIQ843XGJ2JSYKNYDQJBDP3A944V",
	"8DJXHMYI1AMWEEBF2CHEFE6N2EH5GFZ1Y1", "96HN9BN4X33PXZDM8VKPGT7XW44T3KBMXD",
	"8YZIW86T9IN7645JJ2EV1EG5CHATV1NCG3", "FEV3TSAACRZWCTPSNXD9WIBZRGFKS7WJ6C",
	"TGEMAT36N7HTFI8ABJ1XQV4XCBBQH6WEQZ", "GQPTQVJH53ET5UBTZH9A2SE9PUNCMRGUQH",
	"F9HA5RT13FF6EFAWCUM8TPBE5V98VDKK4N", "YYDMYK58H41VV15ZEM4W7R63RXTKHIRFFZ",
	"VYUZP7P5TU51IJ7EPM631GCUWSK2MBXMT7", "79J8345NMK4II6Z1ZSRW3XBUCNFAUDW75X",
	"8S242TTFTZVKCFPGG5SC99YQWFNWU9RXDM", "YTMKYMD1BBFKPKCNETVHWITKSQ5HE7TERQ",
	"MCB712JF5WKJH23TX5S2UF12U9GWM5I2UY", "WVE42TN2U198MEMFFN2D5J9QMI6A8RP89U",
	"BPVYHKH521TB2W3V5YRT7FPMIT2Q6QB6X5", "FIMIQUKT54SY8W3UYHG4MVZ9FPJTYCPNEV",
	"EIAK8X7EAKUJK7MCRTPTIJJC429TUTE5JB", "UR6ZB33DF4GE1DUC29HNEN5CZ9JAHXID9X",
	"W9Q97HURWKX8GP8Q4767J7MUZ3HSVIK364", "HKNS31UNTJESSW3TUC82CPNV94Z2WC1W2C",
	"TPYVYFAR549R5ND3CDHXTED1TRRQZRVG3W", "FYXB5V9SJ3QSJQMF96KQ1EAX48PKSD8TP9",
	"8F528KN2UBB2624JHQWCW1NZ8X8P16UX3A", "KKP6VYG6FYEZ7RT1X4Q1JNGQRN91D2X14F",
	"M8HK6R277YFWQ2HPTEFX8X4ZF9PGTD2EWR", "4KHBYYZCFHAWY8HP9HJ68P4I5X3JETX11Y",
	"FS1U9XCGN9IJMN57DHYRNWSHY1FVFMAU16", "NVZV5XUAG2VWNAC7HNFS4JZHW9GG22YPJF",
	"Z87HD2XDDRM3C5I85MD833VH1VFD8F37B8", "9FV9WZUF9BPHGSQETR8PYV35QGPRRQ7PFC",
	"63EP59177YUAQT1N8ZY8BCCSIEA4PJ4135", "KN15KZPRQ5BFRVQ3G8Q82UQM5RZVHNUT8K",
	"W562XI7JXMYTPN7JTKB35NTXEPNIRBV4Y5", "DKZZQZE1XHW49SPHSKM278B9N9WNUMQUWI",
	"9MKTVBN4QA3BEVW9IBHXY69RCF5S7BS18R", "5TNWW8IPNGZUBPGRCVDTVQI6T93J5A3CPW",
	"DBM1B8DH1DFMBZYW13XU8EAH2M5ZJCEM6F", "3E7BSC5ZCW31F16YQ2DUJJGPFU7WD4Z11P",
	"6ZI2BNR5MD7QXZ5GHQG1JVCRA3KSG4PRT5", "25TYWD47BSXS747895RNWANSS82ZNIPFVT",
	"XZNIW3XCJJUHVGJ9UJ169RH27NB5JT3GVE", "H73IWZP5YQJE7C4T6ZMH91IP6T4Q744XGJ",
	"C4UYM2R2985JAUWQ9XA4NHNEC2VZYWKNXM", "1E1JQ6JIKUEZ4CN1ZUGZQTNV1G34JWZJNM",
	"JXESJRQPXZC1XADV5FMDHVC4PWZF6Y5YNS", "RM1MK15GQXBGUS7DERFJ5NJB3PS9IZWDD6",
	"JRVIWM827FS8179SIDH4M33PIV9VYR3X3P", "FAUGQJGHEE7P2BY186Y7K2YKH3KF8KZUEQ",
	"5S4M9UIBNZ8KBD6GFMT8SEB8ZNY1AUEC9C", "66VKX5PADZDXS89R5D16YRFVNMWI1UJWBZ",
	"5QMHQBHKGNADBCS8MA9I7XMXU6JXQ1ZDBX", "RV9TSJ83HD4K8XARAZJBX73QPISKWFPV3D",
	"MVZUHQB4F9T8YMMYRZTZ1ITJ48SN73Q7IU", "WDSSZDG8PD6QRKXF9IEVD5SIA3723VR2E3",
	"2536IGMTHV9X6IYQGZ38VQM8GV7F6TM5BN", "DW75F8AIDEN3YXM6SENX12ST8V9SWNNRUT",
	"DTX3T4QNH3ITA2FZE6E67KX452ITMZXDXW", "N7I6A4NGUYVJ3HEUGTQNNSWTXSJV2I56VE",
	"D9G8WAIRSZ8KY2ESH5P98WBR9IISFBTRPN", "VIWGJ9XVY4J7961EVTF6N43E1YBV1SX2GG",
	"RWJKGUF5ZKGEAP4BM1XPUJFG6HINWSF5YI", "GBZ5BQNQG1JB7JQ826VEH7E3EYBT985CGR",
	"BHVUSSQ5H5ZXWRKKYP6UFC9SP5WYU3DC88", "SNNF5YWH33685X6EGRJNIY945MVSAFRMQN",
	"ZYYII7YY96MFZYTEKNTJPTDIAH347NBIIH", "EKE9FS66AS8EXPX8EEWH843QWPS13ECGIF",
	"RBY6QIGHPX7RBB91VVHRHBHQERZ64VFRVH", "XKA9PB2QE5H5CGR3HFZSYFT8RYIAETHFUU",
	"RF7T8YMWRQ99H27PY2UFGED68WCNDV7PMW", "C4TK8ASIH68KZERCFYJW1C32JCKYHWHJNM",
	"P4WDUJNXV7XMVYX2G95RUZ2S1WPU1NPNYC", "6PWAUNJ8S6ZPNE889S61EWYNVT6CP19GMH",
	"JX53QMG73JIBWECRNIRAEP463TK2U7WI74", "7ZDZFVJNQNN54ACNJM7R2WUY1X1FCVPZ5R",
	"CSUBWG74A69T4CQ7H8WPTDA7E1N6HMQKIH", "3NHCNKK21VMBCERP6IHSKZKPW1MCUYXGB6",
	"KS6VX72FIKSYHXC5TEWYT33UHNY883Y516", "CFHS7WZNH13KSXH7G45YWBPZUE5FT1SRKE",
	"RQI19GZQI7DGI13KY5BPG1SIXSRJUH8C1D", "6SNFWNY6TGKDV5C82NEVYRY13GREW1Z2F6",
	"GBUJ8ME1HQC8YJSJW378QEKF5EMFGMTPV9", "8SA9BV58443UUR9UKPSW42RDFUZZIVVPBQ",
	"CZ9KSB4GAHDBJV6N25V1562VUGV6Q87DH5", "PDYP3Z5FZN7FRA6CZ4CVPSFRKZFI8G1T5N",
	"KB8X4PHYGA4GIP72V8SAWXY834IK8IMDA4", "YUBFB2H581II2CGVX2S1AQKZZP1JDVCCMN",
	"KBIM1RZC1KYSMU2VBN3SJSZK2S3BVN14PS", "21VDD8YWDHFY4SGM9X4RY9386E7DRW7GWM",
	"BJYQFJS8YEJ7VUFJF433VTXN7Z1K4IBY4H"
)

object EtherScanApi {

	private val apikey: () -> String = { etherScanKeys.getRandom() }

	private const val header = "http://api-ropsten.etherscan.io"
	private const val logHeader = "http://ropsten.etherscan.io"
	@JvmField
	val transactionDetail: (taxHash: String) -> String = {
		"https://ropsten.etherscan.io/tx/$it"
	}

	@JvmStatic
	val transactions: (address: String, startBlock: String) -> String = { address, startBlock ->
		"$header/api?module=account&action=txlist&address=$address&startblock=$startBlock&endblock=99999999&sort=desc&apikey=${apikey()}"
	}
	@JvmStatic
	val transactionsByHash: (taxHash: String) -> String = {
		"$header/api?module=proxy&action=eth_getTransactionByHash&txhash=$it&apikey=${apikey()}"
	}
	@JvmStatic
	val singleTransactionHas: (hash: String) -> String = {
		"$header/api?module=proxy&action=eth_getTransactionByHash&txhash=$it&apikey=${apikey()}"
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
