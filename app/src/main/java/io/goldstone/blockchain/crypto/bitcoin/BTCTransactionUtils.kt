@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.crypto.bitcoin

import io.goldstone.blockchain.kernel.network.bitcoin.model.UnspentModel
import org.bitcoinj.core.*
import org.bitcoinj.core.Utils.HEX
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.script.Script

/**
 * @date 2018/7/20 11:36 AM
 * @author KaySaith
 */
object BTCTransactionUtils {
	
	fun generateSignedRawTransaction(
		sendValue: Long,
		fee: Long,
		toAddress: String,
		changeAddress: String,
		unspentModel: List<UnspentModel>,
		base58Privatekey: String,
		isTest: Boolean
	): BTCSignedModel {
		val net = if (isTest) TestNet3Params.get() else MainNetParams.get()
		//传入主网参数
		val transaction = Transaction(net)
		val privateKey =
			DumpedPrivateKey.fromBase58(net, base58Privatekey)
		val ecKey = privateKey.key
		var money = 0L
		val utxos = arrayListOf<UTXO>()
		unspentModel.forEach {
			//当消费列表某几个 `item` 的值加起来大于实际转账金额+手续费,
			// 就跳出循环, 这个时候就得到了合符条件的utxos数组
			if (money >= (sendValue + fee)) {
				return@forEach
			}
			//遍历unspents, 组装合适的item
			val utxo = UTXO(
				Sha256Hash.wrap(it.txid),
				it.outputNumber,
				Coin.valueOf(it.value),
				it.confirmations,
				false,
				Script(HEX.decode(it.script))
			)
			utxos.add(utxo)
			//把消费列表的值加起来
			money += it.value
		}
		//输出-转给别人
		transaction.addOutput(
			Coin.valueOf(sendValue),
			Address.fromBase58(net, toAddress)
		)
		//消费列表总金额 - 已经转账的金额 - 手续费 就等于需要返回给自己的金额了
		val leave = money - sendValue - fee
		//输出-转给自己
		if (leave > 0) {
			//输出-转给自己
			transaction.addOutput(
				Coin.valueOf(leave),
				Address.fromBase58(net, changeAddress)
			)
		}
		//输入未消费列表项
		utxos.forEach {
			val outPoint = TransactionOutPoint(net, it.index, it.hash)
			transaction.addSignedInput(outPoint, it.script, ecKey, Transaction.SigHash.ALL, true)
		}
		return BTCSignedModel(HEX.encode(transaction.bitcoinSerialize()), transaction.messageSize)
	}
}
