package io.goldstone.blockchain.module.common.tokenpayment.gasselection.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.ExplanationTitle
import io.goldstone.blockchain.common.component.GraySqualCell
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokenpayment.gasselection.presenter.GasSelectionPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

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
						presenter.confirmTransfer(footer) {
							showLoadingStatus(false, Spectrum.white, CommonText.next)
						}
					}
				}
			}.into(this)
			
			ExplanationTitle(context).apply {
				text = QAText.whatIsGas.setUnderline()
			}.click {
				getParentFragment<WalletImportFragment> {
					presenter.showWebViewFragment(WebUrl.whatIsGas, QAText.whatIsGas)
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
	
	override fun setBackEvent(
		activity: MainActivity,
		parent: Fragment?
	) {
		getParentFragment<TokenDetailOverlayFragment>()?.let {
			presenter.backEvent(it)
		}
	}
}