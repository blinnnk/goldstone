package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramstatistics.view

import android.support.v4.app.Fragment
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.view.GrayCardView
import io.goldstone.blockchain.common.component.ProgressView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramstatistics.presenter.RAMStatisticsPresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.verticalLayout
import java.math.BigInteger

/**
 * @date: 2018/11/6.
 * @author: yanglihai
 * @description:
 */
class RAMStatisticsFragment: BaseFragment<RAMStatisticsPresenter>() {
	
	override val presenter: RAMStatisticsPresenter = RAMStatisticsPresenter(this)
	override val pageTitle: String = ""
	val globalRAMCard by lazy {
		GrayCardView(context!!).apply {
			layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, 255.uiPX())
		}
	}
	private val ramAssetCell by lazy {
		ProgressView(context!!).apply {
			setTitle(EOSRAMExchangeText.ramOccupyRate)
			setSubtitle(CommonText.calculating)
		}
	}
	
	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			globalRAMCard.apply {
				addView(ramAssetCell)
			}.into(this)
		}
	}
	
	fun setGlobalRAMUI(occupyAmount: Float, totalAmount: Float, percent: Float) {
		ramAssetCell.setSubtitle(EOSRAMExchangeText.ramTotalAmount(totalAmount.toString()))
		ramAssetCell.setValues(
			EOSRAMExchangeText.ramAvailable( "$occupyAmount GB"),
			"$percent%")
	}
	
}