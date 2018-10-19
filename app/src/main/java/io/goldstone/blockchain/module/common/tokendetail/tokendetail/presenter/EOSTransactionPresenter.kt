package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orEmpty
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
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


/**
 * @author KaySaith
 * @date  2018/10/17
 */
private const val pageCount = 10

fun TokenDetailPresenter.flipEOSPageData(callback: () -> Unit = {}) {
	// 是否有合法的数据
	if (totalCount == null || currentMaxCount == null) {
		showBottomLoading(false)
		return
	} else if (currentMaxCount!! <= 0 || fragment.asyncData?.size == totalCount) {
		// 数据是否有效
		showBottomLoading(false)
		return
	} else when {
		token?.contract.isEOSSeries() -> {
			val account = SharedAddress.getCurrentEOSAccount()
			val codeName =
				if (token?.contract.isEOS()) EOSCodeName.EOSIOToken.value
				else token?.contract?.contract.orEmpty()
			doAsync {
				EOSTransactionTable.getRangeData(
					account,
					currentMaxCount!! - pageCount,
					currentMaxCount!!,
					token?.symbol.orEmpty(),
					codeName,
					false
				) { localData ->
					// 显示内存的数据后异步更新数据
					if (!fragment.asyncData.isNull() && fragment.asyncData!!.isEmpty()) localData.map {
						TransactionListModel(it)
					}.prepareTokenHistoryBalance(token?.contract!!, account.accountName) {
						it.updateChartAndHeaderData()
					}
					var endID = 0L
					var pageSize = pageCount
					fun loadTargetRangeData() {
						// 拉取指定范围和数量的账单
						EOSAPI.getEOSTransactions(
							SharedChain.getEOSCurrent(),
							account,
							pageSize,
							0L,
							endID,
							token?.contract.orEmpty()
						) { data, error ->
							if (!data.isNull() && error.isNone()) {
								if (data!!.isEmpty()) {
									fragment.context?.runOnUiThread { showBottomLoading(false) }
									return@getEOSTransactions
								}
								// 排序后插入数据库
								data.asSequence().sortedByDescending { it.serverID }.forEachIndexed { index, eosTransactionTable ->
									EOSTransactionTable.preventDuplicateInsert(account, eosTransactionTable.apply { dataIndex = currentMaxCount!! - index })
								}
								flipPage(data.plus(localData), callback)
								currentMaxCount = currentMaxCount!! - pageSize
							}
						}
					}

					when {
						// 本地指定范围的数据是空的条件判断
						localData.isEmpty() -> {
							// 准备 `MongoDB` 格式的 `EndID`
							endID =
								if (currentMaxCount == totalCount) 0L // 本地无数据初次加载
								// 分页数据本地不存在此范围片段, 向上获取指定 ID
								else GoldStoneDataBase.database.eosTransactionDao()
									.getDataByDataIndex(
										account.accountName,
										currentMaxCount!! + 1,
										token?.symbol.orEmpty(),
										codeName
									)?.serverID ?: 0L
							loadTargetRangeData()
						}
						localData.size < pageCount && localData.size < totalCount.orZero() -> {
							// 本地片段存在不足的情况
							endID = if (localData.maxBy { it.dataIndex }?.dataIndex.orZero() == currentMaxCount)
								localData.minBy { it.dataIndex }?.serverID ?: 0L
							else GoldStoneDataBase.database.eosTransactionDao().getDataByDataIndex(
								account.accountName,
								currentMaxCount!! + 1,
								token?.symbol.orEmpty(),
								codeName
							)?.serverID ?: 0L
							pageSize = pageCount - localData.size
							loadTargetRangeData()
						}
						// 本地有数据
						else -> {
							flipPage(localData, callback)
							currentMaxCount = localData.minBy { it.dataIndex }?.dataIndex.orZero() - 1
						}
					}
				}
			}
		}
		else -> showBottomLoading(false)
	}
}

@UiThread
private fun TokenDetailPresenter.flipPage(data: List<EOSTransactionTable>, callback: () -> Unit) {
	fragment.asyncData?.addAll(data.map { TransactionListModel(it) })
	fragment.getAdapter<TokenDetailAdapter>()?.dataSet = fragment.asyncData!!
	val totalCount = fragment.asyncData?.size.orZero()
	fragment.context?.runOnUiThread {
		allData = fragment.asyncData
		fragment.removeEmptyView()
		fragment.recyclerView.adapter?.apply {
			try {
				val startPosition = totalCount - data.size.orZero() + 1
				notifyItemRangeChanged(if (startPosition < 1) 1 else startPosition, totalCount)
			} catch (error: Exception) {
				notifyDataSetChanged()
			}
		}
		showBottomLoading(false)
		callback()
	}
}