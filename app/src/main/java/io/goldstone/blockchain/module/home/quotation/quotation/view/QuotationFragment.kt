package io.goldstone.blockchain.module.home.quotation.quotation.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotation.presenter.QuotationPresenter
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.event.QuotationUpdateEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 26/03/2018 8:56 PM
 * @author KaySaith
 */
class QuotationFragment : BaseRecyclerFragment<QuotationPresenter, QuotationModel>() {

	override val pageTitle: String = "Quotation"
	private val slideHeader by lazy { QuotationSlideHeader(context!!) }
	override val presenter = QuotationPresenter(this)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		EventBus.getDefault().register(this)
	}

	// 这个页面是常驻在首页通过 `ViewPager` 管理显示的,
	// 所以在 `Hidden` 的时候注销 `EventBus`
	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		if (isHidden) EventBus.getDefault().unregister(this)
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	fun updateQuotationListEvent(updateEvent: QuotationUpdateEvent) {
		if (updateEvent.needUpdate) presenter.updateData()
	}

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<QuotationModel>?
	) {
		recyclerView.adapter = QuotationAdapter(asyncData.orEmptyArray()) {
			setClickEvent {
				presenter.showMarketTokenDetailFragment(model)
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		wrapper.addView(slideHeader)
		slideHeader.addTokenButton.apply {
			onClick {
				presenter.showQuotationManagement()
				preventDuplicateClicks()
			}
		}
	}

	override fun emptyClickEvent() {
		presenter.showQuotationManagement()
	}

	private var isShow = false
	private val headerHeight = 50.uiPX()

	override fun observingRecyclerViewVerticalOffset(offset: Int, range: Int) {
		if (offset >= headerHeight && !isShow) {
			slideHeader.onHeaderShowedStyle()
			isShow = true
		}
		if (offset < headerHeight && isShow) {
			slideHeader.onHeaderHidesStyle()
			isShow = false
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		mainActivity?.getHomeFragment()?.presenter?.showWalletDetailFragment()
	}
	
}