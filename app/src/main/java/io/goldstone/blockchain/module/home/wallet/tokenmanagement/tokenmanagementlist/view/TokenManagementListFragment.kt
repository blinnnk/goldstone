package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.component.title.AttentionTextView
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.presenter.TokenManagementListPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 25/03/2018 5:11 PM
 * @author KaySaith
 */
class TokenManagementListFragment :
	BaseRecyclerFragment<TokenManagementListPresenter, DefaultTokenTable>() {

	override val pageTitle: String = QuotationText.management
	private var attentionView: AttentionTextView? = null
	override val presenter = TokenManagementListPresenter(this)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		supportTokenManagementOrHide()
	}

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<DefaultTokenTable>?
	) {
		recyclerView.adapter = TokenManagementListAdapter(asyncData.orEmptyArray()) {
			switch.onClick {
				tokenSearchModel?.let { model ->
					// 更新内存数据防止上下滑动导致的复用问题
					asyncData?.find { defaultToken ->
						defaultToken.contract.equals(model.contract, true)
					}?.apply {
						isUsed = switch.isChecked
					}
					// 更新数据库
					TokenManagementListPresenter.updateMyTokenInfoBy(switch, model)
				}
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		getMainActivity()?.getWalletDetailFragment()?.presenter?.updateData()
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		super.setBackEvent(mainActivity)
		mainActivity?.backEvent = null
	}

	private fun supportTokenManagementOrHide() {
		if (Config.getCurrentWalletType().isBTCSeries() || Config.getCurrentWalletType().isEOSSeries()) {
			showAttentionView()
			getParentFragment<TokenManagementFragment> {
				overlayView.header.showSearchButton(false)
			}
		}
	}

	private fun showAttentionView() {
		recyclerView.visibility = View.GONE
		if (attentionView.isNull()) {
			attentionView = AttentionTextView(context!!)
			attentionView?.apply {
				isCenter()
				setMargins<LinearLayout.LayoutParams> { topMargin = 80.uiPX() }
				text = AlertText.btcWalletOnly
			}?.into(wrapper)
			attentionView?.setCenterInHorizontal()
		}
	}
}