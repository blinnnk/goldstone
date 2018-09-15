package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailHeaderView

/**
 * @date 2018/6/6 4:26 PM
 * @author KaySaith
 */
fun TransactionDetailPresenter.updateDataFromTransfer() {
	data?.apply {
		currentHash = taxHash
		count = CryptoUtils.toCountByDecimal(value.toBigDecimal().toDouble(), token.decimal)
		fragment.asyncData = generateModels().toArrayList()
		val headerData = TransactionHeaderModel(
			count,
			toAddress,
			token.symbol,
			true
		)
		updateHeaderValue(headerData)
		headerModel = headerData
		when {
			TokenContract(token.contract).isBTC() -> observerBTCTransaction()
			TokenContract(token.contract).isLTC() -> observerLTCTransaction()
			TokenContract(token.contract).isBCH() -> observerBCHTransaction()
			TokenContract(token.contract).isEOS() -> observerEOSTransaction()
			else -> observerTransaction()
		}
	}
}

fun TransactionDetailPresenter.showConformationInterval(
	intervalCount: Int
) {
	fragment.recyclerView.getItemAtAdapterPosition<TransactionDetailHeaderView>(0) { it ->
		it.apply {
			headerModel?.let {
				updateHeaderValue(it)
			}
			updateConformationBar(intervalCount)
		}
	}
}

fun TransactionDetailPresenter.updateConformationBarFinished() {
	fragment.recyclerView.getItemAtAdapterPosition<TransactionDetailHeaderView>(0) {
		it.apply {
			updateConformationBar(CryptoValue.confirmBlockNumber)
		}
	}
}