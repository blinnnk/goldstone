package io.goldstone.blockchain.module.home.dapp.dappcenter.view.recommend

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.view.MotionEvent
import android.widget.LinearLayout
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseDecoration
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable
import org.jetbrains.anko.matchParent


/**
 * @author KaySaith
 * @date  2018/12/01
 */
@SuppressLint("ViewConstructor")
class RecommendDappView(
	context: Context,
	private val clickCellEvent: (url: String) -> Unit
) : BaseRecyclerView(context) {
	init {
		layoutManager = LinearLayoutManager(context).apply {
			orientation = LinearLayoutManager.HORIZONTAL
		}
		layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
		addItemDecoration(BaseDecoration())
		setOnTouchListener { view, event ->
			view.parent.requestDisallowInterceptTouchEvent(true)
			if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
				view.parent.requestDisallowInterceptTouchEvent(false)
			}
			return@setOnTouchListener false
		}
	}

	fun setData(data: ArrayList<DAPPTable>) {
		adapter = RecommendDAPPAdapter(data, clickCellEvent)
	}
}