package io.goldstone.blockchain.module.home.dapp.dappcenter.view.applist

import android.annotation.SuppressLint
import android.content.Context
import android.widget.LinearLayout
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPModel
import org.jetbrains.anko.matchParent


/**
 * @author KaySaith
 * @date  2018/12/02
 */
@SuppressLint("ViewConstructor")
class DAPPRecyclerView(
	context: Context,
	private val hold: DAPPModel.() -> Unit
) : BaseRecyclerView(context) {

	init {
		layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
	}

	fun setData(data: ArrayList<DAPPModel>) {
		adapter = DAPPAdapter(data, hold)
	}
}