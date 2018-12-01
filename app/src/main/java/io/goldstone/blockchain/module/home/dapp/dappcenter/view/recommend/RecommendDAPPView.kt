package io.goldstone.blockchain.module.home.dapp.dappcenter.view.recommend

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseDecoration
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPModel
import org.jetbrains.anko.matchParent


/**
 * @author KaySaith
 * @date  2018/12/01
 */
@SuppressLint("ViewConstructor")
class RecommendDappView(
	context: Context,
	private val hold: DAPPModel.() -> Unit
) : BaseRecyclerView(context) {
	init {
		layoutManager = LinearLayoutManager(context).apply {
			orientation = LinearLayoutManager.HORIZONTAL
		}
		layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
		addItemDecoration(BaseDecoration())
	}

	fun setData(data: ArrayList<DAPPModel>) {
		adapter = RecommendDAPPAdapter(data, hold)
	}
}