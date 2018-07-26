package io.goldstone.blockchain.module.common.tokenpayment.gasselection.view

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.ExplanationTitle
import io.goldstone.blockchain.common.component.GraySqualCell
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.kernel.network.bitcoin.BTCJsonRPC
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.GasSelectionPresenter
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.confirmTransfer
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.transferBTC
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
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
	private lateinit var container: RelativeLayout
	override val presenter = GasSelectionPresenter(this)
	
	override fun AnkoContext<Fragment>.initView() {
		container = relativeLayout {
			lparams(matchParent, matchParent)
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
							if (presenter.isBTC()) {
								presenter.transferBTC {
									BTCJsonRPC.sendRawTransaction(
										Config.isTestEnvironment(),
										it.signedMessage
									) {
									}
								}
							} else {
								presenter.confirmTransfer(footer) {
									showLoadingStatus(false, Spectrum.white, CommonText.next)
								}
							}
						}
					}
				}.into(this)
				
				ExplanationTitle(context).apply {
					text = QAText.whatIsGas.setUnderline()
				}.click {
					getParentFragment<TokenDetailOverlayFragment> {
						presenter.showTargetFragment<WebViewFragment>(
							QAText.whatIsGas,
							TokenDetailText.customGas,
							Bundle().apply {
								putString(ArgumentKey.webViewUrl, WebUrl.whatIsGas)
							}
						)
						presenter.recoverHeader()
					}
				}.into(this)
			}
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
	
	fun showMaskView(isShow: Boolean = false) {
		if (isShow) {
			if (container.findViewById<View>(ElementID.mask).isNull()) {
				View(context).apply {
					id = ElementID.mask
					backgroundColor = Color.TRANSPARENT
					isClickable = true
					onClick {
						this@apply.context.alert("Confirming transfer now please wait a momnet")
					}
					layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
				}.into(container)
			}
		} else {
			// When transfer is end that recovery custom miner
			// gas price value and current miner type
			MinerFeeType.Custom.value = 0
			presenter.currentMinerType = MinerFeeType.Recommend.content
			container.findViewById<View>(ElementID.mask)?.let {
				container.removeView(it)
			}
		}
	}
	
	override fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
		getParentFragment<TokenDetailOverlayFragment>()?.let {
			presenter.backEvent(it)
		}
	}
}