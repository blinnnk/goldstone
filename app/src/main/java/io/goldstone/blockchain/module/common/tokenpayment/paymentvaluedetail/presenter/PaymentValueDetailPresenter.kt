package io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.presenter

import com.blinnnk.extension.*
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.toArrayList
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.crypto.getPrivateKey
import io.goldstone.blockchain.crypto.scaleToGwei
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.APIPath
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.model.PaymentValueDetailModel
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view.PaymentValueDetailCell
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view.PaymentValueDetailFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view.PaymentValueDetailHeaderView
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import rx.Subscription
import java.math.BigInteger

/**
 * @date 28/03/2018 12:23 PM
 * @author KaySaith
 */

class PaymentValueDetailPresenter(
  override val fragment: PaymentValueDetailFragment
) : BaseRecyclerPresenter<PaymentValueDetailFragment, PaymentValueDetailModel>() {

  private var transactionObserver: Subscription? = null
  private var currentNonce: BigInteger? = null

  private val web3j = Web3jFactory.build(HttpService(APIPath.ropstan))
  private val defaultGasPrices by lazy {
    arrayListOf(
      BigInteger.valueOf(MinerFeeType.Cheap.value.scaleToGwei().toLong()), // cheap
      BigInteger.valueOf(MinerFeeType.Fast.value.scaleToGwei().toLong()), // fast
      BigInteger.valueOf(MinerFeeType.Recommend.value.scaleToGwei().toLong()) // recommend
    )
  }

  private fun PaymentValueDetailCell.event() {
    fragment.asyncData?.forEachOrEnd { item, isEnd ->
      item.isSelected = item.type == model.type
      if (isEnd) fragment.recyclerView.adapter.notifyDataSetChanged()
    }
  }

  override fun updateData() {
    fragment.apply {
      // 数据在异步计算, 先生成空的数据占位, 避免页面抖动
      generateEmptyData()
      // 计算三种 `Gas` 设置的值, 初始默认的 `count` 设定为 `0`
      prepareRawTransactionByGasPrices(0.0) {
        context?.runOnUiThread {
          fragment.asyncData?.clear()
          fragment.asyncData?.addAll(generateModels(it, defaultGasPrices))
          fragment.recyclerView.adapter.notifyItemRangeChanged(1, 3)
        }
      }
    }
  }

  fun setCellClickEvent(cell: PaymentValueDetailCell) {
    cell.apply {
      onClick {
        event()
        preventDuplicateClicks()
      }
    }
    cell.radioButton.apply {
      onClick {
        cell.event()
        preventDuplicateClicks()
      }
    }
  }

  fun updateHeaderValue(header: PaymentValueDetailHeaderView) {
    DefaultTokenTable.getTokenBySymbol(fragment.symbol!!) { token ->
      header.apply {
        inputTextListener { count ->
          count.isNotEmpty().isTrue {
            updateCurrencyValue(count.toDouble() * token.price)
            // 根据数量更新 `Transaction`
            updateTransactionAndAdapter(count.toDouble())
          } otherwise {
            updateCurrencyValue(0.0)
            updateTransactionAndAdapter(0.0)
          }
        }
      }
    }
  }

  fun transfer(type: String, password: String) {
    doAsync {
      // 获取当前账户的私钥
      fragment.context?.getPrivateKey(WalletTable.current.address, password) { privateKey ->
        val priceAscRaw = fragment.asyncData!!.sortedBy { it.rawTransaction?.gasPrice }
        val raw = when (type) {
          MinerFeeType.Cheap.content -> priceAscRaw[0].rawTransaction
          MinerFeeType.Fast.content -> priceAscRaw[2].rawTransaction
          else -> priceAscRaw[1].rawTransaction
        }
        val credentials = Credentials.create(privateKey)
        val signedMessage = TransactionEncoder.signMessage(raw, credentials)
        val hexValue = Numeric.toHexString(signedMessage)
        // 发起 `sendRawTransaction` 请求
        GoldStoneEthCall.sendRawTransaction(hexValue) { taxHash ->
          System.out.println(taxHash)
          // 开启监听交易是否完成
          transactionObserver = web3j.transactionObservable().filter {
            it.hash == taxHash
          }.subscribe {
            System.out.println("succeed")
            onTransactionFinished(taxHash)
          }
        }
      }
    }
  }

  private fun updateTransactionAndAdapter(value: Double) {
    prepareRawTransactionByGasPrices(value) {
      fragment.asyncData?.clear()
      fragment.asyncData?.addAll(generateModels(it, defaultGasPrices))
      fragment.recyclerView.adapter.notifyItemRangeChanged(1, 3)
    }
  }

  private fun onTransactionFinished(taxHash: String) {
    transactionObserver!!.unsubscribe()
    // 主线程没变化需要排查
    fragment.context?.runOnUiThread {
      // TODO 整理 Model
      val model = TransactionListModel(web3j.ethGetTransactionByHash(taxHash).sendAsync().get().transaction, fragment.symbol!!)
      goToTransactionDetailFragment(model)
    }
  }

  private fun goToTransactionDetailFragment(model: TransactionListModel) {
    fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
      hideChildFragment(fragment)
      addFragmentAndSetArgument<TransactionDetailFragment>(ContainerID.content) {
        putSerializable(ArgumentKey.transactionDetail, model)
      }
      overlayView.header.apply {
        backButton.onClick {
          headerTitle = TokenDetailText.transferDetail
          presenter.popFragmentFrom<TransactionDetailFragment>()
          setHeightMatchParent()
          showCloseButton(false)
        }
      }
      headerTitle = TokenDetailText.transferDetail
    }
  }

  private fun prepareRawTransactionByGasPrices(
    value: Double, hold: (ArrayList<RawTransaction>) -> Unit
  ) {
    // 获取当前账户在链上的 `nonce`， 这个行为比较耗时所以把具体业务和获取 `nonce` 分隔开
    currentNonce.isNull().isTrue {
      GoldStoneAPI.getTransactionListByAddress(WalletTable.current.address) {
        currentNonce = BigInteger.valueOf(first().nonce.toLong() + 1)
        generateTransaction(fragment.address!!, value, hold)
      }
    } otherwise {
      generateTransaction(fragment.address!!, value, hold)
    }
  }

  private fun generateTransaction(
    toAddress: String, value: Double, hold: (ArrayList<RawTransaction>) -> Unit
  ) {
    val count = Convert.toWei(value.toString(), Convert.Unit.ETHER).toBigInteger()
    // 这个 `Transaction` 是用来测量估算可能要用的 `gasLimit` 不是用来转账用的.
    val transaction = Transaction.createEtherTransaction(
      WalletTable.current.address,
      currentNonce,
      BigInteger.valueOf(0),
      BigInteger.valueOf(0),
      toAddress,
      count
    )
    // 因为获取估算的 `gasAmountUsed` 需要在异步和 `链` 交互, 固这里在协程中解决
    defaultGasPrices.map { price ->
      // 测量 `Transaction` 得出 `GasLimit`
      val gasLimit = if (fragment.symbol!! == CryptoSymbol.eth) BigInteger.valueOf(21000)
      else web3j.ethEstimateGas(transaction).sendAsync().get().amountUsed
      // 生成 `RawTransaction` 对象
      RawTransaction.createEtherTransaction(
        currentNonce, price, gasLimit, toAddress, count
      )
    }.let {
      hold(it.toArrayList())
    }
  }

  private fun generateModels(
    rawTransaction: ArrayList<RawTransaction>, gasPrice: ArrayList<BigInteger>
  ) = rawTransaction.mapIndexed { index, it ->
    PaymentValueDetailModel(gasPrice[index].toDouble(), it)
  }.toArrayList()

  private fun generateEmptyData() {
    fragment.asyncData =
      arrayListOf(PaymentValueDetailModel(), PaymentValueDetailModel(), PaymentValueDetailModel())
  }

}