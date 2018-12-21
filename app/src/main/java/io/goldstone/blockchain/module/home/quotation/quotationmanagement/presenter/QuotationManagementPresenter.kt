package io.goldstone.blockchain.module.home.quotation.quotationmanagement.presenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.sandbox.SandBoxManager
import io.goldstone.blockchain.common.thread.launchDefault
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.crypto.utils.getObjectMD5HexString
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.event.QuotationUpdateEvent
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.view.QuotationManagementAdapter
import io.goldstone.blockchain.module.home.quotation.quotationmanagement.view.QuotationManagementFragment
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

/**
 * @date 21/04/2018 3:58 PM
 * @author KaySaith
 */
class QuotationManagementPresenter(
	override val fragment: QuotationManagementFragment
) : BaseRecyclerPresenter<QuotationManagementFragment, QuotationSelectionTable>() {

	private var initDataMD5: String? = null

	override fun updateData() {
		GlobalScope.launch(Dispatchers.Default) {
			val selections =
				QuotationSelectionTable.dao.getAllByOrderID().toArrayList()
			if (initDataMD5.isNull()) {
				initDataMD5 = selections.map {
					Pair(it.pair, it.isSelecting)
				}.toString().getObjectMD5HexString()
			}
			launchUI {
				if (fragment.asyncData.isNull()) fragment.asyncData = selections
				else diffAndUpdateSingleCellAdapterData<QuotationManagementAdapter>(selections)
			}
		}
	}

	override fun afterUpdateAdapterDataSet(recyclerView: BaseRecyclerView) {
		fragment.updateSelectionOrderID()
	}

	/**
	 *  比对管理的结果和初始进入的值是否一样, 如果一样才通知 Quotation 更新数据
	 *  因为 Quotation 包括数据更新, 和长链接重请求. 一方面是用户体验看到的闪烁
	 *  一方面是性能. 所以这里做了一次 `MD5` 比对. 确认数据变化后才执行.
	 */
	fun notifyQuotationDataChanged() {
		val new = fragment.asyncData?.map {
			Pair(it.pair, it.isSelecting)
		}.toString().getObjectMD5HexString()
		if (new != initDataMD5) {
			EventBus.getDefault().post(QuotationUpdateEvent(true))
		}
	}

	fun updateQuotationDataChanged(callback: () -> Unit) {
		GlobalScope.launch(Dispatchers.Default) {
			val turnOffData =
				fragment.asyncData?.filter { !it.isSelecting }
			if (!turnOffData.isNullOrEmpty()) {
				QuotationSelectionTable.dao.deleteAll(turnOffData)
				callback()
			} else callback()
		}
	}

	fun updateSandboxPairs() = launchDefault {
		fragment.asyncData?.filter { it.isSelecting }?.map { it.pair }?.let {
			SandBoxManager.updateQuotationPairs(it)
		}
	}


	private fun QuotationManagementFragment.updateSelectionOrderID() {
		val data = fragment.asyncData.orEmptyArray()
		recyclerView.addDragEventAndReordering(data) { _, toPosition ->
			// 通过权重判断简单的实现了排序效果
			val newOrderID = when (toPosition) {
				0 -> data[toPosition + 1].orderID + 0.1
				data.lastIndex -> data[toPosition - 1].orderID - 0.1
				else -> (data[toPosition - 1].orderID + data[toPosition + 1].orderID) / 2.0
			}
			QuotationSelectionTable.updateSelectionOrderIDBy(data[toPosition].pair, newOrderID) {
				// 更新完数据库后也需要同时更新一下缓存的数据, 解决用户一次更新多个缓存数据排序的情况
				fragment.asyncData?.find {
					it.baseSymbol == data[toPosition].baseSymbol
				}?.orderID = newOrderID
			}
		}
	}
}