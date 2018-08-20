package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoValue
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
		fragment.asyncData = generateModels()
		val headerData = TransactionHeaderModel(
			count,
			toAddress,
			token.symbol,
			true
		)
		updateHeaderValue(headerData)
		headerModel = headerData
		when {
			token.symbol.equals(CryptoSymbol.btc(), true) -> observerBTCTransaction()
			token.symbol.equals(CryptoSymbol.ltc, true) -> observerLTCTransaction()
			token.symbol.equals(CryptoSymbol.bch, true) -> observerBCHTransaction()
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