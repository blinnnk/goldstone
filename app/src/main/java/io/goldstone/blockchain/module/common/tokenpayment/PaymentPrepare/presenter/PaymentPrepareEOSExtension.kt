package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.eos.transaction.EOSTransactionInfo
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.multichain.orEmpty
import io.goldstone.blockchain.crypto.utils.toAmount
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.goToTransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import org.jetbrains.anko.doAsync


/**
 * @author KaySaith
 * @date  2018/09/14
 */

// EOS 的 `Token` 转币只需写对 `Token` 的 `Symbol` 就可以转账成功
fun PaymentPreparePresenter.transferEOS(
	count: Double,
	contract: TokenContract,
	@UiThread callback: (error: GoldStoneError) -> Unit
) {
	// 准备转账信息
	EOSTransactionInfo(
		SharedAddress.getCurrentEOSAccount(),
		EOSAccount(fragment.address!!),
		count.toAmount(contract.decimal.orElse(CryptoValue.eosDecimal)),
		fragment.getMemoContent(),
		contract
	).apply {
		trade(fragment.context) { response, error ->
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
	rootFragment?.goToTransactionDetailFragment(fragment, receiptModel)
	// 把这条转账数据插入本地数据库作为 `Pending Data` 进行检查
	doAsync {
		EOSTransactionTable.getMaxDataIndexTable(
			info.fromAccount,
			getToken()?.contract.orEmpty(),
			SharedChain.getEOSCurrent().chainID,
			false
		) {
			val dataIndex = if (it?.dataIndex.isNull()) 0 else it?.dataIndex!! + 1
			val transaction = EOSTransactionTable(info, response, dataIndex)
			GoldStoneDataBase.database.eosTransactionDao().insert(transaction)
		}
	}
	callback(GoldStoneError.None)
}

