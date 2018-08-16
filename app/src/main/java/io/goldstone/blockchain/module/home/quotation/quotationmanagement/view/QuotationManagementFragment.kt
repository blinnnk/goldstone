package io.goldstone.blockchain.module.home.quotation.quotationmanagement.view

import android.util.Log
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.language.AlarmClockText
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.CornerSize.cell
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.presenter.QuotationManagementPresenter
import io.goldstone.blockchain.module.home.quotation.quotationoverlay.view.QuotationOverlayFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 21/04/2018 3:58 PM
 * @author KaySaith
 * @rewriteDate 16/08/2018 15:44 PM
 * @rewriter wcx
 * @description 增加showConfirmationAlertView()处理关闭时弹窗提示删除闹铃;onDetach()中加入删除闹铃数据库数据逻辑
 */
class QuotationManagementFragment :
  BaseRecyclerFragment<QuotationManagementPresenter, QuotationSelectionTable>() {

  private var willDeletePair = listOf<String>()
  override val presenter = QuotationManagementPresenter(this)
  override fun setRecyclerViewAdapter(
    recyclerView: BaseRecyclerView,
    asyncData: ArrayList<QuotationSelectionTable>?
  ) {
    recyclerView.adapter = QuotationManagementAdapter(asyncData.orEmptyArray()) { cell ->
      cell.switch.onClick { _ ->
        showConfirmationAlertView(cell)
      }
    }
  }

  private fun showConfirmationAlertView(cell: QuotationManagementCell) {
    if (cell.switch.isChecked) {
      switchClickEventDataProcessing(cell)
    } else {
      context?.showAlertView(
        AlarmClockText.confirmDelete,
        AlarmClockText.confirmDeleteContent,
        false,
        {
          cell.switch.isChecked = !cell.switch.isChecked
        }
      ) {
        switchClickEventDataProcessing(cell)
      }
    }
  }

  private fun switchClickEventDataProcessing(cell: QuotationManagementCell) {
    cell.searchModel?.apply {
      // 更新内存里面的数据防止复用的时候出错
      asyncData?.find { selection ->
        selection.pair.equals(pair, true)
      }?.isSelecting = cell.switch.isChecked
      // 更新标记, 来在页面销毁的时候决定是否集中处理逻辑
      if (cell.switch.isChecked) {
        willDeletePair += pair
      } else {
        willDeletePair.filterNot { it.equals(pair, true) }
      }
    }
  }

  override fun onDetach() {
    super.onDetach()
    asyncData?.filter {
      !it.isSelecting
    }?.apply {
      forEach { pair ->
        QuotationSelectionTable.removeSelectionBy(pair.pair)
        // 删除对应数据库闹铃数据
        PriceAlarmClockTable.getAllPriceAlarm {
          val size = it.size - 1
          for (index: Int in 0..size) {
            if (pair.pairDisplay.equals(it[index].pairDisplay, true) && pair.market.equals(it[index].marketName, true)) {
              PriceAlarmClockTable.deleteAlarm(it[index]) {}
            }
          }
        }
      }
      if (isNotEmpty())
        getMainActivity()?.getQuotationFragment()?.presenter?.updateData()
    }
  }

  override fun onHiddenChanged(hidden: Boolean) {
    super.onHiddenChanged(hidden)
    // 从下一个界面返回的时候更新这个界面的 `UI` 数据
    getParentFragment<QuotationOverlayFragment> {
      if (hidden) {
        overlayView.header.showSearchButton(false)
      } else overlayView.header.showSearchButton(true) {
        presenter.showQutationSearchFragment()
      }
    }
  }
}