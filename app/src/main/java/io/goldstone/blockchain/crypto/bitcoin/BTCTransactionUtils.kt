@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.crypto.bitcoin

import io.goldstone.blockchain.crypto.utils.toNoPrefixHexString
import io.goldstone.blockchain.kernel.network.bitcoin.model.UnspentModel
import org.bitcoinj.core.*
import org.bitcoinj.core.Utils.HEX
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.script.Script
import org.spongycastle.asn1.DERInteger
import org.spongycastle.asn1.DERSequenceGenerator
import org.spongycastle.asn1.sec.SECNamedCurves
import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.crypto.signers.ECDSASigner
import org.spongycastle.crypto.signers.HMacDSAKCalculator
import org.spongycastle.util.encoders.Hex
import java.io.ByteArrayOutputStream
import java.math.BigInteger

/**
 * @date 2018/7/20 11:36 AM
 * @author KaySaith
 */
object BTCTransactionUtils {
	
	fun generateSignedRawTransaction(
		sendValue: Long,
		fee: Long,
		targetAddress: String,
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
			Address.fromBase58(net, targetAddress)
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
	
	@JvmStatic
	fun signHash(rawTransaction: String, privateKey: String, isTest: Boolean): String {
		val net = if (isTest) TestNet3Params.get() else MainNetParams.get()
		println("$rawTransaction $privateKey $net")
		// creating a key object from WiF
		val dpk =
			DumpedPrivateKey.fromBase58(
				TestNet3Params.get(),
				"cPoTY5H8dmo6Dd3MroXrg9xeKghi2CrV31HFn6kzWGb1XGAv1CRm"
			)
		val key = dpk.key
		val raw =
			"02000000013173eae9502dc9eda19c5c027b708c9b2cafcdb0bdbae83e83c7bcc0bcf8a2c70100000000ffffffff0260e31600000000001976a9148cb357fd65f42f78a1ce48b47a018e504bbcd1cf88acc0257a09000000001976a91411d529e706f1366ff97a8fe0ce3ec8ece9bc72ab88ac00000000"
		val doubleSha256 = DoubleSHA256.gen(Hex.decode(raw))
		val endian1 = doubleSha256.toNoPrefixHexString().toLittleEndian().orEmpty()
		System.out.println("++++${sign(endian1, key.privKey)}")
		return ""
	}
	
	fun sign(message: String, privateKey: BigInteger): String {
		val signer = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))
		val params = SECNamedCurves.getByName("secp256k1")
		val ecDomainParameters = ECDomainParameters(params.curve, params.g, params.n, params.h)
		val ecPrivateKeyParameters = ECPrivateKeyParameters(privateKey, ecDomainParameters)
		signer.init(true, ecPrivateKeyParameters)
		val sigs = signer.generateSignature(Hex.decode(message))
		val byteArrayOutputStream = ByteArrayOutputStream()
		val seq = DERSequenceGenerator(byteArrayOutputStream)
		seq.addObject(DERInteger(sigs[0]))
		seq.addObject(DERInteger(params.n - sigs[1]))
		seq.close()
		return byteArrayOutputStream.toByteArray().toNoPrefixHexString()
	}
	
	fun createRawTransaction(
		myLatestHash: String,
		outpointIndexNumber: String,
		targetAddressPubHash: String,
		outputTransactionCount: Int,
		amount: String,
		isTest: Boolean
	): String {
		val versionCode = if (isTest) RawTransaction.testVersionCode else RawTransaction.mainVersionCode
		return versionCode +
		       RawTransaction.numberOfInputs +
		       myLatestHash.toLittleEndian() +
		       outpointIndexNumber +
		       RawTransaction.orderIndex +
		       outputTransactionCount.toString() +
		       amount +
		       RawTransaction.p2pkhSize +
		       RawTransaction.opDup +
		       RawTransaction.opHash160 +
		       RawTransaction.pushData14 +
		       targetAddressPubHash +
		       RawTransaction.opEqualverify +
		       RawTransaction.opChecksig +
		       RawTransaction.lockTime
	}
}

object RawTransaction {
	const val mainVersionCode = "01000000"
	const val testVersionCode = "02000000"
	const val numberOfInputs = "01"
	const val scriptSig = "6a" // 该段数据为其后面紧随的scriptSig，即解锁脚本的字节数，十六进制格式
	const val pushData47 = "47" // PUSHDATA 47，将47个字节的数据压入栈中
	const val sigHashAll = "01"
	const val pushData21 = "21" // PUSHDATA 21，将21个字节的数据压入栈中
	const val orderIndex = "00ffffffff"
	const val p2pkhSize = "19" // 上锁脚本（P2PKH）的大小。后面为该脚本的内容
	const val opDup = "76"
	const val opHash160 = "a9"
	const val pushData14 = "14"
	const val opEqualverify = "88" // OP_EQUALVERIFY，上锁脚本scriptPubKey的一部分
	const val opChecksig = "ac" // OP_CHECKSIG，上锁脚本scriptPubKey的一部分
	const val lockTime =
		"00000000" // nLockTime，可以为UNIX时间戳或者区块高度。在达到这个数值之前，该笔交易不可被添加进区块。若nLockTime为0则表示该交易可以被立刻执行
	const val transactionHashLength = 64
}
