package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter.TransactionDetailPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 27/03/2018 3:26 AM
 * @author KaySaith
 */
class TransactionDetailFragment :
	BaseRecyclerFragment<TransactionDetailPresenter, TransactionDetailModel>() {

	override val pageTitle: String = TransactionText.detail
	override val presenter = TransactionDetailPresenter(this)

	override fun setRecyclerViewLayoutManager(recyclerView: BaseRecyclerView) {
		super.setRecyclerViewLayoutManager(recyclerView)
		recyclerView.layoutParams
	}

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<TransactionDetailModel>?
	) {
		recyclerView.adapter = TransactionDetailAdapter(asyncData.orEmptyArray()) cell@{
			if (
				model.description.equals(CommonText.from, true)
				|| model.description.equals(CommonText.to, true)
			) {
				presenter.showAddContactsButton(this)
			}

			onClick {
				if (model.description.equals(TransactionText.url, true)) {
					presenter.showTransactionWebFragment()
				} else {
					this@cell.context?.clickToCopy(model.info)
				}
				preventDuplicateClicks()
			}
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		parentFragment?.let {
			presenter.runBackEventBy(it)
		}
	}
}