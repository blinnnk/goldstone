package io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.presenter

import android.os.Bundle
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.blinnnk.util.coroutinesTask
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.crypto.*
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.APIPath
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.GasSelectionFragment
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.model.PaymentPrepareModel
import io.goldstone.blockchain.module.common.tokenpayment.paymentprepare.view.PaymentPrepareFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import java.math.BigInteger
import kotlin.math.max

/**
 * @date 2018/5/15 10:19 PM
 * @author KaySaith
 */

class PaymentPreparePresenter(
	override val fragment: PaymentPrepareFragment
) : BasePresenter<PaymentPrepareFragment>() {

	private val web3j = Web3jFactory.build(HttpService(APIPath.ropstan))
	private var currentToken: WalletDetailCellModel? = null
	private var currentNonce: BigInteger? = null

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		setSymbol()
	}

	fun goToGasEditorFragment(callback: () -> Unit) {
		val count = fragment.getTransferCount()
		if (count == 0.0) {
			fragment.context?.alert(TokenDetailText.setTransferCountAlert)
		} else {
			getPaymentPrepareModel(count, fragment.getMemoContent()) { model ->
				fragment.getParentFragment<TokenDetailOverlayFragment>()?.apply {
					presenter.showTargetFragment<GasSelectionFragment>(
						TokenDetailText.customGas, TokenDetailText.paymentValue, Bundle().apply {
							putSerializable(ArgumentKey.gasPrepareModel, model)
						})
					callback()
				}
			}
		}
	}

	/**
	 * 查询当前账户的可用 `nonce` 以及 `symbol` 的相关信息后, 生成 `Recommond` 的 `RawTransaction`
	 */
	private fun getPaymentPrepareModel(
		value: Double,
		memo: String,
		hold: (PaymentPrepareModel) -> Unit
	) {
		// 获取当前账户在链上的 `nonce`， 这个行为比较耗时所以把具体业务和获取 `nonce` 分隔开
		currentNonce.isNull() isTrue {
			GoldStoneAPI.getTransactionListByAddress {
				TransactionTable.getMyLatestNounce { localNounce ->
					val myLatestNonce = firstOrNull {
						it.fromAddress.equals(WalletTable.current.address, true)
					}?.nonce?.toLong()
					val chainNounce = if (myLatestNonce.isNull()) 0L
					else myLatestNonce!! + 1
					currentNonce = BigInteger.valueOf(
						max(chainNounce, if (localNounce.isNull()) 0 else localNounce!! + 1)
					)
					generateTransaction(fragment.address!!, value, memo, hold)
				}
			}
		} otherwise {
			generateTransaction(fragment.address!!, value, memo, hold)
		}
	}

	private fun generateTransaction(
		toAddress: String,
		value: Double,
		memo: String,
		hold: (PaymentPrepareModel) -> Unit
	) {
		val countWithDecimal: BigInteger
		val data: String
		val to: String
		// `ETH` 转账和 `Token` 转账需要准备不同的 `Transaction`
		if (currentToken?.symbol == CryptoSymbol.eth) {
			to = toAddress
			data = if (memo.isEmpty()) "" else memo.toHexCode() // Memo
			countWithDecimal = Convert.toWei(value.toString(), Convert.Unit.ETHER).toBigInteger()
		} else {
			to = currentToken!!.contract
			countWithDecimal =
				BigInteger.valueOf((value * Math.pow(10.0, currentToken!!.decimal)).toLong())
			data = SolidityCode.contractTransfer + // 方法
				toAddress.toDataStringFromAddress() + // 地址
				countWithDecimal.toDataString() + // 数量
				if (memo.isEmpty()) "" else memo.toHexCode() // Memo
		}

		GoldStoneEthCall.getTransactionExecutedValue(
			to, WalletTable.current.address, data) { limit ->
			hold(
				PaymentPrepareModel(
					currentNonce!!, limit, to, countWithDecimal, value, data, fragment.address!!,
					fragment.getMemoContent()
				)
			)
		}
	}

	private fun setSymbol() {
		fragment.getParentFragment<TokenDetailOverlayFragment> {
			currentToken = token
			fragment.setSymbolAndPrice(
				currentToken?.symbol.orEmpty(),
				currentToken?.price?.formatCurrency().orEmpty() + " " + GoldStoneApp.currencyCode
			)
		}
	}

	override fun onFragmentShowFromHidden() {
		fragment.getParentFragment<TokenDetailOverlayFragment> {
			overlayView.header.backButton.onClick {
				backEvent(this@getParentFragment)
			}
		}
	}

	fun backEvent(fragment: TokenDetailOverlayFragment) {
		fragment.apply {
			headerTitle = TokenDetailText.address
			presenter.popFragmentFrom<PaymentPrepareFragment>()
		}
	}

}