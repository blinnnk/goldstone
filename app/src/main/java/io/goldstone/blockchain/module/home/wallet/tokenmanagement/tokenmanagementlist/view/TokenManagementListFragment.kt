package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view

import android.view.View
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.component.AttentionTextView
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.AlertText
import io.goldstone.blockchain.common.value.ChainID
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.presenter.TokenManagementListPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 25/03/2018 5:11 PM
 * @author KaySaith
 */
class TokenManagementListFragment :
	BaseRecyclerFragment<TokenManagementListPresenter, DefaultTokenTable>() {
	
	private var attentionView: AttentionTextView? = null
	override val presenter = TokenManagementListPresenter(this)
	private var hasInsertToken = false
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<DefaultTokenTable>?
	) {
		recyclerView.adapter = TokenManagementListAdapter(asyncData.orEmptyArray()) {
			switch.onClick {
				hasInsertToken = true
				model?.let { model ->
					// 更新内存数据防止上下滑动导致的复用问题
					asyncData?.find {
						it.contract.equals(model.contract, true)
					}?.apply {
						isUsed = switch.isChecked
					}
					// 更新数据库
					this@TokenManagementListFragment.context?.apply {
						TokenManagementListPresenter.updateMyTokensInfoBy(
							switch,
							model,
							ChainID.getChainIDBySymbol(model.symbol),
							this
						)
					}
				}
			}
		}
	}
	
	override fun onDestroy() {
		super.onDestroy()
		if (hasInsertToken) updateWalletDetailData()
	}
	
	override fun setBackEvent(mainActivity: MainActivity?) {
		super.setBackEvent(mainActivity)
		mainActivity?.backEvent = null
	}
	
	private fun updateWalletDetailData() {
		getMainActivity()?.getWalletDetailFragment()?.presenter?.updateData()
	}
	
	fun showAttentionView() {
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