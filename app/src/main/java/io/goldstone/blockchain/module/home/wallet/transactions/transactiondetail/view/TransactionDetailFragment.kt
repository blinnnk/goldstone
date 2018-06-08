package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.value.TransactionText
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
	
	override val presenter = TransactionDetailPresenter(this)
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView, asyncData: ArrayList<TransactionDetailModel>?
	) {
		recyclerView.adapter = TransactionDetailAdapter(asyncData.orEmptyArray()) {
			onClick {
				if (model.description == TransactionText.url) {
					presenter.showEtherScanTransactionFragment()
				} else {
					this@TransactionDetailFragment.context?.clickToCopy(model.info)
				}
				preventDuplicateClicks()
			}
		}
	}
	
	override fun setBackEvent(mainActivity: MainActivity?) {
		parentFragment?.let { presenter.runBackEventBy(it) }
	}
}