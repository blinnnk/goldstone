package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.presenter

import android.os.Bundle
import android.util.Log
import com.blinnnk.extension.*
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.toArrayList
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.kernel.commonmodel.TransactionDao
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable.Companion.getAllTransactionsByAddress
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable.Companion.getTransactionListModelsByAddress
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.view.TransactionListFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 24/03/2018 2:12 PM
 * @author KaySaith
 */

class TransactionListPresenter(
  override val fragment: TransactionListFragment
) : BaseRecyclerPresenter<TransactionListFragment, TransactionListModel>() {

  override fun updateData() {
    fragment.apply {
      getTransactionListModelsByAddress(WalletTable.current.address) { localData ->
        localData.isEmpty().isTrue {
          getMainActivity()?.getTransactionDataFromEtherScan { asyncData = it }
        } otherwise {
          // 本地可能存在 `pending` 状态的账目, 所以获取最近的 `blockNumber` 先剥离掉 `pending` 的类型
          val lastBlockNumber = localData.first { it.blockNumber.isNotEmpty() }.blockNumber + 1
          // 本地若有数据获取本地最近一条数据的 `BlockNumber` 作为 StartBlock 尝试拉取最新的数据
          getMainActivity()?.getTransactionDataFromEtherScan(lastBlockNumber) { newData ->
            // 如果梅拉去到直接更新本地数据
            newData.isEmpty().isTrue {
              asyncData = localData
            } otherwise {
              doAsync {
                // 拉取到新数据后检查是否包含本地已有的部分, 这种该情况会出现在, 本地转账后插入临时数据的条目。
                newData.forEach { data ->
                  localData.find {
                    it.transactionHash == data.transactionHash
                  }?.let {
                    TransactionTable.deleteByTaxHash(it.transactionHash)
                  }
                }
                // 数据清理干净后在主线程更新 `UI`
                context?.runOnUiThread {
                  // 拉取到后, 把最新获取的数据合并本地数据更新到界面
                  localData.addAll(0, newData)
                  asyncData = localData
                }

              }
              Log.d("DEBUG", "updated new transaction data")
            }
          }
        }
      }
    }
  }

  fun showTransactionDetail(model: TransactionListModel?) {
    fragment.getParentFragment<TransactionFragment>()?.apply {
      Bundle().apply {
        putSerializable(ArgumentKey.transactionFromList, model)
        presenter.showTargetFragment(true, this)
      }
    }
  }

  companion object {

    private fun completeTransactionInfo(
      data: ArrayList<TransactionTable>, hold: ArrayList<TransactionTable>.() -> Unit
    ) {
      data.forEachOrEnd { transaction, isEnd ->
        CryptoUtils.isERC20Transfer(transaction) {
          // 解析 `input code` 获取 `ERC20` 接受 `address`, 及接受 `count`
          val transactionInfo = CryptoUtils.loadTransferInfoFromInputData(transaction.input)
          // 判断是否是接收交易
          val receiveStatus = WalletTable.current.address == transactionInfo?.address
          // 首先从本地数据库检索 `contract` 对应的 `symbol`
          DefaultTokenTable.getTokenByContractAddress(transaction.to) { tokenInfo ->
            val count = CryptoUtils.toCountByDecimal(
              transactionInfo?.count.orElse(0.0), tokenInfo?.decimals.orElse(0.0)
            )
            tokenInfo.isNull().isTrue {
              // 如果本地没有检索到 `contract` 对应的 `symbol` 则从链上查询
              GoldStoneEthCall.getTokenSymbol(transaction.to) { tokenSymbol ->
                transaction.apply {
                  isReceive = receiveStatus
                  isERC20 = true
                  symbol = tokenSymbol
                  value = count.toString()
                  tokenReceiveAddress = transactionInfo?.address
                  recordOwnerAddress = WalletTable.current.address
                }
                if (isEnd) hold(data)
              }
            } otherwise {
              transaction.apply {
                isReceive = receiveStatus
                isERC20 = true
                symbol = tokenInfo!!.symbol
                value = count.toString()
                tokenReceiveAddress = transactionInfo?.address
                recordOwnerAddress = WalletTable.current.address
              }
              if (isEnd) hold(data)
            }
          }
        }.isFalse {
          transaction.apply {
            isReceive = WalletTable.current.address == transaction.to
            symbol = CryptoSymbol.eth
            value = CryptoUtils.toCountByDecimal(transaction.value.toDouble(), 18.0).toString()
            recordOwnerAddress = WalletTable.current.address
            tokenReceiveAddress = transaction.to
          }
          if (isEnd) hold(data)
        }
      }
    }

    // 默认拉取全部的 `EtherScan` 的交易数据
    private fun MainActivity.getTransactionDataFromEtherScan(
      startBlock: String = "0", hold: (ArrayList<TransactionListModel>) -> Unit
    ) {
      // Show loading view
      showLoadingView()
      // Get transaction data from `etherScan`
      GoldStoneAPI.getTransactionListByAddress(WalletTable.current.address, startBlock) {
        val chainData = this
        if (chainData.isEmpty()) {
          runOnUiThread {
            removeLoadingView()
            // 没有数据返回空数组
            hold(arrayListOf())
          }
          return@getTransactionListByAddress
        }
        // 因为进入这里之前外部已经更新了最近的 `BlockNumber`, 所以这里的数据可以直接理解为最新的本地没有的部分
        removeLoadingView()
        filterCompletedData(chainData, hold)
        Log.d("DEBUG", "update the new data from chain")
      }
    }

    fun updateTransactions(
      activity: MainActivity?,
      startBlock: String = "0",
      hold: (ArrayList<TransactionListModel>) -> Unit
    ) {
      activity?.getTransactionDataFromEtherScan(startBlock, hold)

    }

    private fun MainActivity.filterCompletedData(
      data: ArrayList<TransactionTable>, hold: (ArrayList<TransactionListModel>) -> Unit
    ) {
      // 把拉取到的数据加工数据格式并插入本地数据库
      completeTransactionInfo(data) {
        forEachOrEnd { it, isEnd ->
          it.to.isNotEmpty().isTrue {
            GoldStoneDataBase.database.transactionDao().insert(it)
          }
          if (isEnd) {
            val transactions = filter { it.to.isNotEmpty() }.toArrayList()
            GoldStoneAPI.context.runOnUiThread {
              removeLoadingView()
              hold(transactions.map { TransactionListModel(it) }.toArrayList())
            }
          }
        }
      }
    }
  }
}