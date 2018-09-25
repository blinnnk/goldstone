package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.eos.transaction.EOSTransactionInfo
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.utils.toEOSUnit
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.GasSelectionPresenter
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
	// 准备转账信息
	EOSTransactionInfo(
		Config.getCurrentEOSAccount(),
		EOSAccount(fragment.address!!),
		count.toEOSUnit(),
		fragment.getMemoContent(),
		symbol.symbol!!
	).apply {
		trade(fragment.context) { error, response ->
			if (error.isNone() && !response.isNull())
				insertPendingDataAndGoToTransactionDetail(this, response!!, callback)
			else callback(error)
		}
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
				getDataByRecordAccount(info.fromAccount.accountName).maxBy { it.dataIndex }?.dataIndex ?: 0
			val transaction = EOSTransactionTable(info, response, dataIndex)
			insert(transaction)
		}
	}
	callback(GoldStoneError.None)
}

