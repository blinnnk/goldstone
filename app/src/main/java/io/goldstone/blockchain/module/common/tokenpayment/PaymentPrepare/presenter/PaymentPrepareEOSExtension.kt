package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter

import android.content.Context
import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.accountregister.EOSResponse
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.crypto.eos.transaction.EOSTransactionInfo
import io.goldstone.blockchain.crypto.eos.transaction.ExpirationType
import io.goldstone.blockchain.crypto.error.TransferError
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.utils.isValidDecimal
import io.goldstone.blockchain.crypto.utils.toEOSUnit
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneAPI.context
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.EOSTransaction
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.GasSelectionPresenter
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter.PrivateKeyExportPresenter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread


/**
 * @author KaySaith
 * @date  2018/09/14
 */

// EOS 的 `Token` 转币只需写对 `Token` 的 `Symbol` 就可以转账成功
fun PaymentPreparePresenter.transferEOS(
	count: Double,
	symbol: String,
	@UiThread callback: (error: TransferError) -> Unit
) {
	val accountName = Config.getCurrentEOSName()
	if (!count.toString().isValidDecimal(CryptoValue.eosDecimal))
		callback(TransferError.IncorrectDecimal)
	checkBalanceIsEnoughOrElse(accountName, symbol, count) { hasEnoughBalance ->
		if (hasEnoughBalance) {
			// 准备转账信息
			val eosTransactionInfo =
				generateEOSPaymentModel(
					accountName,
					count,
					fragment.getMemoContent(),
					symbol
				)
			// 向用户获取解锁 `Keystore` 密码获取 `KeyStore` 中的 `PrivateKey` 进行签名
			fragment.context?.getPasswordAndTransferByInfo(eosTransactionInfo) { response, error ->
				// 如果 response 不为空, 那么插入转账数据的 `Pending Data` 到数据库
				if (!response.isNull()) insertPendingDataAndGoToTransactionDetail(eosTransactionInfo, response!!) {
					callback(error)
				} else callback(error)
			}
		} else callback(TransferError.BalanceIsNotEnough)
	}
}

private fun PaymentPreparePresenter.insertPendingDataAndGoToTransactionDetail(
	info: EOSTransactionInfo,
	response: EOSResponse,
	callback: () -> Unit
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
	callback()
}

private fun Context.getPasswordAndTransferByInfo(
	info: EOSTransactionInfo,
	callback: (response: EOSResponse?, error: TransferError) -> Unit
) {
	showAlertView(
		"Transfer EOS Token",
		"prepare to transfer eos token, now you should enter your password",
		true
	) { passwordInput ->
		if (passwordInput.isNull()) return@showAlertView
		val password = passwordInput!!.text.toString()
		if (password.isNotEmpty()) WalletTable.getCurrentWallet {
			PrivateKeyExportPresenter.getPrivateKey(
				context,
				Config.getCurrentEOSAddress(),
				ChainType.EOS.id,
				password
			) {
				if (!isNullOrEmpty()) transferEOSToken(
					info,
					EOSPrivateKey(this!!),
					{ callback(null, TransferError.GetChainInfoError) } // Error callback
				) {
					callback(it, TransferError.None)
				}
				else alert("decrypt your keystore by password found error")
			}
		} else alert(CommonText.enterPassword)
	}
}

private fun transferEOSToken(
	info: EOSTransactionInfo,
	privateKey: EOSPrivateKey,
	errorCallback: (Throwable) -> Unit,
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

private fun checkBalanceIsEnoughOrElse(
	accountName: String,
	symbol: String,
	transferCount: Double,
	@UiThread hold: (isEnough: Boolean) -> Unit
) {
	EOSAPI.getAccountBalanceBySymbol(accountName, symbol) { balance ->
		GoldStoneAPI.context.runOnUiThread {
			hold(balance >= transferCount)
		}
	}
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

