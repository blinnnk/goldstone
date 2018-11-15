package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.orEmpty
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.multichain.isEOS
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel

/**
 * @author KaySaith
 * @date  2018/10/17
 */

@WorkerThread
fun TokenDetailPresenter.flipEOSPage(callback: () -> Unit) {
	val account = SharedAddress.getCurrentEOSAccount()
	val codeName =
		if (token.contract.isEOS()) EOSCodeName.EOSIOToken.value
		else token.contract.contract.orEmpty()
	val startIndex = (currentMaxCount ?: 0) - DataValue.pageCount
	EOSTransactionTable.getRangeData(
		account,
		if (startIndex < 0) 0 else startIndex,
		currentMaxCount ?: 0,
		token.symbol.symbol,
		codeName
	) { localData ->
		val dao = EOSTransactionTable.dao
		if (detailView.asyncData?.isEmpty() == true) localData.map {
			TransactionListModel(it)
		}.generateBalanceList(token.contract) {
			it.updateHeaderData(false)
		}
		fun loadTargetRangeData(endID: Long, pageSize: Int) {
			// 拉取指定范围和数量的账单
			EOSAPI.getEOSTransactions(
				SharedChain.getEOSCurrent().chainID,
				account,
				pageSize,
				0L,
				endID,
				token.contract
			) { data, error ->
				if (data?.isNotEmpty() == true && error.isNone()) {
					data.mapIndexed { index, eosTransactionTable ->
						eosTransactionTable.apply { dataIndex = currentMaxCount.orZero() - (index + 1) }
					}.let {
						dao.insertAll(it)
					}
					flipPage(data.plus(localData), callback)
					currentMaxCount = currentMaxCount.orZero() - pageSize
				} else {
					callback()
				}
			}
		}
		when {
			// 本地指定范围的数据是空的条件判断
			localData.isEmpty() -> {
				// 准备 `MongoDB` 格式的 `EndID`
				loadTargetRangeData(
					// 本地无数据初次加载
					// 或分页数据本地不存在此范围片段, 向上获取指定 `ID`
					if (currentMaxCount == totalCount) 0L
					else dao.getDataByDataIndex(
						account.accountName,
						currentMaxCount.orZero() + 1,
						token.symbol.symbol,
						codeName
					)?.serverID ?: 0L,
					DataValue.pageCount
				)
			}
			// 防止天然数据不够一页的情况, 防止拉到最后一页数据不够一页数量的情况.
			localData.size < DataValue.pageCount
				&& localData.size != totalCount
				&& localData.minBy { it.dataIndex }?.dataIndex ?: 0 > 1 -> {
				val pageSize = DataValue.pageCount - localData.size
				loadTargetRangeData(
					// 本地片段存在不足的情况
					if (localData.maxBy { it.dataIndex }?.dataIndex.orZero() == currentMaxCount)
						localData.minBy { it.dataIndex }?.serverID ?: 0L
					else dao.getDataByDataIndex(
						account.accountName,
						currentMaxCount.orZero() + 1,
						token.symbol.symbol,
						codeName
					)?.serverID ?: 0L,
					pageSize
				)
			}
			// 本地有数据
			else -> {
				flipPage(localData, callback)
				currentMaxCount = localData.minBy { it.dataIndex }?.dataIndex.orZero() - 1
			}
		}
	}
}