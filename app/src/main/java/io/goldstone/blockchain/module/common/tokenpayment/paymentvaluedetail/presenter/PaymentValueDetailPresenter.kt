package io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.presenter

import com.blinnnk.extension.*
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.toArrayList
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.crypto.*
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
  private var currentToken: DefaultTokenTable? = null

  private val web3j = Web3jFactory.build(HttpService(APIPath.ropstan))
  private val defaultGasPrices by lazy {
    arrayListOf(
      BigInteger.valueOf(MinerFeeType.Cheap.value.scaleToGwei().toLong()), // cheap
      BigInteger.valueOf(MinerFeeType.Fast.value.scaleToGwei().toLong()), // fast
      BigInteger.valueOf(MinerFeeType.Recommend.value.scaleToGwei().toLong()) // recommend
    )
  }

  override fun updateData() {
    fragment.apply {
      // 数据在异步计算, 先生成空的数据占位, 避免页面抖动
      generateEmptyData()
      // 计算三种 `Gas` 设置的值, 初始默认的 `count` 设定为 `0`
      prepareRawTransactionByGasPrices(0.0000001) {
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
        val priceAscRaw
          = fragment.asyncData!!.sortedBy { it.rawTransaction?.gasPrice }

        val raw = when (type) {
          MinerFeeType.Cheap.content -> priceAscRaw[0].rawTransaction
          MinerFeeType.Fast.content -> priceAscRaw[2].rawTransaction
          else -> priceAscRaw[1].rawTransaction
        }
        // 准备秘钥格式
        val credentials = Credentials.create(privateKey)
        // 生成签名文件
        val signedMessage = TransactionEncoder.signMessage(raw, credentials)
        // 生成签名哈希值
        val hexValue = Numeric.toHexString(signedMessage)
        // 发起 `sendRawTransaction` 请求
        GoldStoneEthCall.sendRawTransaction(hexValue) { taxHash ->
          System.out.println(taxHash)
          // 开启监听交易是否完成
          transactionObserver = web3j.transactionObservable().filter {
            System.out.println("checking ${ it.hash }")
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
      val model = TransactionListModel(
        web3j.ethGetTransactionByHash(taxHash).sendAsync().get().transaction,
        fragment.symbol!!
      )
      goToTransactionDetailFragment(model)
    }
  }

  private fun goToTransactionDetailFragment(model: TransactionListModel) {
    // 准备跳转到下一个界面
    fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
      // 如果有键盘收起键盘
      activity?.apply { SoftKeyboard.hide(this) }
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

  /**
   * 查询当前
   */
  private fun prepareRawTransactionByGasPrices(
    value: Double, hold: (ArrayList<RawTransaction>) -> Unit
  ) {
    // 获取当前账户在链上的 `nonce`， 这个行为比较耗时所以把具体业务和获取 `nonce` 分隔开
    currentNonce.isNull().isTrue {
      GoldStoneAPI.getTransactionListByAddress(WalletTable.current.address) {
        val myLatestNonce
          = first { it.fromAddress == WalletTable.current.address }.nonce.toLong()
        getTokenBySymbol { token ->
          currentNonce = BigInteger.valueOf(myLatestNonce + 1)
          generateTransaction(fragment.address!!, value, token, hold)
        }
      }
    } otherwise {
      generateTransaction(fragment.address!!, value, currentToken!!, hold)
    }
  }

  private fun getTokenBySymbol(hold: (DefaultTokenTable) -> Unit) {
    currentToken.isNotNull {
      hold(currentToken!!)
    } otherwise {
      DefaultTokenTable.getTokenBySymbol(fragment.symbol!!) {
        currentToken = it
        hold(it)
      }
    }
  }

  private fun generateTransaction(
    toAddress: String, value: Double, token: DefaultTokenTable?, hold: (ArrayList<RawTransaction>) -> Unit
  ) {

    val count: BigInteger
    val data: String
    val to: String
    // `ETH` 转账和 `Token` 转账需要准备不同的 `Transaction`
    if (fragment.symbol == CryptoSymbol.eth) {
      to = toAddress
      data = ""
      count = Convert.toWei(value.toString(), Convert.Unit.ETHER).toBigInteger()
    } else {
      to = token!!.contract
      count = BigInteger.valueOf((value * Math.pow(10.0, token.decimals)).toLong())
      data = SolidityCode.contractTransfer + toAddress.toDataStringFromAddress() + count.toDataString()
    }

    // 这个 `Transaction` 是用来测量估算可能要用的 `gasLimit` 不是用来转账用的.
    val transaction = Transaction(
      WalletTable.current.address,
      currentNonce,
      BigInteger.valueOf(0),
      BigInteger.valueOf(0),
      to,
      count,
      data
    )

    // 测量 `Transaction` 得出 `GasLimit`
    val gasLimit = if (fragment.symbol == CryptoSymbol.eth) BigInteger.valueOf(21000)
    else web3j.ethEstimateGas(transaction).sendAsync().get().amountUsed
    // 因为获取估算的 `gasAmountUsed` 需要在异步和 `链` 交互, 固这里在协程中解决
    defaultGasPrices.map { price ->
      // 生成 `RawTransaction` 对象
      RawTransaction.createTransaction(currentNonce, price, gasLimit, to, count, data)
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

  // 更新 `RadioBox` 选中的状态
  private fun PaymentValueDetailCell.event() {
    fragment.asyncData?.forEachOrEnd { item, isEnd ->
      item.isSelected = item.type == model.type
      if (isEnd) fragment.recyclerView.adapter.notifyDataSetChanged()
    }
  }

}