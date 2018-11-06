package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramstatistics.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.view.GrayCardView
import io.goldstone.blockchain.common.component.ProgressView
import io.goldstone.blockchain.common.component.cell.GraySquareCell
import io.goldstone.blockchain.common.component.title.SessionTitleView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramstatistics.presenter.RAMStatisticsPresenter
import org.jetbrains.anko.*

/**
 * @date: 2018/11/6.
 * @author: yanglihai
 * @description:
 */
class RAMStatisticsFragment: BaseFragment<RAMStatisticsPresenter>() {
	override val presenter: RAMStatisticsPresenter = RAMStatisticsPresenter(this)
	override val pageTitle: String = ""
	private val globalRAMCard by lazy {
		GrayCardView(context!!).apply {
			layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 110.uiPX())
		}
	}
	private val ramAssetCell by lazy {
		ProgressView(context!!).apply {
			setTitle(EOSRAMExchangeText.ramOccupyRate)
			setSubtitle(CommonText.calculating)
		}
	}
	private val ramBalanceCell by lazy {
		GraySquareCell(context!!).apply {
			setTitle(EOSRAMExchangeText.chainRAMBalance)
			setSubtitle(CommonText.calculating)
		}
	}
	private val ramTotalCell by lazy {
		GraySquareCell(context!!).apply {
			setTitle(EOSRAMExchangeText.chainRAMTotal)
			setSubtitle(CommonText.calculating)
		}
	}
	private val ramOfEOSCell by lazy {
		GraySquareCell(context!!).apply {
			setTitle(EOSRAMExchangeText.eosForRAM)
			setSubtitle(CommonText.calculating)
		}
	}
	
	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			topPadding = 16.uiPX()
			gravity = Gravity.CENTER_HORIZONTAL
			globalRAMCard.apply {
				addView(ramAssetCell)
			}.into(this)
			SessionTitleView(context).setTitle(EOSRAMExchangeText.chainRAMData).into(this)
			ramBalanceCell.into(this)
			ramTotalCell.into(this)
			ramOfEOSCell.into(this)
		}
	}
	
	fun setGlobalRAMData(availableAmount: Float, totalAmount: Float, percent: Float) {
		ramAssetCell.setSubtitle(EOSRAMExchangeText.totalRAM("$totalAmount GB"))
		ramAssetCell.setValues(
			EOSRAMExchangeText.ramAvailable( "$availableAmount GB"),
			"$percent%")
		ramAssetCell.updateProgress(percent / 100f)
		ramTotalCell.setSubtitle("$totalAmount GB")
	}
	
	fun setChainRAMData(ramBalance: String, ramOfEOS: String) {
		ramBalanceCell.setSubtitle("$ramBalance GB")
		ramOfEOSCell.setSubtitle("$ramOfEOS EOS")
	}
	
}