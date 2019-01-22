package io.goldstone.blinnnk.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import io.goldstone.blinnnk.common.sharedpreference.SharedAddress
import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.NetworkUtil
import io.goldstone.blinnnk.common.value.DataValue
import io.goldstone.blinnnk.crypto.eos.EOSCodeName
import io.goldstone.blinnnk.crypto.multichain.isEOS
import io.goldstone.blinnnk.kernel.commontable.EOSTransactionTable
import io.goldstone.blinnnk.kernel.network.eos.EOSAPI
import io.goldstone.blinnnk.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel

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
		fun updateHeaderDataOrElse(headerData: List<EOSTransactionTable>) {
			if (detailView.asyncData?.isEmpty() == true) headerData.map {
				TransactionListModel(it)
			}.generateBalanceList(token.contract) {
				it.updateHeaderData(false)
			}
		}
		fun loadRangeDataFromChain(endID: Long, pageSize: Int) {
			// 拉取指定范围和数量的账单
			if (NetworkUtil.hasNetwork()) EOSAPI.getEOSTransactions(
				SharedChain.getEOSCurrent().chainID,
				account,
				pageSize,
				0L,
				endID,
				token.contract
			) { data, error ->
				if (data?.isNotEmpty() == true && error.isNone()) {
					dao.insertAll(data.mapIndexed { index, eosTransactionTable ->
						eosTransactionTable.apply {
							dataIndex = currentMaxCount.orZero() - (index + 1)
						}
					})
					updateHeaderDataOrElse(data)
					flipPage(data, callback)
					currentMaxCount = currentMaxCount.orZero() - pageSize
				} else callback()
			}
		}
		when {
			// 本地指定范围的数据是空的条件判断
			localData.isEmpty() -> {
				// 准备 `MongoDB` 格式的 `EndID`
				loadRangeDataFromChain(
					// 本地无数据初次加载
					// 或分页数据本地不存在此范围片段, 向上获取指定 `ID`
					if (currentMaxCount == totalCount) 0L
					else dao.getDataByDataIndex(
						account.name,
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
				&& localData.minBy { it.dataIndex }?.dataIndex.isNotNull() -> {
				loadRangeDataFromChain(
					// 本地片段存在不足的情况
					if (localData.maxBy { it.dataIndex }?.dataIndex.orZero() == currentMaxCount)
						localData.minBy { it.dataIndex }?.serverID ?: 0L
					else dao.getTargetServerID(
						account.name,
						currentMaxCount.orZero() + 1,
						token.symbol.symbol,
						codeName
					).orElse(0L),
					DataValue.pageCount
				)
			}
			// 本地有数据
			else -> {
				updateHeaderDataOrElse(localData)
				flipPage(localData, callback)
				currentMaxCount = localData.minBy { it.dataIndex }?.dataIndex.orZero() - 1
			}
		}
	}
}

fun TokenDetailPresenter.getCountInfoFromChain() {
	// 创建的时候准备相关的账单数据, 服务本地网络混合分页的逻辑
	val account = SharedAddress.getCurrentEOSAccount()
	val chainID = SharedChain.getEOSCurrent().chainID
	val codeName = token.contract.contract
	EOSAPI.getTransactionCount(
		chainID,
		account,
		codeName,
		token.symbol
	) { count, error ->
		if (count.hasValue() && error.isNone()) {
			launchUI {
				totalCount = count
				currentMaxCount = count
				// 初次加载的时候, 这个逻辑会复用到监听转账的 Pending Data 的状态更改.
				// 当 `PendingData Observer` 调用这个方法的时候让数据重新加载显示, 来达到更新 `Pending Status` 的效果
				detailView.getDetailAdapter()?.dataSet?.clear()
				loadMore()
			}
		} else {
			detailView.showLoading(false)
			detailView.showBottomLoading(false)
		}
	}
}

fun TokenDetailPresenter.getCountInfoFromLocal() {
	// 创建的时候准备相关的账单数据, 服务本地网络混合分页的逻辑
	val account = SharedAddress.getCurrentEOSAccount()
	val chainID = SharedChain.getEOSCurrent().chainID
	EOSTransactionTable.getMaxDataIndexTable(
		account,
		token.contract,
		chainID
	) {
		launchUI {
			totalCount = it
			currentMaxCount = it
			// 初次加载的时候, 这个逻辑会复用到监听转账的 Pending Data 的状态更改.
			// 当 `PendingData Observer` 调用这个方法的时候让数据重新加载显示, 来达到更新 `Pending Status` 的效果
			detailView.getDetailAdapter()?.dataSet?.clear()
			// 初始化
			loadMore()
		}
	}
}