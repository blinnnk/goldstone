package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.text.format.DateUtils
import android.util.Log
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.toArrayList
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.crypto.toEthValue
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.APIPath
import io.goldstone.blockchain.kernel.network.EtherScanApi
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailHeaderView
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import rx.Subscription

/**
 * @date 27/03/2018 3:27 AM
 * @author KaySaith
 * @description
 * 这个界面由两个入场场景公用, 分别是账单列表进入或转账完成进入, `fragment` 承担了两种身份
 * 固再次需要注意.
 */

class TransactionDetailPresenter(
  override val fragment: TransactionDetailFragment
) : BaseRecyclerPresenter<TransactionDetailFragment, TransactionDetailModel>() {

  private val data by lazy {
    fragment.arguments?.get(ArgumentKey.transactionDetail) as? ReceiptModel
  }
  private val dataFromList by lazy {
    fragment.arguments?.get(ArgumentKey.transactionFromList) as? TransactionListModel
  }
  private var count = 0.0
  private var currentHash = ""

  override fun updateData() {
    dataFromList?.apply {
      updateHeaderValue(count, targetAddress, symbol, isPending, isReceived)
      fragment.asyncData = generateModels(this)
      currentHash = transactionHash
      if (isPending) {
        // 异步从链上查一下这条 `taxHash` 是否有最新的状态变化
        doAsync {
          fragment.getTransactionFromChain(transactionHash) {
            updateHeaderValue(count, targetAddress, symbol, false)
          }
        }
      }
    }

    data?.apply {
      currentHash = taxHash
      count = CryptoUtils.toCountByDecimal(raw.value.toDouble(), token.decimals)
      fragment.asyncData = generateModels()
      observerTransaction()
      updateHeaderValue(count, address, token.symbol, true)
    }

  }

  private fun generateModels(
    receipt: Any? = null
  ): ArrayList<TransactionDetailModel> {

    val minerFee = if (data.isNull()) dataFromList?.minerFee
    else (data!!.raw.gasLimit * data!!.raw.gasPrice).toDouble().toEthValue()

    val date = if (data.isNull()) dataFromList?.date
    else DateUtils.formatDateTime(
      GoldStoneAPI.context, data!!.timestamp, DateUtils.FORMAT_SHOW_YEAR
    ) + " " + DateUtils.formatDateTime(
      GoldStoneAPI.context, data!!.timestamp * 1000, DateUtils.FORMAT_SHOW_TIME
    )

    val receiptData = when (receipt) {
      is TransactionListModel -> {
        arrayListOf(
          receipt.minerFee,
          receipt.memo,
          receipt.transactionHash,
          receipt.blockNumber,
          receipt.date,
          receipt.url
        )
      }

      is TransactionReceipt -> {
        arrayListOf(
          minerFee,
          "There isn't a memo",
          currentHash,
          receipt.blockNumber.toBigDecimal(),
          date,
          EtherScanApi.transactionsByHash(currentHash)
        )
      }

      else -> {
        arrayListOf(
          minerFee,
          "There isn't a memo",
          currentHash,
          "waiting",
          date,
          EtherScanApi.transactionsByHash(currentHash)
        )
      }
    }
    arrayListOf(
      TransactionText.minerFee,
      TransactionText.memo,
      TransactionText.transactionHash,
      TransactionText.blockNumber,
      TransactionText.transactionDate,
      TransactionText.url
    ).mapIndexed { index, it ->
      TransactionDetailModel(receiptData[index].toString(), it)
    }.let {
      return it.toArrayList()
    }
  }

  private fun updateHeaderValue(
    count: Double, address: String, symbol: String, isPending: Boolean, isReceive: Boolean = false
  ) {
    fragment.recyclerView.getItemViewAtAdapterPosition<TransactionDetailHeaderView>(0) {
      setIconStyle(count, address, symbol, isReceive, isPending)
    }
  }

  /** ———————————— 这里是从转账完成后跳入的账单详情界面用到的数据 ————————————*/

  private val web3j = Web3jFactory.build(HttpService(APIPath.ropstan))
  private var transactionObserver: Subscription? = null

  private fun observerTransaction() {
    doAsync {
      try {
        // 开启监听交易是否完成
        transactionObserver = web3j.transactionObservable().filter {
          Log.d("DEBUG", it.hash)
          it.hash == currentHash
        }.subscribe {
          println("succeed")
          onTransactionSucceed()
        }
      } catch (error: Exception) {
        println(error)
      }
    }
  }

  /**
   * 当 `Transaction` 监听到自身发起的交易的时候执行这个函数, 关闭监听以及执行动作
   */
  private fun onTransactionSucceed() {
    data?.apply {
      updateHeaderValue(count, address, token.symbol, false, false)
      transactionObserver!!.unsubscribe()
      transactionObserver = null
      fragment.getTransactionFromChain(taxHash)
    }
  }

  private fun TransactionDetailFragment.getTransactionFromChain(taxHash: String, callback: () -> Unit = {}) {
    web3j.ethGetTransactionReceipt(taxHash).sendAsync().get().transactionReceipt?.let {
      context?.runOnUiThread {
        println(it)
        asyncData?.clear()
        asyncData?.addAll(generateModels(it))
        recyclerView.adapter.notifyItemRangeChanged(1, 6)
        callback()
      }
      // 成功获取数据后在异步线程更新数据库记录
      updateDataInDatabase(it)
    }
  }

  private fun updateDataInDatabase(data: TransactionReceipt) {
    GoldStoneDataBase.database.transactionDao().apply {
      getTransactionsByTaxHash(data.transactionHash)?.let {
        update(it.apply {
          blockNumber = data.blockNumber.toString()
          isPending = false
          hasError = "0"
          txreceipt_status = "1"
        })
      }
    }
  }

}