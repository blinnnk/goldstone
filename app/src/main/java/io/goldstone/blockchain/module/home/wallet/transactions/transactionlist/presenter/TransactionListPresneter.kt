package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.presenter

import android.support.v4.app.Fragment
import com.blinnnk.extension.*
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.toArrayList
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
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

  override fun updateData(asyncData: ArrayList<TransactionListModel>?) {
    TransactionTable.getAllTransactionsByAddress(WalletTable.currentWallet.address) {
      it.isEmpty().isTrue {
        fragment.getTransactionDataFromEtherScan()
      } otherwise {
        fragment.asyncData = it.map { TransactionListModel(it) }.toArrayList()
        System.out.println("has local data about transaction")
      }
    }
  }

  fun showTransactionDetail() {
    fragment.getParentFragment<TransactionFragment>()?.apply {
      presenter.showTargetFragment(true)
    }
  }

  private fun TransactionListFragment.getTransactionDataFromEtherScan() {
    // Show loading view
    getMainActivity()?.showLoadingView()
    // Get transaction data from `etherScan`
    GoldStoneAPI.getTransactionListByAddress(WalletTable.currentWallet.address) {
      doAsync {
        completeTransactionInfo {
          forEachIndexed { index, it ->
            it.to.isNotEmpty().isTrue { GoldStoneDataBase.database.transactionDao().insert(it) }
            if (index == lastIndex) {
              val transactions = filter { it.to.isNotEmpty() }.toArrayList()
              context?.runOnUiThread {
                getMainActivity()?.removeLoadingView()
                asyncData = transactions.map { TransactionListModel(it) }.toArrayList()
              }
            }
          }
        }
      }
    }
  }

  private fun ArrayList<TransactionTable>.completeTransactionInfo(hold: ArrayList<TransactionTable>.() -> Unit) {
    forEachIndexed { index, it ->
      CryptoUtils.isERC20Transfer(it) {
        // 解析 `input code` 获取 `ERC20` 接受 `address`, 及接受 `count`
        val transactionInfo = CryptoUtils.loadTransferInfoFromInputData(it.input)
        // 判断是否是接受交易
        val receiveStatus = WalletTable.currentWallet.address == transactionInfo?.address
        // 首先从本地数据库检索 `contract` 对应的 `symbol`
        DefaultTokenTable.getTokenByContractAddress(it.to) { tokenInfo ->
          val count = CryptoUtils.toCountByDecimal(
            transactionInfo?.count.orElse(0.0), tokenInfo?.decimals.orElse(0.0)
          )
          tokenInfo.isNull().isTrue {
            // 如果本地没有检索到 `contract` 对应的 `symbol` 则从链上查询
            GoldStoneEthCall.getTokenSymbol(it.to) { tokenSymbol ->
              it.apply {
                isReceive = receiveStatus
                isERC20 = true
                symbol = tokenSymbol
                value = count.toString()
                tokenReceiveAddress = transactionInfo?.address
              }

              if (index == lastIndex) {
                hold(this)
              }

            }
          } otherwise {
            it.apply {
              isReceive = receiveStatus
              isERC20 = true
              symbol = tokenInfo!!.symbol
              value = count.toString()
              tokenReceiveAddress = transactionInfo?.address
            }

            if (index == lastIndex) {
              hold(this)
            }
          }
        }
      }.isFalse {
        it.apply {
          isReceive = WalletTable.currentWallet.address == it.to
          symbol = "ETH"
          value = CryptoUtils.toCountByDecimal(it.value.toDouble(), 18.0).toString()
        }
        if (index == lastIndex) {
          hold(this)
        }
      }
    }
  }

}