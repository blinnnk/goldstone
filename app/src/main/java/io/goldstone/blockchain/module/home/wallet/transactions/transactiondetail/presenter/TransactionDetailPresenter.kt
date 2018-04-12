package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.text.format.DateUtils
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.toArrayList
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.crypto.toEthValue
import io.goldstone.blockchain.kernel.network.APIPath
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
  private var transactionHash = ""

  override fun updateData() {

    dataFromList?.apply {
      updateHeaderValue(count, targetAddress, symbol, false, isReceived)
      fragment.asyncData = generateModels(this)
    }

    data?.apply {
      transactionHash = taxHash
      count = CryptoUtils.toCountByDecimal(raw.value.toDouble(), token.decimals)
      fragment.asyncData = generateModels()
      updateHeaderValue(count, address, token.symbol, true)
      observerTransaction()
    }

  }

  private fun generateModels(
    receipt: Any? = null
  ): ArrayList<TransactionDetailModel> {

    var minerFee = ""
    var date = ""
    data?.apply {
      minerFee = (raw.gasLimit * raw.gasPrice).toDouble().toEthValue()
      date = DateUtils.formatDateTime(
        GoldStoneAPI.context, data!!.timestamp, DateUtils.FORMAT_SHOW_YEAR
      ) + " " + DateUtils.formatDateTime(
        GoldStoneAPI.context, data!!.timestamp * 1000, DateUtils.FORMAT_SHOW_TIME
      )
    }

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
          transactionHash,
          receipt.blockNumber.toBigDecimal(),
          date,
          "https://etherscan.io/account"
        )
      }

      else -> {
        arrayListOf(
          minerFee,
          "There isn't a memo",
          transactionHash,
          "waiting",
          date,
          "https://etherscan.io/account"
        )
      }
    }
    arrayListOf(
      "Miner Fee", "Memo", "Transaction Hash", "Block Number", "Transaction Date", "Open A Url"
    ).mapIndexed { index, it ->
      TransactionDetailModel(receiptData[index].toString(), it)
    }.let {
      return it.toArrayList()
    }
  }

  private fun updateHeaderValue(
    count: Double, address: String, symbol: String, isPending: Boolean, isReceive: Boolean = false) {
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
          println("checking ${it.hash}")
          it.hash == transactionHash
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
    updateHeaderValue(count, data!!.address, false, false)
    transactionObserver!!.unsubscribe()
    transactionObserver = null
    fragment.getTransactionFromChain()
  }

  private fun TransactionDetailFragment.getTransactionFromChain() {
    val receipt = web3j.ethGetTransactionReceipt(data!!.taxHash).sendAsync().get().transactionReceipt
    context?.runOnUiThread {
      println(receipt)
      asyncData?.clear()
      asyncData?.addAll(generateModels(receipt))
      recyclerView.adapter.notifyItemRangeChanged(1, 6)
    }
  }


}