package io.goldstone.blockchain.crypto.eos.transaction

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.eos.base.EOSModel
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.utils.*
import io.goldstone.blockchain.kernel.commontable.EOSTransactionTable
import io.goldstone.blockchain.kernel.network.ParameterUtil
import io.goldstone.blockchain.kernel.network.eos.EOSTransaction
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.presenter.BaseTradingPresenter
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.StakeType
import org.json.JSONObject
import java.io.Serializable
import java.math.BigInteger

/**
 * @author KaySaith
 * @date 2018/09/03
 */

data class EOSTransactionInfo(
	val fromAccount: EOSAccount,
	val toAccount: EOSAccount,
	val amount: BigInteger, // 这里是把精度包含进去的最小单位的值, 签名的时候会对这个值直接转换
	val contract: TokenContract,
	val memo: String,
	// 账单的模型和买内存的复用, 唯一不同的是, 是否包含 `Memo`. 这个 `Boolean` 主要的用处是
	// 在序列化的时候不让 `Memo` 参与其中. 注意 就算 `Memo` 为空 序列化也会得出 `00` 的值, 所以
	// 一定是不参与序列化
	val isTransaction: Boolean
) : Serializable, EOSModel {

	private val chainID = SharedChain.getEOSCurrent().chainID

	// For DAPP
	constructor(action: JSONObject, customMemo: String) : this(
		EOSAccount(action.getTargetObject("data").safeGet("from")),
		EOSAccount(action.getTargetObject("data").safeGet("to")),
		action.getTargetObject("data")
			.safeGet("quantity")
			.substringBeforeLast(" ")
			.toDoubleOrZero()
			.toAmount(action.getDecimalFromData()),
		TokenContract(
			action.safeGet("account"),
			action.getTargetObject("data").safeGet("quantity").substringAfterLast(" "),
			action.getDecimalFromData()
		),
		customMemo,
		true
	)

	constructor(data: JSONObject) : this(
		EOSAccount(data.safeGet("from")),
		EOSAccount(data.safeGet("to")),
		data.safeGet("quantity").substringBeforeLast(" ").toDoubleOrZero().toEOSUnit(),
		TokenContract.EOS,
		data.safeGet("memo"),
		true
	)

	constructor(
		fromAccount: EOSAccount,
		toAccount: EOSAccount,
		amount: BigInteger
	) : this(
		fromAccount,
		toAccount,
		amount,
		TokenContract.EOS,
		"",
		false
	)

	constructor(
		fromAccount: EOSAccount,
		toAccount: EOSAccount,
		amount: BigInteger,
		memo: String,
		contract: TokenContract
	) : this(
		fromAccount,
		toAccount,
		amount,
		contract,
		memo,
		true
	)

	// 转不同的币需要标记 `Decimal` 不然会转账失败, 这里面会有检查函数
	// 故在此传入 Decimal
	fun trade(
		context: Context,
		chaiURL: String = SharedChain.getEOSCurrent().getURL(),
		cancelAction: () -> Unit,
		@WorkerThread hold: (response: EOSResponse?, error: GoldStoneError) -> Unit
	) {
		if (!toAccount.isValid(false)) {
			hold(null, AccountError.InvalidAccountName)
		} else {
			BaseTradingPresenter.prepareTransaction(
				context,
				amount.toCount(contract.decimal ?: CryptoValue.eosDecimal),
				contract,
				StakeType.Trade,
				cancelAction = cancelAction
			) { privateKey, error ->
				if (error.isNone() && privateKey.isNotNull()) {
					transfer(EOSPrivateKey(privateKey), chaiURL, hold)
				} else hold(null, error)
			}
		}
	}

	fun insertPendingDataToDatabase(
		response: EOSResponse,
		@WorkerThread callback: () -> Unit
	) {
		// 把这条转账数据插入本地数据库作为 `Pending Data` 进行检查
		EOSTransactionTable.getMaxDataIndexTable(
			fromAccount,
			contract,
			SharedChain.getEOSCurrent().chainID
		) {
			val dataIndex = if (it.isNull()) 0 else it + 1
			val transaction = EOSTransactionTable(this, response, dataIndex)
			EOSTransactionTable.dao.insert(transaction)
			callback()
		}
	}

	private fun transfer(
		privateKey: EOSPrivateKey,
		chaiURL: String = SharedChain.getEOSCurrent().getURL(),
		@WorkerThread hold: (response: EOSResponse?, error: GoldStoneError) -> Unit
	) {
		val permission =
			EOSAccountTable.getValidPermission(fromAccount, chainID)
		if (permission.isNotNull()) {
			EOSTransaction(
				EOSAuthorization(fromAccount.name, permission),
				toAccount.name,
				amount,
				memo,
				// 这里现在默认有效期设置为 5 分钟. 日后根据需求可以用户自定义
				ExpirationType.FiveMinutes,
				contract
			).send(privateKey, chaiURL, hold)
		} else hold(null, TransferError.WrongPermission)
	}

	override fun createObject(): String {
		return ParameterUtil.prepareObjectContent(
			Pair("from", fromAccount),
			Pair("to", toAccount),
			// `Count` 与 `Symbol` 之间留一个空格, 官方强制的脑残需求
			Pair("quantity", "${CryptoUtils.toCountByDecimal(amount, contract.decimal.orElse(CryptoValue.eosDecimal))} " + contract.symbol),
			Pair("memo", fromAccount)
		)
	}

	override fun serialize(): String {
		val encryptFromAccount = EOSUtils.getLittleEndianCode(fromAccount.name)
		val encryptToAccount = EOSUtils.getLittleEndianCode(toAccount.name)
		val amountCode = EOSUtils.convertAmountToCode(amount)
		val decimalCode = EOSUtils.getEvenHexOfDecimal(contract.decimal.orElse(CryptoValue.eosDecimal))
		var symbolCode = contract.symbol.toCryptHexString()
		// `symbol` 长度为固定 `7` 字节，`ASCII` 编码, 就是固定长度 `14` 个字符
		symbolCode = symbolCode.completeZero(14 - symbolCode.count())
		val memoCode = if (isTransaction) EOSUtils.convertMemoToCode(memo) else ""
		return encryptFromAccount + encryptToAccount + amountCode + decimalCode + symbolCode + memoCode
	}

	companion object {
		fun serializedEOSAmount(amount: BigInteger): String {
			val amountCode = EOSUtils.convertAmountToCode(amount)
			val decimalCode = EOSUtils.getEvenHexOfDecimal(CryptoValue.eosDecimal)
			val symbolCode = CoinSymbol.eos.toByteArray().toNoPrefixHexString()
			val completeZero = "00000000"
			return amountCode + decimalCode + symbolCode + completeZero
		}

		/**
		 * `quantity`:`0.000 IQ` 通过截取小数点后的数字计算出 Decimal
		 */
		fun JSONObject.getDecimalFromData(): Int {
			return getTargetObject("data")
				.safeGet("quantity")
				.substringBefore(" ")
				.substringAfterLast(".").length
		}
	}
}