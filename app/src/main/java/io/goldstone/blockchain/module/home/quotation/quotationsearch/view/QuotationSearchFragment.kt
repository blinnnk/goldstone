package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.component.overlay.ContentScrollOverlayView
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.presenter.QuotationSearchPresenter
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

/**
 * @date 21/04/2018 4:31 PM
 * @author KaySaith
 */

class QuotationSearchFragment :
	BaseRecyclerFragment<QuotationSearchPresenter, QuotationSelectionTable>() {
	
	private val exchangeFilterDescriptionView by lazy {
		TextView(context).apply {
			layoutParams = LinearLayout.LayoutParams(matchParent, 45.uiPX())
			backgroundColor = GrayScale.lightGray
			leftPadding = 10.uiPX()
			textColor = GrayScale.black
			gravity = Gravity.CENTER_VERTICAL
			singleLine = true
			visibility = View.GONE
		}
	}
	override val presenter = QuotationSearchPresenter(this)
	
	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(view, savedInstanceState)
		wrapper.addView(exchangeFilterDescriptionView,0)
	}
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<QuotationSelectionTable>?
	) {
		recyclerView.adapter = QuotationSearchAdapter(asyncData.orEmptyArray()) { cell ->
			cell.searchModel?.let { model ->
				cell.switch.onClick {
					getMainActivity()?.showLoadingView()
					presenter.setQuotationSelfSelection(model, cell.switch.isChecked) {
						getMainActivity()?.removeLoadingView()
					}
				}
			}
		}
	}
	
	override fun recoveryBackEvent() {
		getMainActivity()?.apply {
			backEvent = Runnable {
				val overlayView = findViewById<ContentScrollOverlayView>(ElementID.contentScrollview)
				if (overlayView.isNull()) {
					setBackEvent(this)
				}else {
					overlayView.remove()
				}
			}
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		getParentFragment<QuotationOverlayFragment> {
			headerTitle = QuotationText.management
			presenter.popFragmentFrom<QuotationSearchFragment>()
			overlayView.header.showSearchInput(false)
		}
	}
	
	fun showExchangeFilterDescriptionView(content: String){
		exchangeFilterDescriptionView.text = content
		exchangeFilterDescriptionView.visibility = View.VISIBLE
		recyclerView.y = exchangeFilterDescriptionView.layoutParams.height.toFloat()
	}
	
	fun removeExchangeFilterDescriptionView() {
		exchangeFilterDescriptionView.visibility = View.GONE
		if (getLoadingView().visibility != View.VISIBLE) {
			recyclerView.y = 0f
		}
	}
	
	
	override fun removeLoadingView() {
		getLoadingView().visibility = View.GONE
		if (exchangeFilterDescriptionView.visibility != View.VISIBLE) {
			recyclerView.y = 0f
		}
	}
	
}