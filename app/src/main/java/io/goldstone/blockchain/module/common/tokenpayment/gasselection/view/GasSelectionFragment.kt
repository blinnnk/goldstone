package io.goldstone.blockchain.module.common.tokenpayment.gasselection.view

import android.content.Context
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.GraySqualCell
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getDecimalCount
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.PrepareTransferText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.GasSelectionPresenter
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 2018/5/16 3:53 PM
 * @author KaySaith
 */

class GasSelectionFragment : BaseFragment<GasSelectionPresenter>() {

	private val footer by lazy {
		GasSelectionFooter(context!!)
	}
	private val spendingCell by lazy { GraySqualCell(context!!) }

	private lateinit var gasLayout: LinearLayout

	override val presenter = GasSelectionPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)

			spendingCell.apply {
				setTitle(PrepareTransferText.willSpending)
			}.into(this)

			spendingCell.setMargins<LinearLayout.LayoutParams> {
				topMargin = 30.uiPX()
				bottomMargin = 20.uiPX()
			}

			gasLayout = verticalLayout {
				gravity = Gravity.CENTER_HORIZONTAL
				lparams(matchParent, wrapContent)
				presenter.generateGasSelections(this)
			}

			footer.apply {
				getCustomButton {
					onClick {
						presenter.goToGasEditorFragment()
						preventDuplicateClicks()
					}
				}
				getConfirmButton {
					onClick {
						showLoadingStatus()
						confirmTransfer {
							showLoadingStatus(false)
						}
					}
				}
			}.into(this)
		}
	}

	fun clearGasLayout() {
		gasLayout.removeAllViews()
	}

	fun getGasLayout(): LinearLayout {
		return gasLayout
	}

	fun setSpendingValue(value: String) {
		spendingCell.setSubtitle(value)
	}

	private fun confirmTransfer(callback: () -> Unit) {
		val token = getParentFragment<TokenDetailOverlayFragment>()?.token
		// 如果输入的 `Decimal` 不合规就提示竞购并返回
		if (!presenter.getTransferCount().toString().checkDecimalIsvalid(token)) return
		// 检查网络并执行转账操作
		NetworkUtil.hasNetworkWithAlert(context) isTrue {
			MyTokenTable.getBalanceWithSymbol(
				token?.symbol!!, WalletTable.current.address, true
			) { balance ->
				context?.runOnUiThread {
					showAlertOrTransfer(balance, callback)
				}
			}
		}
	}

	private fun String.checkDecimalIsvalid(token: WalletDetailCellModel?): Boolean {
		return if (getDecimalCount() > token?.decimal.orElse(0.0)) {
			context?.alert(
				"The value's decimal you inputed is bigger than this currency token's decimal please re-input"
			)
			false
		} else {
			true
		}
	}

	private fun Context.showAlertOrTransfer(
		balance: Double,
		callback: () -> Unit
	) {
		if (presenter.getTransferCount() <= 0) {
			callback()
			alert("Please Enter Your Transfer Value")
		} else {
			if (balance > presenter.getTransferCount()) {
				footer.setCanUseStyle(true)
				showConfirmAttentionView(callback)
			} else {
				callback()
				alert("You haven't enough currency to transfer")
			}
		}
	}

	private fun showConfirmAttentionView(callback: () -> Unit) {
		context?.showAlertView(TransactionText.confirmTransaction,
			CommonText.enterPassword.toUpperCase(), true, {
				// 点击 `Alert` 取消按钮a
				footer.getConfirmButton { showLoadingStatus(false) }
			}) {
			presenter.transfer(
				it?.text.toString(), callback
			)
		}
	}

	override fun setBackEvent(
		activity: MainActivity,
		parent: Fragment?
	) {
		getParentFragment<TokenDetailOverlayFragment>()?.let {
			presenter.backEvent(it)
		}
	}

}