package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.orEmpty
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.multichain.isEOS
import io.goldstone.blockchain.crypto.multichain.isEOSSeries
import io.goldstone.blockchain.crypto.multichain.orEmpty
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailAdapter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.uiThread

/**
 * @author KaySaith
 * @date  2018/10/17
 */

fun TokenDetailPresenter.flipEOSPageData(@UiThread callback: () -> Unit) {
	// 是否有合法的数据
	if (totalCount == null || currentMaxCount == null) {
		callback()
	} else if (currentMaxCount!! <= 0 || fragment.asyncData?.size == totalCount) {
		callback()    // 数据是否有效
	} else when {
		token?.contract.isEOSSeries() -> doAsync {
			val account = SharedAddress.getCurrentEOSAccount()
			val codeName =
				if (token?.contract.isEOS()) EOSCodeName.EOSIOToken.value
				else token?.contract?.contract.orEmpty()
			val startIndex = currentMaxCount!! - DataValue.transactionPageCount
			EOSTransactionTable.getRangeData(
				account,
				if (startIndex < 0) 0 else startIndex,
				currentMaxCount!!,
				token?.symbol.orEmpty(),
				codeName,
				false
			) { localData ->
				// 显示内存的数据后异步更新数据
				if (fragment.asyncData?.isEmpty() == true) localData.map {
					TransactionListModel(it)
				}.prepareTokenHistoryBalance(token?.contract!!, account.accountName) {
					it.updateChartAndHeaderData()
				}
				fun loadTargetRangeData(endID: Long, pageSize: Int) {
					// 拉取指定范围和数量的账单
					EOSAPI.getEOSTransactions(
						SharedChain.getEOSCurrent().chainID,
						account,
						pageSize,
						0L,
						endID,
						token?.contract.orEmpty()
					) { data, error ->
						if (data != null && data.isNotEmpty() && error.isNone()) {
							data.forEachIndexed { index, eosTransactionTable ->
								EOSTransactionTable.preventDuplicateInsert(
									account,
									eosTransactionTable.apply {
										dataIndex = currentMaxCount!! - (index + 1)
									}
								)
							}
							flipPage(data.plus(localData), callback)
							currentMaxCount = currentMaxCount!! - pageSize
						} else uiThread {
							callback()
						}
					}
				}

				val transactionDao =
					GoldStoneDataBase.database.eosTransactionDao()
				when {
					// 本地指定范围的数据是空的条件判断
					localData.isEmpty() -> {
						// 准备 `MongoDB` 格式的 `EndID`
						loadTargetRangeData(
							// 本地无数据初次加载
							// 或分页数据本地不存在此范围片段, 向上获取指定 `ID`
							if (currentMaxCount == totalCount) 0L
							else transactionDao.getDataByDataIndex(
								account.accountName,
								currentMaxCount!! + 1,
								token?.symbol.orEmpty(),
								codeName
							)?.serverID ?: 0L,
							DataValue.transactionPageCount
						)
					}
					// 防止天然数据不够一页的情况, 防止拉到最后一页数据不够一页数量的情况.
					localData.size < DataValue.transactionPageCount &&
						localData.size != totalCount &&
						localData.minBy { it.dataIndex }?.dataIndex ?: 0 > 1 -> {
						val pageSize = DataValue.transactionPageCount - localData.size
						loadTargetRangeData(
							// 本地片段存在不足的情况
							if (localData.maxBy { it.dataIndex }?.dataIndex.orZero() == currentMaxCount)
								localData.minBy { it.dataIndex }?.serverID ?: 0L
							else transactionDao.getDataByDataIndex(
								account.accountName,
								currentMaxCount!! + 1,
								token?.symbol.orEmpty(),
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
		else -> showBottomLoading(false)
	}
}

private fun TokenDetailPresenter.flipPage(
	data: List<EOSTransactionTable>,
	@UiThread callback: () -> Unit
) {
	fragment.asyncData?.addAll(data.map { TransactionListModel(it) })
	fragment.getAdapter<TokenDetailAdapter>()?.dataSet = fragment.asyncData!!
	val totalCount = fragment.asyncData?.size.orZero()
	allData = fragment.asyncData
	fragment.context?.runOnUiThread {
		fragment.removeEmptyView()
		fragment.recyclerView.adapter?.apply {
			try {
				val startPosition = totalCount - data.size.orZero() + 1
				notifyItemRangeChanged(if (startPosition < 1) 1 else startPosition, totalCount)
			} catch (error: Exception) {
				notifyDataSetChanged()
			}
		}
		callback()
	}
}