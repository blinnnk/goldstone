package io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view

import android.content.Context
import android.os.Bundle
import android.view.View
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getDecimalCount
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.model.PaymentValueDetailModel
import io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.presenter.PaymentValueDetailPresenter
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 28/03/2018 12:23 PM
 * @author KaySaith
 */

class PaymentValueDetailFragment : BaseRecyclerFragment<PaymentValueDetailPresenter, PaymentValueDetailModel>() {

	val address by lazy { arguments?.getString(ArgumentKey.paymentAddress) }
	val token by lazy {
		arguments?.get(ArgumentKey.paymentSymbol) as? WalletDetailCellModel
	}
	private var transferCount = 0.0
	private var footer: PaymentValueDetailFooter? = null

	override val presenter = PaymentValueDetailPresenter(this)

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<PaymentValueDetailModel>?
	) {
		recyclerView.adapter = PaymentValueDetailAdapter(asyncData.orEmptyArray()) {
			presenter.setCellClickEvent(this)
		}
	}

	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(
			view,
			savedInstanceState
		)

		recyclerView.getItemAtAdapterPosition<PaymentValueDetailHeaderView>(0) {
			it?.let { header ->
				header.setInputFocus()
				address?.apply {
					header.showTargetAddress(this)
				}
				presenter.updateHeaderValue(header)
				header.inputTextListener {
					it.isNotEmpty() isTrue {
						presenter.hasCalculated = false
						transferCount = it.toDouble()
						footer?.setCanUseStyle(true)
					} otherwise {
						transferCount = 0.0
						footer?.setCanUseStyle(false)
					}
				}
				header.setHeaderSymbol(token?.symbol.orEmpty())
			}
		}

		recyclerView.getItemAtAdapterPosition<PaymentValueDetailFooter>(asyncData?.size.orZero() + 1) { footer ->
			this.footer = footer
			footer?.getConfirmButton {
				onClick {
					isClickable = false
					confirmTransfer {
						isClickable = true
					}
				}
			}
			footer?.customGasEvent = showCustomGasFragment()
		}
	}

	fun getTransferCount(): Double {
		return transferCount
	}

	private fun showCustomGasFragment(): Runnable {
		return Runnable {
			presenter.goToGasEditorFragment()
		}
	}

	private fun confirmTransfer(callback: () -> Unit) {
		// 如果输入的 `Decimal` 不合规就提示竞购并返回
		if (!getTransferCount().toString().checkDecimalIsvalid())
			return
		// 检查网络并执行转账操作
		NetworkUtil.hasNetworkWithAlert(context) isTrue {
			MyTokenTable.getBalanceWithSymbol(
				token?.symbol!!,
				WalletTable.current.address,
				true
			) { balance ->
				context?.runOnUiThread {
					showAlertOrTransfer(balance, callback)
				}
			}
		}
	}

	private fun String.checkDecimalIsvalid(): Boolean {
		return if (getDecimalCount() > token?.decimal.orElse(0.0)) {
			context?.alert("The value's decimal you inputed is bigger than this currency token's decimal please re-input")
			false
		} else {
			true
		}
	}

	private fun Context.showAlertOrTransfer(
		balance: Double,
		callback: () -> Unit
	) {
		if (presenter.hasCalculated) {
			if (transferCount <= 0) {
				alert("Please Enter Your Transfer Value")
			} else {
				if (balance > transferCount) {
					footer?.setCanUseStyle(true)
					showConfirmAttentionView(callback)
				} else alert("You haven't enough currency to transfer")
			}
		} else {
			alert("Calculating Now Please Wait")
		}
	}

	private fun showConfirmAttentionView(callback: () -> Unit) {
		context?.showAlertView(
			TransactionText.confirmTransaction,
			CommonText.enterPassword.toUpperCase()
		) {
			presenter.transfer(it?.text.toString(), callback)
		}
	}

}