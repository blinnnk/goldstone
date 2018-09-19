package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.accountregister.EOSResponse
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.crypto.eos.transaction.EOSTransactionInfo
import io.goldstone.blockchain.crypto.eos.transaction.ExpirationType
import io.goldstone.blockchain.crypto.error.GoldStoneError
import io.goldstone.blockchain.crypto.error.TransferError
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.utils.isValidDecimal
import io.goldstone.blockchain.crypto.utils.toEOSUnit
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.eos.EOSTransaction
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.GasSelectionPresenter
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter.PaymentPreparePresenter.Companion.checkBalanceIsEnoughOrElse
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter.PaymentPreparePresenter.Companion.showGetPrivateKeyDashboard
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import org.jetbrains.anko.doAsync


/**
 * @author KaySaith
 * @date  2018/09/14
 */

// EOS 的 `Token` 转币只需写对 `Token` 的 `Symbol` 就可以转账成功
fun PaymentPreparePresenter.transferEOS(
	count: Double,
	symbol: CoinSymbol,
	@UiThread callback: (error: GoldStoneError) -> Unit
) {
	val accountName = Config.getCurrentEOSName()
	if (!count.toString().isValidDecimal(CryptoValue.eosDecimal))
		callback(TransferError.incorrectDecimal)
	checkBalanceIsEnoughOrElse(accountName, symbol, count) { hasEnoughBalance ->
		if (hasEnoughBalance) {
			// 准备转账信息
			val eosTransactionInfo =
				generateEOSPaymentModel(
					accountName,
					count,
					fragment.getMemoContent(),
					symbol.symbol!!
				)
			// 向用户获取解锁 `Keystore` 密码获取 `KeyStore` 中的 `PrivateKey` 进行签名
			showGetPrivateKeyDashboard(fragment.context) { privateKey, error ->
				if (privateKey.isNull()) callback(error)
				else transferEOSToken(eosTransactionInfo, privateKey!!, callback) { response ->
					insertPendingDataAndGoToTransactionDetail(eosTransactionInfo, response, callback)
				}
			}
		} else callback(TransferError.balanceIsNotEnough)
	}
}

private fun PaymentPreparePresenter.insertPendingDataAndGoToTransactionDetail(
	info: EOSTransactionInfo,
	response: EOSResponse,
	callback: (GoldStoneError) -> Unit
) {
	val receiptModel = ReceiptModel(info, response, getToken()!!)
	GasSelectionPresenter.goToTransactionDetailFragment(
		rootFragment,
		fragment,
		receiptModel
	)
	// 把这条转账数据插入本地数据库作为 `Pending Data` 进行检查
	doAsync {
		GoldStoneDataBase.database.eosTransactionDao().apply {
			val dataIndex =
				getDataByRecordAccount(info.fromAccount).maxBy { it.dataIndex }?.dataIndex ?: 0
			val transaction = EOSTransactionTable(info, response, dataIndex)
			insert(transaction)
		}
	}
	callback(TransferError.none)
}

private fun transferEOSToken(
	info: EOSTransactionInfo,
	privateKey: EOSPrivateKey,
	errorCallback: (GoldStoneError) -> Unit,
	hold: (EOSResponse) -> Unit
) {
	EOSTransaction(
		EOSAuthorization(info.fromAccount, EOSActor.Active),
		info.toAccount,
		info.amount,
		info.memo,
		// 这里现在默认有效期设置为 5 分钟. 日后根据需求可以用户自定义
		ExpirationType.FiveMinutes,
		info.symbol
	).send(privateKey, errorCallback, hold)
}

private fun PaymentPreparePresenter.generateEOSPaymentModel(
	accountName: String,
	count: Double,
	memo: String,
	symbol: String
): EOSTransactionInfo {
	// TODO 检查 Symbol 准备正确的 Long Value
	// 检查是否输入了正确的精度值
	return EOSTransactionInfo(
		accountName,
		fragment.address!!,
		count.toEOSUnit(),
		memo,
		symbol
	)
}

