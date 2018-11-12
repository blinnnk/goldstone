package io.goldstone.blockchain.module.common.tokenpayment.gasselection.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.cell.GraySquareCell
import io.goldstone.blockchain.common.component.title.ExplanationTitle
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.PrepareTransferText
import io.goldstone.blockchain.common.language.QAText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.safeShowError
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.WebUrl
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.model.MinerFeeType
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.GasSelectionPresenter
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 2018/5/16 3:53 PM
 * @author KaySaith
 */
class GasSelectionFragment : BaseFragment<GasSelectionPresenter>() {

	override val pageTitle: String = TokenDetailText.customGas
	private val footer by lazy { GasSelectionFooter(context!!) }
	private val spendingCell by lazy { GraySquareCell(context!!) }
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
							presenter.confirmTransfer { error ->
								resetMinerType()
								launchUI {
									if (error.hasError()) safeShowError(error)
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
							Bundle().apply {
								putString(ArgumentKey.webViewUrl, WebUrl.whatIsGas)
								putString(ArgumentKey.webViewName, QAText.whatIsGas)
							}
						)
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

	private fun resetMinerType() {
		MinerFeeType.Custom.value = 0
		presenter.currentMinerType = MinerFeeType.Recommend
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<TokenDetailOverlayFragment>()?.apply {
			presenter.popFragmentFrom<GasSelectionFragment>()
		}
	}
}