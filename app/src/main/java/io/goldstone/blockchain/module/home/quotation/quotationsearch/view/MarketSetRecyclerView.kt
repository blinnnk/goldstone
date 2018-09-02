package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import android.widget.LinearLayout
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import org.jetbrains.anko.*

/**
 * @date: 2018/8/29.
 * @author: yanglihai
 * @description:
 */
class MarketSetRecyclerView(context: Context) : BaseRecyclerView(context) {
	init {
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
	}
}