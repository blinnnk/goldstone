package io.goldstone.blinnnk.module.home.wallet.tokenmanagement.tokenmanagementlist.view

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blinnnk.common.component.title.AttentionTextView
import io.goldstone.blinnnk.common.language.AlertText
import io.goldstone.blinnnk.common.language.QuotationText
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.common.utils.getMainActivity
import io.goldstone.blinnnk.common.value.UMengEvent
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import io.goldstone.blinnnk.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blinnnk.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blinnnk.module.home.wallet.tokenmanagement.tokenmanagementlist.presenter.TokenManagementListPresenter
import org.jetbrains.anko.sdk27.coroutines.onClick

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

	override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<DefaultTokenTable>?) {
		recyclerView.adapter = TokenManagementListAdapter(asyncData.orEmptyArray()) { default, switch ->
			switch.onClick {
				switch.isClickable = false
				// 更新内存数据防止上下滑动导致的复用问题
				asyncData?.find { data ->
					data.contract.equals(default.contract, true)
				}?.isUsed = switch.isChecked
				// 更新数据库
				TokenManagementListPresenter.addOrCloseMyToken(switch.isChecked, default)
				switch.isClickable = true
				UMengEvent.add(context, UMengEvent.Click.TokenManage.switchButton, UMengEvent.Page.tokenList)
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		getMainActivity()?.getWalletDetailFragment()?.presenter?.start()
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		super.setBackEvent(mainActivity)
		mainActivity?.backEvent = null
	}

	private fun supportTokenManagementOrHide() {
		if (SharedWallet.getCurrentWalletType().isBTCSeries()) {
			showAttentionView()
			getParentFragment<TokenManagementFragment> {
				showSearchButton(false) {}
			}
		}
	}

	private fun showAttentionView() {
		recyclerView.visibility = View.GONE
		if (attentionView == null) {
			attentionView = AttentionTextView(context!!)
			attentionView?.apply {
				isCenter()
				setMargins<LinearLayout.LayoutParams> { topMargin = 80.uiPX() }
				text = AlertText.btcWalletOnly
			}?.into(wrapper)
			attentionView?.centerInHorizontal()
		}
	}
}