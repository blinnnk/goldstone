package io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecordandlargesinglerecord.view

import android.support.v4.app.Fragment
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setMargins
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.ViewPagerMenu
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.TraderMemoryDetailOverlay.view.TraderMemoryOverlayFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecordandlargesinglerecord.presenter.TraderMemorySalesRecordAndLargeSingleRecordPresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.onPageChangeListener

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */

class TraderMemorySalesRecordAndLargeSingleRecordFragment
	: BaseFragment<TraderMemorySalesRecordAndLargeSingleRecordPresenter>() {

	// 这个 `Model` 是服务 `ViewPager` 中的 `TraderMemorySalesRecordFragment`
	val currencyInfo by lazy {
		arguments?.getSerializable(ArgumentKey.quotationCurrencyDetail) as? QuotationModel
	}

	private val menuBar by lazy {
		ViewPagerMenu(context!!)
	}
	private val viewPager by lazy {
		TraderMemorySalesRecordAndLargeSingleRecordViewPager(this)
	}
	private val menuTitles =
		arrayListOf("买卖记录", "大单记录")

	override val presenter = TraderMemorySalesRecordAndLargeSingleRecordPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		relativeLayout {
			lparams(matchParent, matchParent)
			menuBar.into(this)
			addView(viewPager, RelativeLayout.LayoutParams(ScreenSize.heightWithOutHeader, matchParent))
			viewPager.apply {
				// `MenuBar` 点击选中动画和内容更换
				menuBar.setMemnuTitles(menuTitles) { button, id ->
					button.onClick {
						currentItem = id
						menuBar.moveUnderLine(menuBar.getUnitWidth() * currentItem)
						button.preventDuplicateClicks()
					}
				}
				setMargins<RelativeLayout.LayoutParams> {
					topMargin = menuBar.layoutParams.height
				}

				// `MenuBar` 滑动选中动画
				onPageChangeListener {
					onPageScrolled { position, percent, _ ->
						menuBar.moveUnderLine(menuBar.getUnitWidth() * (percent + position))
					}
				}
			}
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		TraderMemoryOverlayFragment.removeContentOverlayOrElse(this) {
			super.setBaseBackEvent(activity, parent)
		}
	}

}