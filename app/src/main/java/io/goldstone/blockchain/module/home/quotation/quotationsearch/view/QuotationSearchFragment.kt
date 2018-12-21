package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.afollestad.materialdialogs.list.customListAdapter
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.suffix
import com.blinnnk.extension.toArrayList
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.common.component.overlay.LoadingView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.sandbox.SandBoxManager
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.event.PairUpdateEvent
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.contract.QuotationSearchContract
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.presenter.QuotationSearchPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.*
import java.util.*

/**
 * @date 21/04/2018 4:31 PM
 * @author KaySaith
 */

class QuotationSearchFragment : GSRecyclerFragment<QuotationSelectionTable>(), QuotationSearchContract.GSView {

	override val pageTitle: String = "Quotation Search"
	override lateinit var presenter: QuotationSearchContract.GSPresenter
	private val rootFragment by lazy {
		parentFragment as? QuotationOverlayFragment
	}

	private lateinit var filterDescriptionView: TextView

	override fun showLoading(status: Boolean) = launchUI {
		showLoadingView(status)
	}

	override fun showError(error: Throwable) {
		ErrorDisplayManager(error).show(context!!)
	}

	override fun updateUI(data: List<QuotationSelectionTable>) {
		launchUI {
			updateSingleCellAdapterData<QuotationSearchAdapter>(data.toArrayList())
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		EventBus.getDefault().post(PairUpdateEvent(true))
	}

	override fun showFilterDescription(data: List<ExchangeTable>) {
		presenter.getSelectedExchange {
			val selectedExchanges = data.filter { it.isSelected }
			val selectedExchangeNames = selectedExchanges.map { it.exchangeName }
			rootFragment?.resetFilterStatus(isNotEmpty())
			if (isEmpty()) removeExchangeFilterDescriptionView()
			else {
				val content =
					QuotationText.searchFilterTextDescription(
						if (selectedExchangeNames.size > 3) selectedExchangeNames.subList(0, 3).joinToString(",") suffix "etc."
						else selectedExchangeNames.joinToString(",")
					)
				filterDescriptionView.text = content
				filterDescriptionView.visibility = View.VISIBLE
				recyclerView.y = filterDescriptionView.layoutParams.height.toFloat()
			}
		}
	}

	private fun showExchangeDashboard() {
		load {
			ExchangeTable.dao.getAll()
		} then { exchanges ->
			val exchangeAdapter = ExchangeAdapter(exchanges.toArrayList()) { model, isChecked ->
				model.isSelected = isChecked
			}
			MaterialDialog(context!!)
				.title(text = QuotationText.exchangeList)
				.customListAdapter(exchangeAdapter)
				.checkBoxPrompt(text = QuotationText.selectAll) {
					exchanges.forEach { model ->
						model.isSelected = it
					}
					exchangeAdapter.notifyDataSetChanged()
				}
				.positiveButton(text = CommonText.confirm) { dialog ->
					GlobalScope.launch(Dispatchers.Default) {
						ExchangeTable.dao.insertAll(exchanges)
						showFilterDescription(exchanges)
					}
					val selectedIDs = exchanges.filter { exchange ->
						exchange.isSelected
					}.map { exchange ->
						exchange.marketId
					}
					presenter.updateSelectedExchangeID(selectedIDs)
					updateResultAfterConditionChanged()
					SandBoxManager.updateMyExchanges(selectedIDs)
					dialog.dismiss()
				}
				.negativeButton(text = CommonText.cancel) {
					it.dismiss()
				}
				.show()
		}
	}

	private fun updateResultAfterConditionChanged() {
		val textForSearch = rootFragment?.getSearchContent().orEmpty()
		if (NetworkUtil.hasNetworkWithAlert(context)) {
			presenter.searchToken(textForSearch)
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		asyncData = arrayListOf()
		presenter = QuotationSearchPresenter(this)
		presenter.start()
		initExchangeFDescriptionView()
		rootFragment?.showFilterImage(true)
		rootFragment?.searchInputListener {
			if (NetworkUtil.hasNetwork()) presenter.searchToken(it)
		}
		rootFragment?.setFilterEvent {
			showExchangeDashboard()
		}
	}

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<QuotationSelectionTable>?
	) {
		val loadingView = LoadingView(context!!)
		recyclerView.adapter = QuotationSearchAdapter(asyncData.orEmptyArray()) { model, isChecked ->
			loadingView.show()
			presenter.updateLocalQuotation(model, isChecked) { error ->
				// 更新内存里面的数据, 防止回收后 `UI` 不对
				asyncData?.find { it.pair.equals(model.pair, true) }?.isSelecting = isChecked
				launchUI {
					loadingView.remove()
					if (error.hasError()) safeShowError(error)
				}
			}
		}
	}

	override fun recoveryBackEvent() {
		getMainActivity()?.apply {
			setBackEvent(this)
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		getParentFragment<QuotationOverlayFragment> {
			presenter.popFragmentFrom<QuotationSearchFragment>()
			showSearchInput(false, {}) {}
		}
	}

	private fun removeExchangeFilterDescriptionView() {
		filterDescriptionView.visibility = View.GONE
		recyclerView.y = 0f
	}

	private fun initExchangeFDescriptionView() {
		filterDescriptionView = TextView(context).apply {
			textSize = fontSize(12)
			layoutParams = LinearLayout.LayoutParams(matchParent, 45.uiPX())
			backgroundColor = GrayScale.whiteGray
			leftPadding = 10.uiPX()
			textColor = GrayScale.black
			gravity = Gravity.CENTER_VERTICAL
			typeface = GoldStoneFont.heavy(context)
			singleLine = true
			visibility = View.GONE
		}
		wrapper.addView(filterDescriptionView, 0)
	}

}