package io.goldstone.blockchain.module.home.quotation.quotation.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import io.goldstone.blockchain.module.home.quotation.quotation.presenter.QuotationPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 26/03/2018 8:56 PM
 * @author KaySaith
 */

class QuotationFragment : BaseRecyclerFragment<QuotationPresenter, QuotationModel>() {

	private val slideHeader by lazy { QuotationSlideHeader(context!!) }

	override val presenter = QuotationPresenter(this)

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView, asyncData: ArrayList<QuotationModel>?
	) {
		recyclerView.adapter = QuotationAdapter(asyncData.orEmptyArray()) {
			onClick {
				presenter.showMarketTokenDetailFragment(model.pairDisplay)
				preventDuplicateClicks()
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		wrapper.addView(slideHeader)

		slideHeader.apply {
			addTokenButton.apply {
				onClick {
					presenter.showQuotationManagement()
					preventDuplicateClicks()
				}
			}
		}
	}

	private var isShow = false
	private val headerHeight = 50.uiPX()
	private var totalRange = 0

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

}