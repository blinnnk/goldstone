package io.goldstone.blinnnk.module.common.tokenpayment.gasselection.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blinnnk.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blinnnk.common.base.gsfragment.GSFragment
import io.goldstone.blinnnk.common.component.button.RoundButton
import io.goldstone.blinnnk.common.component.cell.GraySquareCell
import io.goldstone.blinnnk.common.component.title.ExplanationTitle
import io.goldstone.blinnnk.common.language.PrepareTransferText
import io.goldstone.blinnnk.common.language.QAText
import io.goldstone.blinnnk.common.language.TokenDetailText
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.ErrorDisplayManager
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.common.value.ContainerID
import io.goldstone.blinnnk.common.value.WebUrl
import io.goldstone.blinnnk.crypto.multichain.getChainType
import io.goldstone.blinnnk.crypto.multichain.isBTCSeries
import io.goldstone.blinnnk.crypto.multichain.orEmpty
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blinnnk.module.common.tokenpayment.gaseditor.presenter.GasFee
import io.goldstone.blinnnk.module.common.tokenpayment.gaseditor.view.GasEditorFragment
import io.goldstone.blinnnk.module.common.tokenpayment.gasselection.contract.GasSelectionContract
import io.goldstone.blinnnk.module.common.tokenpayment.gasselection.presenter.GasSelectionPresenter
import io.goldstone.blinnnk.module.common.tokenpayment.paymentdetail.model.PaymentBTCSeriesModel
import io.goldstone.blinnnk.module.common.tokenpayment.paymentdetail.model.PaymentDetailModel
import io.goldstone.blinnnk.module.common.tokenpayment.paymentdetail.presenter.PaymentDetailPresenter
import io.goldstone.blinnnk.module.common.tokenpayment.paymentdetail.presenter.PrivatekeyActionType
import io.goldstone.blinnnk.module.common.webview.view.WebViewFragment
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import io.goldstone.blinnnk.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import io.goldstone.blinnnk.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent
import java.math.BigInteger

/**
 * @date 2018/5/16 3:53 PM
 * @author KaySaith
 */
@Suppress("UNCHECKED_CAST")
class GasSelectionFragment : GSFragment(), GasSelectionContract.GSView {

	override val pageTitle: String = TokenDetailText.customGas
	override lateinit var presenter: GasSelectionContract.GSPresenter
	private val overlayFragment by lazy {
		parentFragment as? TokenDetailOverlayFragment
	}

	private val paymentModel by lazy {
		arguments?.getSerializable(ArgumentKey.btcSeriesPrepareModel)
			?: arguments?.getSerializable(ArgumentKey.gasPrepareModel)
	}

	private lateinit var footer: GasSelectionFooter
	private lateinit var spendingCell: GraySquareCell
	private lateinit var gasLayout: LinearLayout
	private lateinit var wrapper: RelativeLayout
	private val token by lazy {
		(parentFragment as? TokenDetailOverlayFragment)?.token
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		token?.let {
			presenter = GasSelectionPresenter(it, this)
			presenter.start()
		}
	}

	override fun showLoading(status: Boolean) {}
	override fun showError(error: Throwable) = ErrorDisplayManager(error).show(context)
	override fun getCustomFee(): GasFee {
		return arguments?.getSerializable(ArgumentKey.gasEditor) as? GasFee
			?: presenter.currentFee
	}

	override fun clearGasLayout() = gasLayout.removeAllViews()
	override fun getMemo(): String = (paymentModel as? PaymentDetailModel)?.memo.orEmpty()

	override fun getTransferCount(): BigInteger? {
		return when {
			token?.contract.isBTCSeries() ->
				(paymentModel as? PaymentBTCSeriesModel)?.value?.toBigInteger()
			else -> (paymentModel as? PaymentDetailModel)?.amount
		}
	}

	override fun getGasLimit(): Long {
		return when {
			token?.contract.isBTCSeries() ->
				(paymentModel as? PaymentBTCSeriesModel)?.signedMessageSize ?: 226
			else -> (paymentModel as? PaymentDetailModel)?.gasLimit?.toLong() ?: 0
		}
	}

