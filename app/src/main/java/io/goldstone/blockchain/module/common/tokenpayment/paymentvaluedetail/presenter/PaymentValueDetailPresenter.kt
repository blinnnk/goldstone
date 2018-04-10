package io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.presenter

import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.crypto.getPrivateKey
import io.goldstone.blockchain.kernel.network.APIPath
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.model.PaymentValueDetailModel
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view.PaymentValueDetailFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.math.BigInteger

/**
 * @date 28/03/2018 12:23 PM
 * @author KaySaith
 */

class PaymentValueDetailPresenter(
  override val fragment: PaymentValueDetailFragment
) : BaseRecyclerPresenter<PaymentValueDetailFragment, PaymentValueDetailModel>() {

  fun beginTransfer(address: String) {
    transfer(address)
  }

  private fun transfer(toAddress: String) {
    fragment.context?.getPrivateKey(WalletTable.current.address, "125883Kay") { privateKey ->

      val web3j = Web3jFactory.build(HttpService(APIPath.ropstan))

      coroutinesTask({
        web3j.ethGasPrice().sendAsync().get().gasPrice

      }) { gasPrice ->

        GoldStoneAPI.getTransactionListByAddress(WalletTable.current.address) {

          val getNonce = first().nonce + 3
          val gasLimit = BigInteger.valueOf(4_300_000)

          System.out.println("hello nonce $getNonce")
          System.out.println("hello gasPrice $gasPrice")
          System.out.println("hello address $toAddress")
          System.out.println("hello limit $gasLimit")

          try {
            val nonce = BigInteger(getNonce)
            val value = Convert.toWei("0.1", Convert.Unit.ETHER).toBigInteger()
            val rawTransaction = RawTransaction.createEtherTransaction(
              nonce, gasPrice, gasLimit, toAddress, value
            )
            val credentials = Credentials.create(privateKey)
            val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
            val hexValue = Numeric.toHexString(signedMessage)

            GoldStoneEthCall.sendRawTransaction(hexValue) { taxHash ->
              System.out.println("hello$taxHash")
            }

          } catch (e: Exception) {
            e.printStackTrace()
          }
        }
      }
    }
  }


}