package io.goldstone.blockchain.module.common.tokendetail.tokendetail.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.into
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setAlignParentBottom
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter.TokenDetailPresenter
import io.goldstone.blockchain.module.common.tokendetail.tokendetailcenter.view.TokenDetailCenterFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 27/03/2018 3:20 PM
 * @author KaySaith
 */
class TokenDetailFragment : BaseRecyclerFragment<TokenDetailPresenter, TransactionListModel>() {
	// 首页的 `cell` 点击进入详情界面传入的 `Symbol`
	val token by lazy {
		(parentFragment as? TokenDetailCenterFragment)?.token
	}

	private val footer by lazy {
		TokenDetailFooter(context!!)
	}
	override val presenter = TokenDetailPresenter(this)

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<TransactionListModel>?
	) {
		recyclerView.adapter = TokenDetailAdapter(asyncData.orEmptyArray(), {
			onClick {
				model?.apply {
					presenter.showTransactionDetailFragment(this)
					preventDuplicateClicks()
				}
			}
		}) {
			menu.getButton { button ->
				button.onClick {
					when (button.text) {
						CommonText.all -> presenter.showAllData()
						CommonText.deposit -> presenter.showOnlyReceiveData()
						CommonText.send -> presenter.showOnlySendData()
						CommonText.failed -> presenter.showOnlyFailedData()
					}
					menu.selected(button.id)
					button.preventDuplicateClicks()
				}
			}
		}
	}

	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(view, savedInstanceState)
		footer.into(wrapper)
		footer.apply {
			setAlignParentBottom()
			sendButton.onClick {
				presenter.showAddressSelectionFragment()
			}
			receivedButton.onClick {
				presenter.showDepositFragment()
			}
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		super.setBackEvent(mainActivity)
		mainActivity?.backEvent = null
	}
}