	override fun getGasLayout() = gasLayout
	override fun showSpendingValue(value: String) = spendingCell.setSubtitle(value)

	private fun goToGasEditorFragment() {
		overlayFragment?.apply {
			presenter.showTargetFragment<GasEditorFragment>(
				Bundle().apply {
					putLong(ArgumentKey.gasSize, getGasLimit())
					putBoolean(ArgumentKey.isBTCSeries, token?.contract.isBTCSeries())
				}
			)
		}
	}

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		if (!isHidden) {
			overlayFragment?.apply {
				showBackButton(true) {
					presenter.popFragmentFrom<GasSelectionFragment>()
				}
			}
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		overlayFragment?.presenter?.popFragmentFrom<GasSelectionFragment>()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
			wrapper = relativeLayout {
				lparams(matchParent, matchParent)
				verticalLayout {
					gravity = Gravity.CENTER_HORIZONTAL
					lparams(matchParent, matchParent)
					spendingCell = GraySquareCell(context)
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
					}
					footer = GasSelectionFooter(context)
					footer.apply {
						getCustomButton {
							onClick {
								goToGasEditorFragment()
								preventDuplicateClicks()
							}
						}
						getConfirmButton {
							onClick {
								showLoadingStatus()
								transferEvent()
							}
						}
					}.into(this)
					ExplanationTitle(context).apply {
						text = QAText.whatIsGas.setUnderline()
					}.click {
						overlayFragment?.apply {
							presenter.showTargetFragment<WebViewFragment>(
								Bundle().apply {
									putString(ArgumentKey.webViewUrl, WebUrl.whatIsGas)
									putString(ArgumentKey.webViewName, QAText.whatIsGas)
								}
							)
						}
					}.into(this)
				}
			}
		}.view
	}

	private fun RoundButton.transferEvent() {
		// Check balance, input value decimal is correct value first
		presenter.checkIsValidTransfer { error ->
			launchUI {
				if (error.isNone()) {
					PaymentDetailPresenter.getPrivatekey(
						context!!,
						token?.contract.getChainType(),
						PrivatekeyActionType.Transfer,
						cancelEvent = {
							showLoadingStatus(false)
						}
					) { privateKey, privateKeyError ->
						if (privateKey.isNotNull() && privateKeyError.isNone()) {
							presenter.transfer(
								token?.contract.orEmpty(),
								privateKey,
								paymentModel!!,
								presenter.currentFee
							) { receiptModel, error ->
								// get response model or show error
								launchUI {
									if (receiptModel.isNotNull() && error.isNone()) {
										goToTransactionDetailFragment(
											overlayFragment,
											this@GasSelectionFragment,
											receiptModel
										)
									} else {
										// 用户取消输入密码会返回 `model null`  和 `error none` 所以不用提示
										if (error.hasError()) showError(error)
										showLoadingStatus(false)
									}
								}
							}
						} else {
							if (privateKeyError.hasError()) showError(privateKeyError)
							launchUI {
								showLoadingStatus(false)
							}
						}
					}
				} else {
					showError(error)
					launchUI {
						showLoadingStatus(false)
					}
				}
			}
		}
	}

	/**
	 * 转账开始后跳转到转账监听界面
	 */
	companion object {
		fun goToTransactionDetailFragment(
			overlayFragment: BaseOverlayFragment<*>?,
			current: Fragment,
			receiptModel: ReceiptModel
		) {
			overlayFragment?.apply {
				// 准备跳转到下一个界面
				// 如果有键盘收起键盘
				activity?.apply { SoftKeyboard.hide(this) }
				removeChildFragment(current)
				addFragmentAndSetArgument<TransactionDetailFragment>(ContainerID.content) {
					putSerializable(ArgumentKey.transactionDetail, receiptModel)
				}
				showCloseButton(true) { presenter.removeSelfFromActivity() }
			}
		}
	}
}